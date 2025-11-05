import GrpcStreamComponent from "@/components/test/GrpcStreamComponent";
import { SafeAreaView } from "react-native-safe-area-context";

export default function StreamingPage() {
  return <SafeAreaView style={{
    flex: 1
  }}>
    <GrpcStreamComponent />
  </SafeAreaView>
}
