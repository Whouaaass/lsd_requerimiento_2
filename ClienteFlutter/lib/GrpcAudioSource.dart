import 'dart:async';
import 'package:grpc/grpc.dart';
import 'package:just_audio/just_audio.dart';
import 'generated/serviciosStreaming.pbgrpc.dart';

// Class to hold download progress information
class DownloadProgress {
  final int bytesDownloaded;
  final int totalBytes;

  DownloadProgress({required this.bytesDownloaded, required this.totalBytes});

  double get progress => totalBytes > 0 ? bytesDownloaded / totalBytes : 0.0;
}

class GrpcAudioSource extends StreamAudioSource {
  final CancionDTO cancion;
  final StreamController<List<int>> _controller = StreamController.broadcast();
  final StreamController<DownloadProgress> _progressController =
      StreamController.broadcast();
  ClientChannel? _channel;
  AudioServiceClient? _client;
  bool _isStreamStarted = false;
  int _bytesDownloaded = 0;
  int _totalBytes = 0;

  GrpcAudioSource(this.cancion) : super(tag: 'grpc-stream') {
    // Estimate total bytes based on duration
    // Assuming average bitrate of 128 kbps (16 KB/s) for MP3
    // This is an estimate, actual size may vary
    if (cancion.hasDuracionS()) {
      _totalBytes = cancion.duracionS * 16 * 1024; // 16 KB per second
    }
  }

  // Expose progress stream
  Stream<DownloadProgress> get progressStream => _progressController.stream;

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

          // Update download progress
          _bytesDownloaded += fragment.data.length;
          if (!_progressController.isClosed) {
            _progressController.add(
              DownloadProgress(
                bytesDownloaded: _bytesDownloaded,
                totalBytes: _totalBytes,
              ),
            );
          }
        }
      }
      if (!_controller.isClosed) {
        _controller.close();
      }
      if (!_progressController.isClosed) {
        _progressController.close();
      }
    } catch (e) {
      print("Error in gRPC stream: $e");
      if (!_controller.isClosed) {
        _controller.addError(e);
      }
      if (!_progressController.isClosed) {
        _progressController.addError(e);
      }
    }
  }

  // Manually called to clean up
  Future<void> dispose() async {
    await _channel?.shutdown();
    await _controller.close();
    await _progressController.close();
  }
}
