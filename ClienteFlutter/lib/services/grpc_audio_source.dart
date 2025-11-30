import 'dart:async';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:grpc/grpc.dart';
import 'package:just_audio/just_audio.dart';
import 'package:spotifake_player/services/app_logger.dart';
import '../generated/serviciosStreaming.pbgrpc.dart';
import 'config_service.dart';
import 'grpc_channel_stub.dart'
    if (dart.library.html) 'grpc_channel_web.dart'
    if (dart.library.io) 'grpc_channel_io.dart';

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
  dynamic _channel;
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
    // Platform-specific configuration:
    // - Web: Uses STREAMING_API_HOST_WEB and STREAMING_API_PORT_WEB (Envoy proxy)
    // - Native: Uses STREAMING_API_HOST and STREAMING_API_PORT (direct gRPC)
    final config = ConfigService();
    final host = kIsWeb ? config.streamingHostWeb : config.streamingHost;
    final port = kIsWeb ? config.streamingPortWeb : config.streamingPort;

    AppLogger.info(
      "Platform: ${kIsWeb ? 'Web' : 'Native'}"
      "Host: $host"
      "Port: $port",
    );

    // Use platform-specific channel factory
    _channel = createChannel(host, port);
    _client = AudioServiceClient(_channel!);

    // Assuming a dummy user ID for now as it's not provided in the context
    final request = PeticionStreamDTO(idUsuario: 1, cancion: cancion);

    try {
      AppLogger.info("Attempting to connect to gRPC server at $host:$port");
      final stream = _client!.stremearCancion(request);
      AppLogger.info("gRPC stream initiated successfully");

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
      AppLogger.info("gRPC stream completed successfully");
      if (!_controller.isClosed) {
        _controller.close();
      }
      if (!_progressController.isClosed) {
        _progressController.close();
      }
    } catch (e, stackTrace) {
      AppLogger.error("❌ Error in gRPC stream: $e");
      AppLogger.error("Error type: ${e.runtimeType}");
      if (e is GrpcError) {
        AppLogger.error(
          "gRPC Error Code: ${e.code}"
          "gRPC Error Message: ${e.message}"
          "gRPC Error Details: ${e.details}",
        );
      }
      AppLogger.error("Stack trace: $stackTrace");
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
