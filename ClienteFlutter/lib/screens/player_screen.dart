import 'package:flutter/material.dart';
import 'package:just_audio/just_audio.dart';
import '../models/metadato_cancion_dto.dart';
import '../generated/serviciosStreaming.pb.dart';
import '../GrpcAudioSource.dart';

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
      // Add other fields if necessary
    );

    _audioSource = GrpcAudioSource(cancionDto);

    try {
      await _player.setAudioSource(_audioSource);
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

  @override
  void dispose() {
    _player.dispose();
    _audioSource.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Now Playing')),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.music_note, size: 100, color: Colors.blue),
            const SizedBox(height: 30),
            Text(
              widget.cancion.titulo,
              style: Theme.of(context).textTheme.headlineSmall,
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 10),
            Text(
              widget.cancion.artista,
              style: Theme.of(context).textTheme.titleMedium,
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 50),
            if (_isLoading)
              const CircularProgressIndicator()
            else
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  IconButton(
                    iconSize: 64,
                    icon: Icon(
                      _isPlaying
                          ? Icons.pause_circle_filled
                          : Icons.play_circle_filled,
                    ),
                    onPressed: () {
                      if (_isPlaying) {
                        _player.pause();
                      } else {
                        _player.play();
                      }
                    },
                  ),
                ],
              ),
          ],
        ),
      ),
    );
  }
}
