package modelo.entidades;

import modelo.Party;

import java.util.List;
import java.util.Random;

public class Slime extends Enemigo {

    private static final Random rng = new Random();

    public Slime(String nombre) {
        super(nombre, 50, 18, 2, 9, 1, 30, 8);
    }

    @Override
    public String getNombreTipo() {
        return "Slime";
    }

    @Override
    public Entidad elegirObjetivo(Party party) {
        List<Personaje> vivos = party.obtenerPersonajesVivos();
        if (vivos.isEmpty()) return null;
        return vivos.get(rng.nextInt(vivos.size()));
    }
}
