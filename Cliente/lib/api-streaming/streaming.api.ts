/* eslint-disable @typescript-eslint/no-require-imports */
import audioStreamingClient from "./client";

const {
  PeticionStreamDTO,
  CancionDTO,
} = require("./stub/serviciosStreaming_pb");

const AudioStreamingAPI = {
  getAudioStreamTest() {
    const request = new PeticionStreamDTO();
    console.log(request);
    // Set the fields using the setter methods
    request.setIdusuario(123);

    // Create and set the CancionDTO
    const cancion = new CancionDTO();
    cancion.setId(1);
    cancion.setTitulo("Song Title");
    cancion.setAutor("Artist Name");
    cancion.setAlbum("Album Name");
    cancion.setAniolanzamiento(2023);
    cancion.setDuracions(180);
    cancion.setGenero("Pop");
    cancion.setIdioma("English");
    cancion.setRutaalmacenamiento("../canciones/cancion1.mp3");

    request.setCancion(cancion)

    return audioStreamingClient.stremearCancion(request, {});
  },

  getAudioStream(req: AudioStreamingAPI.PeticionStreamDTO) {
    const request = new PeticionStreamDTO();
    // Set the fields using the setter methods
    request.setIdusuario(req.idUsuario);

    // Create and set the CancionDTO
    const cancion = new CancionDTO();
    cancion.setId(req.cancion.id);
    cancion.setTitulo(req.cancion.titulo);
    cancion.setAutor(req.cancion.autor);
    cancion.setAlbum(req.cancion.album);
    cancion.setAniolanzamiento(req.cancion.anioLanzamiento);
    cancion.setDuracions(req.cancion.duracionS);
    cancion.setGenero(req.cancion.genero);
    cancion.setIdioma(req.cancion.idioma);
    cancion.setRutaalmacenamiento(req.cancion.rutaAlmacenamiento);

    request.setCancion(cancion)
    return audioStreamingClient.stremearCancion(request, {});
  }
};

declare global {
  namespace AudioStreamingAPI {
    type PeticionStreamDTO = {
      idUsuario: number,
      cancion: CancionDTO
    }

    type CancionDTO = {
      id: number,
      titulo: string,
      autor: string,
      album: string,
      anioLanzamiento: number,
      duracionS: number,
      genero: string,
      idioma: string,
      rutaAlmacenamiento: string
    }
  }
}

export default AudioStreamingAPI;
