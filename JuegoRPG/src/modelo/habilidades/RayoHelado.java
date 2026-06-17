package modelo.habilidades;

import modelo.GestorCombate;
import modelo.TipoObjetivo;
import modelo.entidades.Entidad;

public class RayoHelado extends Habilidad {

    public RayoHelado() {
        this.nombre = "Rayo Helado";
        this.descripcion = "Un rayo de hielo que causa daño magico y reduce la velocidad del enemigo.";
        this.costeMana = 20;
        this.tipoObjetivo = TipoObjetivo.ENEMIGO;
    }

    @Override
    public String ejecutar(Entidad origen, Entidad objetivo, GestorCombate gestor) {
        int daño = gestor.calcularDañoMagico(origen, objetivo, 1.4f);
        gestor.infligirDaño(objetivo, daño);
        gestor.aplicarDebuffVelocidad(objetivo, 3);
        return origen.getNombre() + " lanza Rayo Helado sobre " + objetivo.getNombre()
                + " causando " + daño + " de daño y reduciendo su velocidad!";
    }
}
