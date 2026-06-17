package modelo.items;

import modelo.entidades.Entidad;
import modelo.entidades.Personaje;

public class PocianMana extends Consumible {

    private int cantidadMana;

    public PocianMana(String nombre, int cantidadMana) {
        this.nombre = nombre;
        this.cantidadMana = cantidadMana;
        this.descripcion = "Restaura " + cantidadMana + " puntos de mana.";
    }

    @Override
    public String usar(Entidad objetivo) {
        if (objetivo instanceof Personaje) {
            Personaje p = (Personaje) objetivo;
            p.restaurarMana(cantidadMana);
            return p.getNombre() + " usa " + nombre + " y recupera " + cantidadMana + " de mana!";
        }
        return "Este item solo puede usarse en personajes.";
    }

    public int getCantidadMana() { return cantidadMana; }
}
