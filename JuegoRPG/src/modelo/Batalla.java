package modelo;

import modelo.entidades.Enemigo;

import java.io.Serializable;
import java.util.List;

public class Batalla implements Serializable {

    private int numeroEscenario;
    private List<Enemigo> enemigos;
    private boolean completada;
    private boolean victoria;

    public Batalla(int numeroEscenario, List<Enemigo> enemigos) {
        this.numeroEscenario = numeroEscenario;
        this.enemigos = enemigos;
        this.completada = false;
        this.victoria = false;
    }

    public void completar(boolean victoria) {
        this.completada = true;
        this.victoria = victoria;
    }

    public int getNumeroEscenario() { return numeroEscenario; }
    public List<Enemigo> getEnemigos() { return enemigos; }
    public boolean isCompletada() { return completada; }
    public boolean isVictoria() { return victoria; }
}
