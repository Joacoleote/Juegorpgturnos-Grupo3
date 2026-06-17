package modelo.entidades;

import java.io.Serializable;

public abstract class Entidad implements Serializable {

    protected String nombre;
    protected int vidaActual;
    protected int vidaMaxima;
    protected int ataque;
    protected int defensa;
    protected int velocidad;
    protected int nivel;
    protected boolean enDefensa;

    public Entidad(String nombre, int vidaMaxima, int ataque, int defensa, int velocidad, int nivel) {
        this.nombre = nombre;
        this.vidaMaxima = vidaMaxima;
        this.vidaActual = vidaMaxima;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.nivel = nivel;
        this.enDefensa = false;
    }

    public void recibirDaño(int cantidad) {
        // La reduccion por defensa ya fue calculada en GestorCombate.calcularDañoFisico/Magico
        vidaActual = Math.max(0, vidaActual - Math.max(1, cantidad));
    }

    public void curar(int cantidad) {
        vidaActual = Math.min(vidaMaxima, vidaActual + cantidad);
    }

    public boolean estaVivo() {
        return vidaActual > 0;
    }

    public void activarDefensa() {
        enDefensa = true;
    }

    public void desactivarDefensa() {
        enDefensa = false;
    }

    public boolean estaEnDefensa() {
        return enDefensa;
    }

    public String getNombre() { return nombre; }
    public int getVidaActual() { return vidaActual; }
    public int getVidaMaxima() { return vidaMaxima; }
    public int getAtaque() { return ataque; }
    public int getDefensa() { return defensa; }
    public int getVelocidad() { return velocidad; }
    public int getNivel() { return nivel; }

    public void setVidaActual(int v) { vidaActual = Math.max(0, Math.min(vidaMaxima, v)); }
    public void setVidaMaxima(int v) { vidaMaxima = v; }
    public void setAtaque(int a) { ataque = a; }
    public void setDefensa(int d) { defensa = d; }
    public void setNivel(int n) { nivel = n; }
    public void setVelocidad(int v) { velocidad = Math.max(1, v); }
}
