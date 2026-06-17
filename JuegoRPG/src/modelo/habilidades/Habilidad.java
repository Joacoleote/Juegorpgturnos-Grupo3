package modelo.habilidades;

import modelo.GestorCombate;
import modelo.TipoObjetivo;
import modelo.entidades.Entidad;

import java.io.Serializable;

public abstract class Habilidad implements Serializable {

    protected String nombre;
    protected String descripcion;
    protected int costeMana;
    protected TipoObjetivo tipoObjetivo;

    public abstract String ejecutar(Entidad origen, Entidad objetivo, GestorCombate gestor);

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getCosteMana() { return costeMana; }
    public TipoObjetivo getTipoObjetivo() { return tipoObjetivo; }
}
