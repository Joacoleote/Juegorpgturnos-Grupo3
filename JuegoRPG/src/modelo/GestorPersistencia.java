package modelo;

import java.io.*;

public class GestorPersistencia {

    private static final String RUTA_GUARDADO = "partida_guardada.dat";

    public void guardar(Partida partida, String rutaArchivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(partida);
        }
    }

    public void guardar(Partida partida) throws IOException {
        guardar(partida, RUTA_GUARDADO);
    }

    public Partida cargar(String rutaArchivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            return (Partida) ois.readObject();
        }
    }

    public Partida cargar() throws IOException, ClassNotFoundException {
        return cargar(RUTA_GUARDADO);
    }

    public boolean existeGuardado() {
        return new File(RUTA_GUARDADO).exists();
    }

    public boolean existeGuardado(String rutaArchivo) {
        return new File(rutaArchivo).exists();
    }
}
