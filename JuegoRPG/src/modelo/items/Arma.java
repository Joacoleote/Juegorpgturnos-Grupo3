package modelo.items;

public class Arma extends Equipable {

    private int bonusAtaque;

    public Arma(String nombre, int bonusAtaque) {
        this.nombre = nombre;
        this.bonusAtaque = bonusAtaque;
        this.descripcion = "Arma que otorga +" + bonusAtaque + " de ataque.";
    }

    public int getBonusAtaque() { return bonusAtaque; }
}
