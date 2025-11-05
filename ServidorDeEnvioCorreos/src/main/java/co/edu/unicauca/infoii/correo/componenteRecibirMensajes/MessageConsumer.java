package co.edu.unicauca.infoii.correo.componenteRecibirMensajes;

import org.springframework.stereotype.Service;

import co.edu.unicauca.infoii.correo.DTOs.CancionAlmacenarDTOInput;
import co.edu.unicauca.infoii.correo.commons.Simulacion;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

//
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



@Service
public class MessageConsumer {
    @RabbitListener(queues = "notificaciones_canciones")
    public void receiveMessage(CancionAlmacenarDTOInput objClienteCreado) {
        System.out.println("Datos de la cancion recibidos");
        System.out.println("Enviando el correo electronico");
        Simulacion.simular(10000, "Enviando correo");
        System.out.println("Correo enviado al cliente con los siguientes datos: ");
        System.out.println("Titulo: " + objClienteCreado.getTitulo());
        System.out.println("Artista: " + objClienteCreado.getArtista());
        System.out.println("Genero: " + objClienteCreado.getGenero());
        System.out.println("Idioma: " + objClienteCreado.getIdioma());
       
        
        //fecha
        LocalDateTime fechaActual = LocalDateTime.now();
        String fechaFormateada = fechaActual.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        System.out.println("Fecha y hora de registro: "+fechaFormateada);

        //frase
        String frase= obtenerFrase();
        System.out.println("Frase motivadora: "+frase);
    }
    
    //
    private String obtenerFrase() {
    String[] frases = {
        "!Shhh¡ pon atencion",
        "Buena para llorar",
        "Subale volumeeeen",
        "Temazooo",
        "Bailalo, Gozalo"
    
    };
    int indice = (int) (Math.random() * frases.length);
    return frases[indice];
}

}
    