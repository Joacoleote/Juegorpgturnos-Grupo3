package modelo.entidades;

import modelo.Party;

import java.util.Comparator;
import java.util.List;

public class Goblin extends Enemigo {

    public Goblin(String nombre) {
        super(nombre, 70, 22, 5, 14, 1, 40, 10);
    }

    @Override
    public String getNombreTipo() {
        return "Goblin";
    }

    @Override
    public Entidad elegirObjetivo(Party party) {
        
        List<Personaje> vivos = party.obtenerPersonajesVivos();
        if (vivos.isEmpty()) return null;
        return vivos.stream()
                .min(Comparator.comparingInt(Personaje::getVidaActual))
                .orElse(null);
    }
}
