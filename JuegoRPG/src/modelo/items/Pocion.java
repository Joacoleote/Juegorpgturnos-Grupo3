package modelo.items;

import modelo.entidades.Entidad;

public class Pocion extends Consumible {

    private int cantidadCuracion;

    public Pocion(String nombre, int cantidadCuracion) {
        this.nombre = nombre;
        this.cantidadCuracion = cantidadCuracion;
        this.descripcion = "Restaura " + cantidadCuracion + " puntos de vida.";
    }

    @Override
    public String usar(Entidad objetivo) {
        objetivo.curar(cantidadCuracion);
        return objetivo.getNombre() + " usa " + nombre + " y recupera " + cantidadCuracion + " de vida!";
    }

    public int getCantidadCuracion() { return cantidadCuracion; }
}
