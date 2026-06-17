package modelo.entidades;

import modelo.habilidades.BolaDeFuego;
import modelo.habilidades.RayoHelado;

public class Mago extends Personaje {

    public Mago(String nombre) {
        super(nombre, 80, 90, 15, 8, 13);
    }

    @Override
    protected void inicializarHabilidades() {
        habilidades.add(new BolaDeFuego());
        habilidades.add(new RayoHelado());
    }

    @Override
    public String getNombreClase() {
        return "Mago";
    }
}
