package modelo.items;

import modelo.entidades.Entidad;

public abstract class Equipable extends Item {

    @Override
    public String usar(Entidad objetivo) {
        return "Para equipar este item usa el menu de inventario.";
    }
}
