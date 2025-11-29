import 'dart:async';
import 'package:grpc/grpc.dart';
import 'package:just_audio/just_audio.dart';
import 'generated/serviciosStreaming.pbgrpc.dart';

class GrpcAudioSource extends StreamAudioSource {
  final CancionDTO cancion;
  final StreamController<List<int>> _controller = StreamController.broadcast();
  ClientChannel? _channel;
  AudioServiceClient? _client;
  bool _isStreamStarted = false;

  GrpcAudioSource(this.cancion) : super(tag: 'grpc-stream');

  @override
  Future<StreamAudioResponse> request([int? start, int? end]) async {
    if (!_isStreamStarted) {
      _isStreamStarted = true;
      _startGrpcStream();
    }
    return StreamAudioResponse(
      sourceLength: null,
      contentLength: null,
      offset: 0,
      stream: _controller.stream,
      contentType: 'audio/mpeg',
    );
  }

  void _startGrpcStream() async {
    // Use 10.0.2.2 for Android emulator to access localhost of host machine
    // Use localhost for iOS simulator or desktop
    // For now defaulting to localhost, user might need to change this based on platform
    const host = '10.0.2.2';

    _channel = ClientChannel(
      host,
      port: 50051,
      options: const ChannelOptions(credentials: ChannelCredentials.insecure()),
    );
    _client = AudioServiceClient(_channel!);

    // Assuming a dummy user ID for now as it's not provided in the context
    final request = PeticionStreamDTO(idUsuario: 1, cancion: cancion);

    try {
      final stream = _client!.stremearCancion(request);
      await for (var fragment in stream) {
        if (!_controller.isClosed) {
          _controller.add(fragment.data);
        }
      }
      if (!_controller.isClosed) {
        _controller.close();
      }
    } catch (e) {
      print("Error in gRPC stream: $e");
      if (!_controller.isClosed) {
        _controller.addError(e);
      }
    }
  }

  // Manually called to clean up
  Future<void> dispose() async {
    await _channel?.shutdown();
    await _controller.close();
  }
}
