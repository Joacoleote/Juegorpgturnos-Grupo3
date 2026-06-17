package modelo;

import modelo.entidades.Dragon;
import modelo.entidades.Enemigo;
import modelo.entidades.Goblin;

import java.util.ArrayList;
import java.util.List;

public class CatalogoJuego {

    private static CatalogoJuego instancia;

    private CatalogoJuego() {}

    public static CatalogoJuego getInstancia() {
        if (instancia == null) {
            instancia = new CatalogoJuego();
        }
        return instancia;
    }

    public List<Enemigo> crearEncuentro(int escenario) {
        List<Enemigo> enemigos = new ArrayList<Enemigo>();
        int mod = escenario % 6;

        if (mod == 1) {
            enemigos.add(new Goblin("Goblin Rapido"));
            enemigos.add(new Goblin("Goblin Debil"));
        } else if (mod == 2) {
            enemigos.add(new Goblin("Goblin Jefe"));
            enemigos.add(new Goblin("Goblin Endurecido"));
        } else if (mod == 3) {
            enemigos.add(new Goblin("Goblin Guerrero"));
            enemigos.add(new Goblin("Goblin Arquero"));
            enemigos.add(new Goblin("Goblin Chamán"));
        } else if (mod == 4) {
            enemigos.add(new Goblin("Goblin I"));
            enemigos.add(new Goblin("Goblin II"));
            enemigos.add(new Goblin("Goblin III"));
        } else if (mod == 5) {
            enemigos.add(new Dragon("Dragon Anciano"));
        } else {
            enemigos.add(new Goblin("Goblin Elite"));
            enemigos.add(new Dragon("Dragon Menor"));
        }

        // Escalar enemigos para escenarios avanzados
        if (escenario > 6) {
            int bonus = (escenario / 6) * 10;
            for (Enemigo e : enemigos) {
                e.setVidaMaxima(e.getVidaMaxima() + bonus * 3);
                e.setVidaActual(e.getVidaMaxima());
                e.setAtaque(e.getAtaque() + bonus / 2);
                e.setDefensa(e.getDefensa() + bonus / 4);
            }
        }

        return enemigos;
    }

    public String getNombreEncuentro(int escenario) {
        int mod = escenario % 6;
        if (mod == 1) return "Patrulla Goblin";
        if (mod == 2) return "Duo Goblin";
        if (mod == 3) return "Horda Reforzada";
        if (mod == 4) return "Horda Goblin";
        if (mod == 5) return "Jefe: Dragon Anciano";
        return "Elite de las Sombras";
    }
}
