package co.edu.unicauca.infoii.correo.commons;

public class Simulacion {
    public static void simular(int tiempoTotal, String mensaje) {
        System.out.print(mensaje + " ");
        System.out.flush(); 
        int pasos = 20; 
        int delay = tiempoTotal / pasos; 
    
        for (int i = 0; i < pasos; i++) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();                
                return;
            }
    
            System.out.print("# ");
            System.out.flush(); 
        }    
        System.out.println("Finalizado");
        System.out.flush();
    }

    public static void mostrarHiloActual() {
        System.out.println("Hilo actual: " + Thread.currentThread().getName());
        System.out.flush(); 
    }
}
