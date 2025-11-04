import reproduccionesClient from "./reproducciones-client";

const ReproduccionesAPI = {
  async getByUser(user_id: number) {
    const response = await reproduccionesClient.get("/reproducciones", {
      params: {
        user_id,
      },
    });
    return response.data;
  },
};

export default ReproduccionesAPI;
