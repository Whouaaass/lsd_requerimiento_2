import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/metadato_cancion_dto.dart';

class CancionesAPIClient {
  final String baseURL;
  final http.Client httpClient;

  CancionesAPIClient({required this.baseURL, http.Client? client})
    : httpClient = client ?? http.Client();

  Future<List<MetadatoCancionDTO>> listarCanciones() async {
    final url = Uri.parse('$baseURL/canciones/listar');

    try {
      final response = await httpClient.get(
        url,
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode != 200) {
        throw Exception(
          'Unexpected status code: ${response.statusCode}, response: ${response.body}',
        );
      }

      final List<dynamic> body = jsonDecode(response.body);
      print(body[0]);
      return body
          .map((dynamic item) => MetadatoCancionDTO.fromJson(item))
          .toList();
    } catch (e) {
      throw Exception('Failed to execute request: $e');
    }
  }
}
