package componnteconexioncola

import (
	"almacenamiento/config"
	"encoding/json"
	"fmt"
	"log"
	"sync"
	"time"

	"github.com/streadway/amqp"
)

// Constante para el nombre de la cola
const queueName = "notificaciones_canciones"

type RabbitPublisher struct {
	conn    *amqp.Connection
	channel *amqp.Channel
	queue   amqp.Queue

	// Mutex para proteger el acceso concurrente a conn y channel
	mu sync.RWMutex

	// Campos necesarios para la reconexión
	rabbitMQURL    string
	reconnectDelay time.Duration
	closeChan      chan struct{} // Canal para señalar el cierre
}

// Estructura del mensaje que enviará a RabbitMQ
type NotificacionCancion struct {
	Titulo  string `json:"titulo"`
	Artista string `json:"artista"`
	Genero  string `json:"genero"`
	Idioma  string `json:"idioma"`
	Mensaje string `json:"mensaje"`
}

// Crear conexión a RabbitMQ
func NewRabbitPublisher(cfg *config.Config) *RabbitPublisher {
	p := &RabbitPublisher{
		rabbitMQURL:    cfg.RabbitMQURL,
		reconnectDelay: 5 * time.Second, // Reintentar cada 5 segundos
		closeChan:      make(chan struct{}),
		mu:             sync.RWMutex{},
	}

	// Inicia el bucle de conexión en segundo plano
	go p.startReconnectionLoop()

	return p
}

// startReconnectionLoop es el bucle principal que intenta conectarse y reconectarse.
func (p *RabbitPublisher) startReconnectionLoop() {
	log.Println("Iniciando bucle de conexión de RabbitMQ...")

	for {
		select {
		case <-p.closeChan:
			// Se recibió señal de cierre, salir del bucle
			log.Println("Cerrando bucle de reconexión de RabbitMQ.")
			return
		default:
			// Intenta conectar
			err := p.connect()
			if err != nil {
				log.Printf("Error al conectar a RabbitMQ: %v. Reintentando en %v...", err, p.reconnectDelay)

				// Esperar antes de reintentar, pero también escuchar el cierre
				select {
				case <-time.After(p.reconnectDelay):
					continue
				case <-p.closeChan:
					log.Println("Cerrando bucle de reconexión de RabbitMQ mientras se esperaba.")
					return
				}
			}

			// Si connect() retorna sin error, significa que se desconectó
			// El bucle for se reiniciará e intentará reconectar.
			log.Println("RabbitMQ desconectado. Intentando reconectar...")
		}
	}
}

// connect maneja un ciclo de vida de conexión: conecta, y luego bloquea hasta que se desconecta.
func (p *RabbitPublisher) connect() error {
	// Intenta conectarse
	conn, err := amqp.Dial(p.rabbitMQURL)
	if err != nil {
		return fmt.Errorf("error conectando a RabbitMQ: %v", err)
	}

	// Intenta abrir un canal
	ch, err := conn.Channel()
	if err != nil {
		conn.Close() // Cerrar conexión si falla el canal
		return fmt.Errorf("error abriendo canal: %v", err)
	}

	// Intenta declarar la cola
	q, err := ch.QueueDeclare(
		queueName, // nombre de la cola
		true,      // durable
		false,     // autodelete
		false,     // exclusive
		false,     // no-wait
		nil,       // args
	)
	if err != nil {
		ch.Close()
		conn.Close()
		return fmt.Errorf("error declarando cola: %v", err)
	}

	// === Conexión exitosa ===
	log.Println("Conectado exitosamente a RabbitMQ y cola declarada.")

	// Almacenar la conexión y el canal usando el Mutex
	p.mu.Lock()
	p.conn = conn
	p.channel = ch
	p.queue = q
	p.mu.Unlock()

	// Canales para escuchar eventos de cierre
	notifyCloseConn := conn.NotifyClose(make(chan *amqp.Error))
	notifyCloseChan := ch.NotifyClose(make(chan *amqp.Error))

	// Bloquear hasta que se reciba una señal de cierre (desconexión)
	// o una señal de cierre manual (p.closeChan)
	select {
	case err := <-notifyCloseConn:
		log.Printf("Conexión de RabbitMQ cerrada: %v", err)
	case err := <-notifyCloseChan:
		log.Printf("Canal de RabbitMQ cerrado: %v", err)
	case <-p.closeChan:
		log.Println("Cerrando conexión de RabbitMQ por solicitud del usuario.")
	}

	// Limpiar la conexión y el canal
	p.cleanupConnection()
	return fmt.Errorf("conexión perdida") // Retorna error para que el bucle exterior reintente
}

// cleanupConnection cierra y limpia la conexión y el canal de forma segura
func (p *RabbitPublisher) cleanupConnection() {
	p.mu.Lock()
	defer p.mu.Unlock()

	// Intentar cerrar (puede fallar si ya está cerrado, por eso ignoramos errores)
	if p.channel != nil {
		_ = p.channel.Close()
		p.channel = nil
	}
	if p.conn != nil {
		_ = p.conn.Close()
		p.conn = nil
	}
}

func (p *RabbitPublisher) PublicarNotificacion(msg NotificacionCancion) error {
	body, err := json.Marshal(msg)
	if err != nil {
		return fmt.Errorf("error convirtiendo mensaje a JSON: %v", err)
	}

	// Usar RLock para permitir múltiples publicadores concurrentes
	p.mu.RLock()
	defer p.mu.RUnlock()

	// Comprobar si estamos conectados antes de publicar
	if p.channel == nil {
		return fmt.Errorf("RabbitMQ no está conectado. Mensaje descartado: %s", string(body))
	}

	err = p.channel.Publish(
		"",           // exchange
		p.queue.Name, // routing key (nombre de la cola)
		false,
		false,
		amqp.Publishing{
			ContentType: "application/json",
			Body:        body,
		},
	)
	if err != nil {
		// El error aquí podría ser porque la conexión se cayó justo ahora
		return fmt.Errorf("error publicando mensaje: %v", err)
	}

	log.Println("Notificación enviada a RabbitMQ:", string(body))
	return nil
}

// Cerrar detiene el bucle de reconexión y cierra la conexión actual.
func (p *RabbitPublisher) Cerrar() {
	log.Println("Solicitando cierre de RabbitMQ publisher...")
	// Señal para que la goroutine de reconexión se detenga
	close(p.closeChan)

	// Limpia la conexión y el canal actuales
	p.cleanupConnection()
}