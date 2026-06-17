package modelo.items;

import modelo.entidades.Entidad;

public abstract class Consumible extends Item {

    @Override
    public abstract String usar(Entidad objetivo);
}
