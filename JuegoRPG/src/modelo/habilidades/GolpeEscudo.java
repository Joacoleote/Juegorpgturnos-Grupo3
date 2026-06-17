package modelo.habilidades;

import modelo.GestorCombate;
import modelo.TipoObjetivo;
import modelo.entidades.Entidad;

public class GolpeEscudo extends Habilidad {

    public GolpeEscudo() {
        this.nombre = "Golpe de Escudo";
        this.descripcion = "Golpea con el escudo (150% danio fisico) y gana +8 DEF.";
        this.costeMana = 18;
        this.tipoObjetivo = TipoObjetivo.ENEMIGO;
    }

    @Override
    public String ejecutar(Entidad origen, Entidad objetivo, GestorCombate gestor) {
        int danio = gestor.calcularDanioFisico(origen, objetivo, 1.5f);
        gestor.infligirDanio(objetivo, danio);
        gestor.aplicarBuffDefensa(origen, 8);
        return origen.getNombre() + " usa Golpe de Escudo sobre " + objetivo.getNombre()
                + " causando " + danio + " de danio! (+" + 8 + " DEF propio)";
    }
}
