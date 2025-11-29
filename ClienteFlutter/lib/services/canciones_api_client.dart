import 'package:dio/dio.dart';
import '../models/metadato_cancion_dto.dart';

class CancionesAPIClient {
  final String baseURL;
  final Dio _dio;

  CancionesAPIClient({required this.baseURL, Dio? dio})
    : _dio = dio ?? Dio(BaseOptions(baseUrl: baseURL));

  Future<List<MetadatoCancionDTO>> listarCanciones() async {
    try {
      final response = await _dio.get('/canciones/listar');

      if (response.statusCode != 200) {
        throw Exception(
          'Unexpected status code: ${response.statusCode}, response: ${response.data}',
        );
      }

      final List<dynamic> body = response.data;
      return body
          .map((dynamic item) => MetadatoCancionDTO.fromJson(item))
          .toList();
    } catch (e) {
      throw Exception('Failed to execute request: $e');
    }
  }
}
