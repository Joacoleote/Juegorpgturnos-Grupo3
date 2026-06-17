package modelo.items;

import modelo.entidades.Entidad;

import java.io.Serializable;

public abstract class Item implements Serializable {

    protected String nombre;
    protected String descripcion;

    public abstract String usar(Entidad objetivo);

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        return nombre;
    }
}
