package modelo;

import modelo.entidades.Personaje;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Party implements Serializable {

    private String nombre;
    private List<Personaje> personajes;
    private Inventario inventario;

    public Party(String nombre) {
        this.nombre = nombre;
        this.personajes = new ArrayList<>();
        this.inventario = new Inventario();
    }

    public void agregarPersonaje(Personaje personaje) {
        if (personajes.size() < 4) {
            personajes.add(personaje);
        }
    }

    public void eliminarPersonaje(Personaje personaje) {
        personajes.remove(personaje);
    }

    public List<Personaje> getPersonajes() {
        return new ArrayList<>(personajes);
    }

    public List<Personaje> obtenerPersonajesVivos() {
        List<Personaje> vivos = new ArrayList<>();
        for (Personaje p : personajes) {
            if (p.estaVivo()) {
                vivos.add(p);
            }
        }
        return vivos;
    }

    public boolean todosEliminados() {
        return obtenerPersonajesVivos().isEmpty();
    }

    public String getNombre() { return nombre; }
    public Inventario getInventario() { return inventario; }
    public int getCantidadMiembros() { return personajes.size(); }
}
