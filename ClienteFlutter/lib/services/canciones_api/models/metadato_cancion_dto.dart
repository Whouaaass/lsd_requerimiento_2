class MetadatoCancionDTO {
  final int id;
  final String titulo;
  final String? genero;
  final String artista;
  final String idioma;
  final String rutaAlmacenamiento;
  final double duracion;

  MetadatoCancionDTO({
    required this.titulo,
    required this.artista,
    required this.genero,
    required this.idioma,
    required this.rutaAlmacenamiento,
    required this.id,
    required this.duracion,
  });

  factory MetadatoCancionDTO.fromJson(Map<String, dynamic> json) {
    return MetadatoCancionDTO(
      titulo: json['titulo'] as String,
      artista: json['artista'] as String,
      genero: json['genero'] as String,
      idioma: json['idioma'] as String,
      rutaAlmacenamiento: json['ruta-almacenamiento'] as String,
      id: json['id'] as int,
      duracion: json['duracion'] as double,
    );
  }
}
