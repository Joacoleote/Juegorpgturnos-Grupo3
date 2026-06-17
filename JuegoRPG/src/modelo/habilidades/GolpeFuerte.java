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
        int danio = gestor.calcularDanioFisico(origen, objetivo, 1.8f);
        gestor.infligirDanio(objetivo, danio);
        return origen.getNombre() + " usa Golpe Fuerte sobre " + objetivo.getNombre()
                + " causando " + danio + " de danio!";
    }
}
