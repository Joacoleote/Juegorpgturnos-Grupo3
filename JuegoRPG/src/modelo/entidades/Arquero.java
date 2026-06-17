package modelo.entidades;

import modelo.habilidades.DisparoCritico;
import modelo.habilidades.LluviaDeFlecha;

public class Arquero extends Personaje {

    public Arquero(String nombre) {
        super(nombre, 95, 50, 22, 10, 16);
    }

    @Override
    protected void inicializarHabilidades() {
        habilidades.add(new DisparoCritico());
        habilidades.add(new LluviaDeFlecha());
    }

    @Override
    public String getNombreClase() {
        return "Arquero";
    }
}
