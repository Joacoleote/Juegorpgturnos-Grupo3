package modelo.habilidades;

import modelo.GestorCombate;
import modelo.TipoObjetivo;
import modelo.entidades.Entidad;

public class GolpeFuerte extends Habilidad {

    public GolpeFuerte() {
        this.nombre = "Golpe Fuerte";
        this.descripcion = "Un golpe devastador que causa 180% del daño fisico normal.";
        this.costeMana = 15;
        this.tipoObjetivo = TipoObjetivo.ENEMIGO;
    }

    @Override
    public String ejecutar(Entidad origen, Entidad objetivo, GestorCombate gestor) {
        int daño = gestor.calcularDañoFisico(origen, objetivo, 1.8f);
        gestor.infligirDaño(objetivo, daño);
        return origen.getNombre() + " usa Golpe Fuerte sobre " + objetivo.getNombre()
                + " causando " + daño + " de daño!";
    }
}
