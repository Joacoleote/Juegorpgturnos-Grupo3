package modelo.entidades;

import modelo.habilidades.Habilidad;
import modelo.items.Arma;
import modelo.items.Armadura;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Personaje extends Entidad implements Serializable {

    protected int manaActual;
    protected int manaMaxima;
    protected int experiencia;
    protected int expParaSiguienteNivel;
    protected List<Habilidad> habilidades;
    protected Arma armaEquipada;
    protected Armadura armaduraEquipada;

    public Personaje(String nombre, int vidaMaxima, int manaMaxima, int ataque, int defensa, int velocidad) {
        super(nombre, vidaMaxima, ataque, defensa, velocidad, 1);
        this.manaActual = manaMaxima;
        this.manaMaxima = manaMaxima;
        this.experiencia = 0;
        this.expParaSiguienteNivel = 100;
        this.habilidades = new ArrayList<>();
        inicializarHabilidades();
    }

    protected abstract void inicializarHabilidades();

    public abstract String getNombreClase();

    public void ganarExperiencia(int exp) {
        experiencia += exp;
        while (experiencia >= expParaSiguienteNivel) {
            experiencia -= expParaSiguienteNivel;
            subirNivel();
        }
    }

    protected void subirNivel() {
        nivel++;
        expParaSiguienteNivel = (int) (expParaSiguienteNivel * 1.5);
        int bonusVida = 15;
        vidaMaxima += bonusVida;
        vidaActual = vidaMaxima;
        manaMaxima += 10;
        manaActual = manaMaxima;
        ataque += 3;
        defensa += 2;
        velocidad += 1;
    }

    public boolean consumirMana(int cantidad) {
        if (manaActual >= cantidad) {
            manaActual -= cantidad;
            return true;
        }
        return false;
    }

    public void restaurarMana(int cantidad) {
        manaActual = Math.min(manaMaxima, manaActual + cantidad);
    }

    public void equiparArma(Arma arma) {
        if (armaEquipada != null) {
            ataque -= armaEquipada.getBonusAtaque();
        }
        armaEquipada = arma;
        if (arma != null) {
            ataque += arma.getBonusAtaque();
        }
    }

    public void equiparArmadura(Armadura armadura) {
        if (armaduraEquipada != null) {
            defensa -= armaduraEquipada.getBonusDefensa();
        }
        armaduraEquipada = armadura;
        if (armadura != null) {
            defensa += armadura.getBonusDefensa();
        }
    }

    public int getManaActual() { return manaActual; }
    public int getManaMaxima() { return manaMaxima; }
    public int getExperiencia() { return experiencia; }
    public int getExpParaSiguienteNivel() { return expParaSiguienteNivel; }
    public List<Habilidad> getHabilidades() { return habilidades; }
    public Arma getArmaEquipada() { return armaEquipada; }
    public Armadura getArmaduraEquipada() { return armaduraEquipada; }
    public void setManaActual(int m) { manaActual = Math.max(0, Math.min(manaMaxima, m)); }
    public void setManaMaxima(int m) { manaMaxima = m; }
    public void setExperiencia(int e) { experiencia = e; }
    public void setExpParaSiguienteNivel(int e) { expParaSiguienteNivel = e; }
}
