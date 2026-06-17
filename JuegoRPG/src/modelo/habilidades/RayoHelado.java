package modelo.habilidades;

import modelo.GestorCombate;
import modelo.TipoObjetivo;
import modelo.entidades.Entidad;

public class RayoHelado extends Habilidad {

    public RayoHelado() {
        this.nombre = "Rayo Helado";
        this.descripcion = "Un rayo de hielo que causa danio magico y reduce la velocidad del enemigo.";
        this.costeMana = 20;
        this.tipoObjetivo = TipoObjetivo.ENEMIGO;
    }

    @Override
    public String ejecutar(Entidad origen, Entidad objetivo, GestorCombate gestor) {
        int danio = gestor.calcularDanioMagico(origen, objetivo, 1.4f);
        gestor.infligirDanio(objetivo, danio);
        gestor.aplicarDebuffVelocidad(objetivo, 3);
        return origen.getNombre() + " lanza Rayo Helado sobre " + objetivo.getNombre()
                + " causando " + danio + " de danio y reduciendo su velocidad!";
    }
}
