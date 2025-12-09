# lsd_requerimiento_final

Sistema distribuido para el consumo de una aplicación de almacenamiento y reproduccion de canciones

# Correr los servicios web

los servicios web estan configurados para ser usados con docker, para correrlos, se debe usar

```bash
docker compose -f docker-compose.dev.yml up
```
Para la tercera entrega se corre
```bash
docker compose -f docker--compose.3.yaml up
```

# MicroServicios web
- Servidor de Canciones
- Servidor de Envio de correos
- servidor de Preferencias
- Servidor de Reproducciones
- Servidor de Streaming
- Servidor de Reacciones
- Servidor de Pagos

# Aplicaciones Cliente
* ClienteConsola: Es el cliente usado en la segunda entrega, no tiene disponible el sistema de reacciones
* ClienteFlutter: Es el cliente usado en la tercera entrega, no tiene disponible el sistema de preferencias
* ClienteKotlin: Es una prueba de cliente para reproducir una canción en kotlin
* ClienteMobile: Es un intento de cliente de React Native (Se planea eliminar)

# Archivos docker
- `docker-compose.3.yaml`: es el conglomerado de contenedores para lanzar en la entrega 3
- `docker-compose.dev.yml`: es todo el conglomerado de todos los contenedores en el desarrollo


