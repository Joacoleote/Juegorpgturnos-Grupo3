package modelo;

import modelo.items.Consumible;
import modelo.items.Equipable;
import modelo.items.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Inventario implements Serializable {

    private List<Consumible> consumibles;
    private List<Equipable> equipables;

    public Inventario() {
        this.consumibles = new ArrayList<>();
        this.equipables = new ArrayList<>();
    }

    public void agregar(Item item) {
        if (item instanceof Consumible) {
            consumibles.add((Consumible) item);
        } else if (item instanceof Equipable) {
            equipables.add((Equipable) item);
        }
    }

    public void remover(Item item) {
        consumibles.remove(item);
        equipables.remove(item);
    }

    public List<Item> getItems() {
        List<Item> todos = new ArrayList<>();
        todos.addAll(consumibles);
        todos.addAll(equipables);
        return todos;
    }

    public List<Consumible> getConsumibles() {
        return new ArrayList<>(consumibles);
    }

    public List<Equipable> getEquipables() {
        return new ArrayList<>(equipables);
    }

    public boolean estaVacio() {
        return consumibles.isEmpty() && equipables.isEmpty();
    }

    public int getCantidad() {
        return consumibles.size() + equipables.size();
    }
}
