package modelo;

import modelo.entidades.Enemigo;
import modelo.entidades.Goblin;
import modelo.entidades.Slime;

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

        enemigos.add(new Goblin("Goblin"));
        enemigos.add(new Slime("Slime"));

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
        return "Goblin y Slime";
    }
}
