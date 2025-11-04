import { AudioStreamingAPI } from '@/lib/api-streaming';
import { ClientReadableStream } from 'grpc-web';
import React, { useEffect, useState } from 'react';
import { Button, FlatList, Text, View } from 'react-native';

const GrpcStreamComponent = () => {
  const [messages, setMessages] = useState<any[]>([]);
  const [stream, setStream] = useState<ClientReadableStream<any> | null>(null);
  const [isStreaming, setIsStreaming] = useState(false);

  const startStream = () => {

    // Start the stream - this returns a ClientReadableStream
    const stream = AudioStreamingAPI.getAudioStreamTest()
    console.log(stream)
    // Store stream reference
    setStream(stream);
    setIsStreaming(true);
    setMessages([])

    // Handle incoming data
    stream.on('data', (response) => {
      const newMessage = response.toObject();
      setMessages(prev => [...prev, newMessage]);
      console.log('Received +1');
    });

    // Handle errors
    stream.on('error', (error) => {
      console.log('Stream error:', error);
      setIsStreaming(false);
      setStream(null);
    });

    // Handle stream end
    stream.on('end', () => {
      console.log('Stream ended');
      setIsStreaming(false);
      setStream(null);
    });

    // Handle status events
    stream.on('status', (status) => {
      console.log('Stream status:', status);
    });
  };

  const stopStream = () => {
    if (stream) {
      // Cancel the stream
      stream.cancel();
      setIsStreaming(false);
      setStream(null);
    }
  };

  // Cleanup on component unmount
  useEffect(() => {
    return () => {
      if (stream) {
        stream.cancel();
      }
    };
  }, [stream]);

  return (
    <View style={{ flex: 1, padding: 20 }}>
      <Text style={{ fontSize: 18, marginBottom: 20 }}>
        Server Streaming Example
      </Text>

      {!isStreaming ? (
        <Button title="Start Stream" onPress={startStream} />
      ) : (
        <Button title="Stop Stream" onPress={stopStream} />
      )}

      <View style={{ marginTop: 20, flex: 1 }}>
        <Text>Received Messages:</Text>
        <Text>Received Count: {messages.length}</Text>
        <FlatList
          data={messages}
          style={{ flex: 1 }}
          renderItem={({ item, index }) => (<Text numberOfLines={1}>
            {`Fragmento #${index}`}
          </Text>)}
        >
        </FlatList>
      </View>
    </View>
  );
};

export default GrpcStreamComponent;
