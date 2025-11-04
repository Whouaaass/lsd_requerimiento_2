import axios from "axios";

const API_URL = process.env.EXPO_PUBLIC_CANCIONES_API_URL;

console.log("API_URL for reproducciones is: " + API_URL);

const cancionesClient = axios.create({
  baseURL: API_URL + "/api",
});

export default cancionesClient;
