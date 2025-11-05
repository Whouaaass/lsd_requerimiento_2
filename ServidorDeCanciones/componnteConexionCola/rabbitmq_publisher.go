package componnteconexioncola

import (
	"encoding/json"
	"fmt"

	"github.com/streadway/amqp"
)

type RabbitPublisher struct {
	conn    *amqp.Connection
	channel *amqp.Channel
	queue   amqp.Queue
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
func NewRabbitPublisher() (*RabbitPublisher, error) {
	//conn, err := amqp.Dial("amqp://admin:1234@10.132.53.6:5672/")
	conn, err := amqp.Dial("amqp://admin:1234@localhost:5672/")

	if err != nil {
		return nil, fmt.Errorf("error conectando a RabbitMQ: %v", err)
	}

	ch, err := conn.Channel()
	if err != nil {
		return nil, fmt.Errorf("error abriendo canal: %v", err)
	}

	q, err := ch.QueueDeclare(
		"notificaciones_canciones", // nombre de la cola
		true,                       // durable
		false,                      // autodelete
		false,                      // exclusive
		false,                      // no-wait
		nil,                        // args
	)
	if err != nil {
		return nil, fmt.Errorf("error declarando cola: %v", err)
	}

	return &RabbitPublisher{
		conn:    conn,
		channel: ch,
		queue:   q,
	}, nil
}

func (p *RabbitPublisher) PublicarNotificacion(msg NotificacionCancion) error {
	body, err := json.Marshal(msg)
	if err != nil {
		return fmt.Errorf("error convirtiendo mensaje a JSON: %v", err)
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
		return fmt.Errorf("error publicando mensaje: %v", err)
	}

	fmt.Println("Notificación enviada a RabbitMQ:", string(body))
	return nil
}

func (p *RabbitPublisher) Cerrar() {
	p.channel.Close()
	p.conn.Close()
}
