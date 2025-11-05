import { ClientReadableStream, Status } from "grpc-web";
import { useEffect, useState } from "react";

export function useGrpcStream<T = { data: string }>(props: {
  streamFn: () => ClientReadableStream<T>;
  onBegin: () => void;
  onData: (response: T) => void;
  onEnd: () => void;
  onError: (error: any) => void;
  onStatus: (status: Status) => void;
}) {
  const [stream, setStream] = useState<ClientReadableStream<T> | null>(null);
  const [isStreaming, setIsStreaming] = useState<boolean>(false);
  const [isFinished, setIsFinished] = useState<boolean>(false); 

  const startStream = () => {
    // Start the stream - this returns a ClientReadableStream
    const stream = props.streamFn();
    console.log(stream);
    // Store stream reference
    setStream(stream);
    setIsStreaming(true);
    props.onBegin();

    // Handle incoming data
    stream.on("data", props.onData);

    // Handle errors
    stream.on("error", (error: any) => {
      setIsStreaming(false);
      setStream(null);
      props.onError(error)
    });

    // Handle stream end
    stream.on("end", () => {
      console.log("Stream ended");
      setIsStreaming(false);
      setStream(null);
      setIsFinished(true);
      props.onEnd()
    });

    // Handle status events
    stream.on("status", (status) => {
      console.log("Stream status:", status);
      props.onStatus(status)
    });
  };

  useEffect(() => {
    return () => {
      if (stream) {
        stream.cancel();
      }
    };
  }, [stream]);

  return {
    startStream,
    isStreaming,
    isFinished
  };
}
