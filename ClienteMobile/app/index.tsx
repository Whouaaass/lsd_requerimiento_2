import { Button } from "@react-navigation/elements";
import { useRouter } from "expo-router";
import { Text } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";

export default function Index() {
  const router = useRouter();
  return (
    <SafeAreaView
      style={{
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <Text>Edit app/index.tsx to edit this screen.</Text>

      <Button onPressOut={() => router.navigate("/streaming")}>
        Ir a Streaming
      </Button>
      <Button onPressOut={() => router.navigate("/test-reproducciones")}>
        Ir a reproducciones
      </Button>
      <Button onPressOut={() => router.navigate("/audio-streaming-test")}>
        Ir a audio streaming
      </Button>
    </SafeAreaView>
  );
}
