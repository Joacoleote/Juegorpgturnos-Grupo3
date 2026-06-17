package modelo.entidades;

import modelo.habilidades.DefensaFortificada;
import modelo.habilidades.GolpeEscudo;

public class Tanque extends Personaje {

    public Tanque(String nombre) {
        super(nombre, 160, 40, 18, 20, 7);
    }

    @Override
    protected void inicializarHabilidades() {
        habilidades.add(new DefensaFortificada());
        habilidades.add(new GolpeEscudo());
    }

    @Override
    public String getNombreClase() {
        return "Tanque";
    }
}
