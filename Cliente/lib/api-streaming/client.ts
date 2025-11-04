import { AudioServiceClient } from './stub/serviciosStreaming_grpc_web_pb'

const API_URL = process.env.EXPO_PUBLIC_STREAMING_API_URL ?? ''

console.log("API_URL for streaming is " + API_URL)

const audioStreamingClient = new AudioServiceClient(API_URL, null, null);

// Make gRPC call
export default audioStreamingClient
