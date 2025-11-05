import { useGrpcStream } from '@/hooks/useGrpcStream';
import { AudioStreamingAPI } from '@/lib/api-streaming';
import { AudioStreamPlayer } from '@/lib/audio-stream-player';
import { Button, Text } from '@react-navigation/elements';
import React, { useEffect, useRef, useState } from 'react';
import { StyleSheet, View } from 'react-native';

const AudioStreamComponent = () => {

  const [chunksReceived, setChunksReceived] = useState(0);
  const audioPlayerRef = useRef<AudioStreamPlayer>(new AudioStreamPlayer());
  const streamHandlerRef = useRef<boolean>(false);


  const { startStream, isStreaming, isFinished } = useGrpcStream<any>({
    streamFn: () => AudioStreamingAPI.getAudioStream({
      idUsuario: 1,
      cancion: {
        id: 1,
        album: "Album",
        autor: "Artista",
        titulo: "Titulo",
        duracionS: 10,
        genero: "genero",
        idioma: "idioma",
        anioLanzamiento: 2022,
        rutaAlmacenamiento: "../canciones/cancion1.mp3"
      }
    }),
    onBegin: () => {
      console.log('Stream started');
    },
    onData: async (response) => {
      try {
        const data = response.getData();

        // Process chunk immediately - player will handle streaming
        await audioPlayerRef.current.processStreamChunk(data);
        setChunksReceived(prev => prev + 1);

        // Update playing state based on player's internal state
        const status = audioPlayerRef.current.getPlaybackStatus();

        if (status.isPlaying) {

        }

      } catch (error) {
        console.error('Error processing chunk:', error);
      }
    },
    onEnd: () => {
      console.log('Stream ended');
    },
    onError: (error) => {
      console.error('Stream error:', error);
    },
    onStatus: (status) => {
      console.log('Stream status:', status);
    }
  });
  useEffect(() => {
    const audioPlayer = audioPlayerRef.current;

    const initializePlayer = async () => {
      await audioPlayer.initialize();
    };
    initializePlayer();

    return () => {
      audioPlayer.cleanup();
    };
  }, []);

  const handleStop = async () => {
    setChunksReceived(0);
    await audioPlayerRef.current.stop();
    streamHandlerRef.current = false;
  };

  const handlePlay = async () => {
    if (!isFinished && !isStreaming) {
      startStream();
      return;
    }
    audioPlayerRef.current.play(0);
    
  }

  return (
    <View style={styles.container}>
      <Text style={styles.status}>
        A
      </Text>

      <Text style={styles.chunkInfo}>
        Chunks received: {chunksReceived}
      </Text>
      <Button onPressOut={handlePlay} disabled={isStreaming}>
        {isStreaming ? "Parar reproducción" : "Reproduccir canción"}
      </Button>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 20,
    alignItems: 'center',
  },
  status: {
    fontSize: 16,
    marginBottom: 20,
    textAlign: 'center',
  },
  chunkInfo: {
    fontSize: 14,
    marginTop: 10,
    color: '#666',
  },
  button: {
    backgroundColor: '#ff4444',
    padding: 15,
    borderRadius: 8,
    minWidth: 100,
    alignItems: 'center',
  },
  buttonDisabled: {
    backgroundColor: '#cccccc',
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default AudioStreamComponent;