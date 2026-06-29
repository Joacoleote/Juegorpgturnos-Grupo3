package modelo;

import modelo.entidades.Personaje;
import modelo.items.Item;
import modelo.items.Pocion;
import modelo.items.PocionMana;
import modelo.items.Arma;
import modelo.items.Armadura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GestorRecompensas {

    private Random random;

    public GestorRecompensas() {
        this.random = new Random();
    }

    public Map<Personaje, Boolean> distribuirXP(List<Personaje> personajes, int expTotal) {
        Map<Personaje, Boolean> subieroNivel = new HashMap<>();
        if (personajes.isEmpty() || expTotal <= 0) return subieroNivel;

        int expPorPersonaje = expTotal / personajes.size();
        for (Personaje p : personajes) {
            int nivelAntes = p.getNivel();
            p.ganarExperiencia(expPorPersonaje);
            subieroNivel.put(p, p.getNivel() > nivelAntes);
        }
        return subieroNivel;
    }

    public List<Item> generarRecompensasItems(int escenario) {
        List<Item> recompensas = new ArrayList<>();
        int tirada = random.nextInt(100);

        if (tirada < 50) {
            recompensas.add(new Pocion("Pocion de Vida", 40));
        } else if (tirada < 70) {
            recompensas.add(new PocionMana("Pocion de Mana", 30));
        } else if (tirada < 80) {
            recompensas.add(new Pocion("Pocion Grande de Vida", 80));
        } else if (tirada < 90 && escenario >= 3) {
            recompensas.add(new Arma("Espada del Escenario " + escenario, 5 + escenario));
        } else if (tirada < 95 && escenario >= 2) {
            recompensas.add(new Armadura("Armadura del Escenario " + escenario, 4 + escenario));
        }

        return recompensas;
    }

    public void restaurarVidaParcialment(List<Personaje> personajes) {
        for (Personaje p : personajes) {
            int restauracion = (int) (p.getVidaMaxima() * 0.30);
            p.curar(restauracion);
        }
    }
}
