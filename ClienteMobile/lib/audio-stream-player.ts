import { Audio, AVPlaybackStatus } from 'expo-av';

export class AudioStreamPlayer {
  private sound: Audio.Sound | null = null;
  private isPlaying: boolean = false;
  private isInitialized: boolean = false;
  private chunkQueue: Uint8Array[] = [];
  private currentBuffer: string = '';
  private playbackStarted: boolean = false;

  async initialize() {
    if (this.isInitialized) return;
    
    await Audio.setAudioModeAsync({
      allowsRecordingIOS: false,
      playsInSilentModeIOS: true,
      staysActiveInBackground: false,
      shouldDuckAndroid: true,
      playThroughEarpieceAndroid: false,
    });
    
    this.isInitialized = true;
  }

  async processStreamChunk(chunk: Uint8Array): Promise<void> {
    try {
      // Add chunk to queue
      this.chunkQueue.push(chunk);
      
      // Convert to base64 and add to current buffer
      const base64Chunk = this.uint8ToBase64(chunk);
      this.currentBuffer += base64Chunk;

      // Start playback if we have enough data and haven't started yet
      if (!this.playbackStarted && this.getTotalBufferSize() > 100000) { // ~100KB buffer
        await this.startStreamingPlayback();
      } else if (this.playbackStarted) {
        // If already playing, we could restart with updated buffer
        // or implement a more sophisticated streaming approach
        await this.updatePlaybackBuffer();
      }

    } catch (error) {
      console.error('Error processing stream chunk:', error);
    }
  }

  private getTotalBufferSize(): number {
    return this.chunkQueue.reduce((total, chunk) => total + chunk.length, 0);
  }

  private async startStreamingPlayback(): Promise<void> {
    try {
      this.playbackStarted = true;
      this.isPlaying = true;
      
      const audioUri = `data:audio/mp3;base64,${this.currentBuffer}`;
      
      console.log('Starting streaming playback with buffer size:', this.getTotalBufferSize());
      
      const { sound } = await Audio.Sound.createAsync(
        { uri: audioUri },
        { 
          shouldPlay: true,
          isLooping: false,
          volume: 1.0,
        }
      );
      
      this.sound = sound;
      sound.setOnPlaybackStatusUpdate(this.handlePlaybackStatus);
      
    } catch (error) {
      console.error('Error starting streaming playback:', error);
      this.isPlaying = false;
      this.playbackStarted = false;
    }
  }

  private async updatePlaybackBuffer(): Promise<void> {
    // For continuous streaming, we need to restart playback with the updated buffer
    // This is not ideal but works for demonstration
    if (this.sound && this.isPlaying) {
      try {
        // Get current position
        const status = await this.sound.getStatusAsync();
        
        // Restart playback from current position with updated buffer
        await this.sound.unloadAsync();
        
        const audioUri = `data:audio/mp3;base64,${this.currentBuffer}`;
        const { sound } = await Audio.Sound.createAsync(
          { uri: audioUri },
          { 
            shouldPlay: true,
            isLooping: false,
            volume: 1.0,
            positionMillis: (status as any).positionMillis || 0,
          }
        );
        
        this.sound = sound;
        sound.setOnPlaybackStatusUpdate(this.handlePlaybackStatus);
        
      } catch (error) {
        console.error('Error updating playback buffer:', error);
      }
    }
  }

  private handlePlaybackStatus = (status: AVPlaybackStatus) => {
    if (!status.isLoaded) return;
    
    if (status.didJustFinish) {
      console.log('Playback finished');
      this.isPlaying = false;
    }
    
  };

  private uint8ToBase64(uint8Array: Uint8Array): string {
    let binary = '';
    for (let i = 0; i < uint8Array.byteLength; i++) {
      binary += String.fromCharCode(uint8Array[i]);
    }
    return btoa(binary);
  }

  async stop(): Promise<void> {
    if (this.sound) {
      await this.sound.stopAsync();
      await this.sound.unloadAsync();
      this.sound = null;
    }
    this.isPlaying = false;
    this.playbackStarted = false;
    this.chunkQueue = [];
    this.currentBuffer = '';
  }

  async cleanup(): Promise<void> {
    await this.stop();
    this.isInitialized = false;
  }

  getPlaybackStatus() {
    return {
      isPlaying: this.isPlaying,
      chunksInQueue: this.chunkQueue.length,
      totalBufferSize: this.getTotalBufferSize(),
    };
  }
}