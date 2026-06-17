package modelo.entidades;

import modelo.Party;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public abstract class Enemigo extends Entidad implements Serializable {

    protected int experienciaOtorgada;
    protected int oroOtorgado;
    protected transient Random random = new Random();

    public Enemigo(String nombre, int vidaMaxima, int ataque, int defensa, int velocidad,
                   int nivel, int experienciaOtorgada, int oroOtorgado) {
        super(nombre, vidaMaxima, ataque, defensa, velocidad, nivel);
        this.experienciaOtorgada = experienciaOtorgada;
        this.oroOtorgado = oroOtorgado;
    }

    public abstract String getNombreTipo();

    public Entidad elegirObjetivo(Party party) {
        List<Personaje> vivos = party.obtenerPersonajesVivos();
        if (vivos.isEmpty()) return null;
        if (random == null) random = new Random();
        return vivos.get(random.nextInt(vivos.size()));
    }

    public int getExperienciaOtorgada() { return experienciaOtorgada; }
    public int getOroOtorgado() { return oroOtorgado; }
}
