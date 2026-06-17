package modelo;

import modelo.items.Consumible;
import modelo.items.Equipable;
import modelo.items.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Inventario implements Serializable {

    private List<Item> items;

    public Inventario() {
        this.items = new ArrayList<>();
    }

    public void agregar(Item item) {
        items.add(item);
    }

    public void remover(Item item) {
        items.remove(item);
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public List<Consumible> getConsumibles() {
        List<Consumible> consumibles = new ArrayList<>();
        for (Item item : items) {
            if (item instanceof Consumible) {
                consumibles.add((Consumible) item);
            }
        }
        return consumibles;
    }

    public List<Equipable> getEquipables() {
        List<Equipable> equipables = new ArrayList<>();
        for (Item item : items) {
            if (item instanceof Equipable) {
                equipables.add((Equipable) item);
            }
        }
        return equipables;
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }

    public int getCantidad() {
        return items.size();
    }
}
