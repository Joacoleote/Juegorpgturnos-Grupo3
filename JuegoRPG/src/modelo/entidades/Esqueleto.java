package modelo.entidades;

public class Esqueleto extends Enemigo {

    public Esqueleto(String nombre) {
        super(nombre, 75, 18, 10, 9, 2, 65, 15);
    }

    @Override
    public String getNombreTipo() {
        return "Esqueleto";
    }
}
