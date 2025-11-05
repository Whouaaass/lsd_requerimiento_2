import AudioStreamComponent from "@/components/test/AudioStreamingComponent";
import { SafeAreaView } from "react-native-safe-area-context";

export default function AudioStreamingTestPage() {



  return <SafeAreaView style={{ flex: 1 }}>
    <AudioStreamComponent />
  </SafeAreaView>;
}
