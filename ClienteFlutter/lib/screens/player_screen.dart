import 'package:flutter/material.dart';
import 'package:just_audio/just_audio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import '../models/metadato_cancion_dto.dart';
import '../generated/serviciosStreaming.pb.dart';
import '../services/grpc_audio_source.dart';
import '../widgets/player_controls_widget.dart';
import '../widgets/listener_chat_widget.dart';

class PlayerScreen extends StatefulWidget {
  final MetadatoCancionDTO cancion;

  const PlayerScreen({super.key, required this.cancion});

  @override
  State<PlayerScreen> createState() => _PlayerScreenState();
}

class _PlayerScreenState extends State<PlayerScreen> {
  late AudioPlayer _player;
  late GrpcAudioSource _audioSource;
  bool _isPlaying = false;
  bool _isLoading = true;
  DownloadProgress? _downloadProgress;
  Duration _currentPosition = Duration.zero;
  Duration _totalDuration = Duration.zero;

  // WebSocket URL - configure this based on your server
  // For Android emulator: ws://10.0.2.2:PORT
  // For iOS simulator/desktop: ws://localhost:PORT
  static final String webSocketUrl = 'ws://${dotenv.env['CANCIONES_API_URL'] ?? ""}/ws';

  @override
  void initState() {
    super.initState();
    _player = AudioPlayer();
    _initAudio();
  }

  Future<void> _initAudio() async {
    // Convert MetadatoCancionDTO to CancionDTO
    final cancionDto = CancionDTO(
      id: widget.cancion.id,
      titulo: widget.cancion.titulo,
      autor: widget.cancion.artista,
      genero: widget.cancion.genero,
      idioma: widget.cancion.idioma,
      rutaAlmacenamiento: widget.cancion.rutaAlmacenamiento,
      duracionS: widget.cancion.duracion.toInt(),
    );

    _audioSource = GrpcAudioSource(cancionDto);

    // Listen to download progress
    _audioSource.progressStream.listen((progress) {
      if (mounted) {
        setState(() {
          _downloadProgress = progress;
        });
      }
    });

    try {
      await _player.setAudioSource(_audioSource);

      // Listen to player state
      _player.playerStateStream.listen((state) {
        if (mounted) {
          setState(() {
            _isPlaying = state.playing;
            _isLoading =
                state.processingState == ProcessingState.loading ||
                state.processingState == ProcessingState.buffering;
          });
        }
      });

      // Listen to playback position
      _player.positionStream.listen((position) {
        if (mounted) {
          setState(() {
            _currentPosition = position;
          });
        }
      });

      // Listen to duration
      _player.durationStream.listen((duration) {
        if (mounted && duration != null) {
          setState(() {
            _totalDuration = duration;
          });
        }
      });

      _player.play();
    } catch (e) {
      print("Error loading audio: $e");
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text('Error loading audio: $e')));
      }
    }
  }

  void _handlePlayPause() {
    if (_isPlaying) {
      _player.pause();
    } else {
      _player.play();
    }
  }

  void _handleSeek(Duration position) {
    _player.seek(position);
  }

  @override
  void dispose() {
    _player.dispose();
    _audioSource.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Now Playing'), elevation: 2),
      body: SingleChildScrollView(
        child: Column(
          children: [
            // Player Controls Widget
            PlayerControlsWidget(
              songTitle: widget.cancion.titulo,
              artistName: widget.cancion.artista,
              isPlaying: _isPlaying,
              isLoading: _isLoading,
              downloadProgress: _downloadProgress,
              currentPosition: _currentPosition,
              totalDuration: _totalDuration,
              onPlayPause: _handlePlayPause,
              onSeek: _handleSeek,
            ),

            // Listener Chat Widget
            ListenerChatWidget(
              webSocketUrl: webSocketUrl,
              songId: widget.cancion.id,
            ),

            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }
}
