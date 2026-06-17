package modelo.habilidades;

import modelo.GestorCombate;
import modelo.TipoObjetivo;
import modelo.entidades.Entidad;

public class Bendicion extends Habilidad {

    public Bendicion() {
        this.nombre = "Bendicion";
        this.descripcion = "Otorga una bendicion a un aliado, aumentando su defensa en 10.";
        this.costeMana = 15;
        this.tipoObjetivo = TipoObjetivo.ALIADO;
    }

    @Override
    public String ejecutar(Entidad origen, Entidad objetivo, GestorCombate gestor) {
        gestor.aplicarBuffDefensa(objetivo, 10);
        return origen.getNombre() + " bendice a " + objetivo.getNombre()
                + "! +" + 10 + " DEF!";
    }
}
