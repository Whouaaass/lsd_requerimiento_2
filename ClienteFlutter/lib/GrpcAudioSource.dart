import 'dart:async';
import 'package:flutter/services.dart' show rootBundle;
import 'package:just_audio/just_audio.dart';

class GrpcAudioSource extends StreamAudioSource {
  final StreamController<List<int>> _controller = StreamController.broadcast();
  StreamSubscription? _grpcSubscription;

  // Añadimos un 'flag' para asegurarnos de que solo se inicie una vez
  bool _isStreamStarted = false;

  GrpcAudioSource(/* this._client */) : super(tag: 'grpc-stream');

  // --- MÉTODO 'startStreaming()' ELIMINADO ---
  // Ya no lo necesitamos aquí.

  @override
  Future<StreamAudioResponse> request([int? start, int? end]) async {

    // --- LÓGICA DE INICIO MOVIDA AQUÍ ---
    // Cuando just_audio pida los datos, empezamos el stream.
    if (!_isStreamStarted) {
      _isStreamStarted = true;
      _startSimulatedStream(); // Llama a la función que inicia el stream
    }
    // --- FIN DE LA NUEVA LÓGICA ---

    return StreamAudioResponse(
      sourceLength: null,
      contentLength: null,
      offset: 0,
      stream: _controller.stream,
      contentType: 'audio/mpeg', // Asegúrate que coincida con tu archivo
    );
  }

  // Esta era la lógica de 'startStreaming', ahora en su propio método
  void _startSimulatedStream() {
    // TODO: Reemplaza con tu llamada gRPC real
    final Stream<List<int>> grpcStream = _simulateGrpcStream();

    _grpcSubscription = grpcStream.listen(
          (bytes) {
        if (!_controller.isClosed) {
          _controller.add(bytes);
        }
      },
      onError: (e) {
        if (!_controller.isClosed) {
          _controller.addError(e);
        }
      },
      onDone: () {
        if (!_controller.isClosed) {
          _controller.close();
        }
      },
    );
  }


  @override
  void dispose() {
    _grpcSubscription?.cancel();
    _controller.close();
  }

  // --- SIMULACIÓN (Asegúrate de que usa el archivo bueno) ---
  Stream<List<int>> _simulateGrpcStream() async* {

    // ¡ASEGÚRATE DE QUE ESTA ES LA RUTA CORRECTA!
    const String assetPath = 'assets/audio/sample4.mp3';
    const int chunkSize = 8192;
    const Duration duration = Duration(milliseconds: 150);

    try {
      final byteData = await rootBundle.load(assetPath);
      final buffer = byteData.buffer.asUint8List();
      print("Simulación: Archivo cargado (${assetPath}), tamaño: ${buffer.length} bytes");

      for (int i = 0; i < buffer.length; i += chunkSize) {
        final end = (i + chunkSize > buffer.length) ? buffer.length : (i + chunkSize);
        yield buffer.sublist(i, end);
        print("Simulación: Enviando chunk ${i ~/ chunkSize + 1}");
        await Future.delayed(duration);
      }
      print("Simulación: Stream completado.");

    } catch (e) {
      print("Error en simulación de stream: $e");
      yield* Stream.error(e);
    }
  }
}