package modelo.entidades;

import modelo.habilidades.GolpeFuerte;
import modelo.habilidades.GritoDeBatalla;

public class Guerrero extends Personaje {

    public Guerrero(String nombre) {
        super(nombre, 130, 30, 25, 15, 10);
    }

    @Override
    protected void inicializarHabilidades() {
        habilidades.add(new GolpeFuerte());
        habilidades.add(new GritoDeBatalla());
    }

    @Override
    public String getNombreClase() {
        return "Guerrero";
    }
}
