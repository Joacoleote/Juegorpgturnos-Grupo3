package modelo.items;

public class Armadura extends Equipable {

    private int bonusDefensa;

    public Armadura(String nombre, int bonusDefensa) {
        this.nombre = nombre;
        this.bonusDefensa = bonusDefensa;
        this.descripcion = "Armadura que otorga +" + bonusDefensa + " de defensa.";
    }

    public int getBonusDefensa() { return bonusDefensa; }
}
