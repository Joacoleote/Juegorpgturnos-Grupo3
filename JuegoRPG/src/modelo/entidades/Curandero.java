package modelo.entidades;

import modelo.habilidades.Curar;
import modelo.habilidades.Bendicion;

public class Curandero extends Personaje {

    public Curandero(String nombre) {
        super(nombre, 90, 80, 12, 12, 11);
    }

    @Override
    protected void inicializarHabilidades() {
        habilidades.add(new Curar());
        habilidades.add(new Bendicion());
    }

    @Override
    public String getNombreClase() {
        return "Curandero";
    }
}
