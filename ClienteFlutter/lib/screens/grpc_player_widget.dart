import 'package:flutter/material.dart';
import 'package:just_audio/just_audio.dart';
// Asegúrate de importar tu archivo GrpcAudioSource
import 'package:spotifake_player/GrpcAudioSource.dart';

class GrpcPlayerWidget extends StatefulWidget {
  const GrpcPlayerWidget({Key? key}) : super(key: key);

  @override
  State<GrpcPlayerWidget> createState() => _GrpcPlayerWidgetState();
}

class _GrpcPlayerWidgetState extends State<GrpcPlayerWidget> {
  late final AudioPlayer _audioPlayer;
  GrpcAudioSource? _audioSource;

  bool _isDownloading = false;
  bool _isPlaying = false;

  @override
  void initState() {
    super.initState();
    _audioPlayer = AudioPlayer();

    _audioPlayer.playerStateStream.listen((playerState) {
      final isPlaying = playerState.playing;
      final processingState = playerState.processingState;

      setState(() {
        _isPlaying = isPlaying;

        // Si la descarga/buffering termina
        if (processingState != ProcessingState.buffering &&
            processingState != ProcessingState.loading) {
          _isDownloading = false;
        }

        // Si termina de reproducir, resetea el estado
        if (processingState == ProcessingState.completed) {
          // Usamos un pequeño retraso para que el estado 'completed' se registre
          // antes de limpiar la UI.
          Future.delayed(Duration(milliseconds: 500), () {
            if (mounted) { // Solo si el widget sigue en pantalla
              stopPlayback();
            }
          });
        }
      });
    });
  }

  @override
  void dispose() {
    stopPlayback();
    _audioPlayer.dispose();
    super.dispose();
  }
// ... en _GrpcPlayerWidgetState ...

  void startDownloadAndPlay() async {
    // Asegurarse de que todo esté detenido antes de empezar
    stopPlayback();

    setState(() {
      _isDownloading = true;
    });

    // 1. Crea tu custom audio source
    // (Asegúrate que tu GrpcAudioSource esté usando 'sample4.mp3' internamente)
    _audioSource = GrpcAudioSource(/* yourGrpcClient */);

    try {
      // 2. Indica a just_audio que use esta source.
      // ESTO AHORA AUTOMÁTICAMENTE INICIARÁ EL STREAM.
      await _audioPlayer.setAudioSource(_audioSource!);

      // 3. ¡YA NO NECESITAMOS ESTA LÍNEA!
      // _audioSource!.startStreaming(); // <--- BORRA ESTA LÍNEA

      // 4. Indica al player que reproduzca.
      _audioPlayer.play();

    } catch (e) {
      print("Error al establecer la fuente de audio (Stream): $e");
      if (mounted) {
        setState(() {
          _isDownloading = false;
        });
      }
    }
  }

  void startDirectPlay() async {
    // 1. Detiene cualquier reproducción anterior
    stopPlayback();

    try {
      // 2. Apunta al nuevo archivo de prueba EXACTO
      print("Intentando cargar 'assets/audio/sample4.mp3'...");
      await _audioPlayer.setAsset('assets/audio/sample4.mp3');

      print("Carga de asset exitosa, reproduciendo...");
      // 3. Reproduce
      _audioPlayer.play();

    } catch (e) {
      print("Error al reproducir directamente desde el asset: $e");
      // Si esto falla, el problema es 100% de configuración del proyecto
    }
  }

  void stopPlayback() {
    _audioPlayer.stop();
    // Limpia la suscripción gRPC y el stream controller
    _audioSource?.dispose();
    _audioSource = null; // Libera la referencia

    // Solo actualiza el estado si el widget está montado
    if (mounted) {
      setState(() {
        _isDownloading = false;
        _isPlaying = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('gRPC Audio Stream Player'),
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              if (_isDownloading)
                Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: CircularProgressIndicator(),
                ),

              // --- LÓGICA DE BOTONES ACTUALIZADA ---

              // Si no está sonando Y no está descargando, muestra los botones de play
              if (!_isPlaying && !_isDownloading)
                Column(
                  children: [
                    ElevatedButton.icon(
                      icon: Icon(Icons.play_arrow),
                      label: Text('Play Stream (gRPC)'),
                      onPressed: startDownloadAndPlay,
                    ),
                    SizedBox(height: 16),
                    ElevatedButton.icon(
                      icon: Icon(Icons.audiotrack),
                      label: Text('Play Directo (Asset)'),
                      style: ElevatedButton.styleFrom(
                          backgroundColor: Colors.green
                      ),
                      onPressed: startDirectPlay,
                    ),
                  ],
                ),

              // Si está sonando O está descargando, muestra el botón de Stop
              if (_isPlaying || _isDownloading)
                ElevatedButton.icon(
                  icon: Icon(Icons.stop),
                  label: Text('Stop Playback'),
                  style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
                  onPressed: stopPlayback,
                ),
            ],
          ),
        ),
      ),
    );
  }
}