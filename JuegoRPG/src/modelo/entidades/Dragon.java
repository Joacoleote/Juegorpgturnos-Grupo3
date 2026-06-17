package modelo.entidades;

import modelo.Party;

import java.util.Comparator;
import java.util.List;

public class Dragon extends Enemigo {

    private int turnosTranscurridos;
    private static final int TURNOS_PARA_SOPLO = 3;

    public Dragon(String nombre) {
        super(nombre, 300, 35, 20, 8, 5, 250, 60);
        this.turnosTranscurridos = 0;
    }

    @Override
    public String getNombreTipo() {
        return "Dragon";
    }

    public boolean usarSoplo() {
        turnosTranscurridos++;
        if (turnosTranscurridos % TURNOS_PARA_SOPLO == 0) {
            return true;
        }
        return false;
    }

    @Override
    public Entidad elegirObjetivo(Party party) {
        // Dragon targets the ally with the highest HP
        List<Personaje> vivos = party.obtenerPersonajesVivos();
        if (vivos.isEmpty()) return null;
        return vivos.stream()
                .max(Comparator.comparingInt(Personaje::getVidaActual))
                .orElse(null);
    }

    public int getTurnosTranscurridos() { return turnosTranscurridos; }
}
