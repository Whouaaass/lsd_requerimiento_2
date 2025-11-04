import { ReproduccionesAPI } from "@/lib/api-reproducciones";
import { Button, Text } from "@react-navigation/elements";
import { useState } from "react";
import { SafeAreaView } from "react-native-safe-area-context";

export default function TestStreamingPage() {
  const [response, setResponse] = useState<any>();

  const getReproducciones = () =>
    ReproduccionesAPI.getByUser(1)
      .then((data) => {
        setResponse(data);
      })
      .catch((err) => {
        console.log(err);
      });
  return (
    <SafeAreaView>
      <Button onPressOut={getReproducciones}>
        Obtener reproducciones
      </Button>

      <Text>
        {JSON.stringify(response)}
      </Text>
    </SafeAreaView>
  )


}
