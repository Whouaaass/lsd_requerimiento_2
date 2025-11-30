import 'package:dio/dio.dart';
import '../config_service.dart';
import 'models/metadato_cancion_dto.dart';

class CancionesAPIClient {
  final Dio _dio;

  CancionesAPIClient() : _dio = Dio() {
    _dio.options.baseUrl = ConfigService().apiBaseUrl;
    _dio.options.connectTimeout = const Duration(seconds: 5);
    _dio.options.receiveTimeout = const Duration(seconds: 3);
  }

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
