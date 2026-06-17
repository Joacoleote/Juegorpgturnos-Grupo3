package modelo.habilidades;

import modelo.GestorCombate;
import modelo.TipoObjetivo;
import modelo.entidades.Entidad;

public class GolpeEscudo extends Habilidad {

    public GolpeEscudo() {
        this.nombre = "Golpe de Escudo";
        this.descripcion = "Golpea con el escudo (150% daño fisico) y gana +8 DEF.";
        this.costeMana = 18;
        this.tipoObjetivo = TipoObjetivo.ENEMIGO;
    }

    @Override
    public String ejecutar(Entidad origen, Entidad objetivo, GestorCombate gestor) {
        int daño = gestor.calcularDañoFisico(origen, objetivo, 1.5f);
        gestor.infligirDaño(objetivo, daño);
        gestor.aplicarBuffDefensa(origen, 8);
        return origen.getNombre() + " usa Golpe de Escudo sobre " + objetivo.getNombre()
                + " causando " + daño + " de daño! (+" + 8 + " DEF propio)";
    }
}
