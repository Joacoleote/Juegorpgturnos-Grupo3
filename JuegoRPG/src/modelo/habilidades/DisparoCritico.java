package modelo.habilidades;

import modelo.GestorCombate;
import modelo.TipoObjetivo;
import modelo.entidades.Entidad;

public class DisparoCritico extends Habilidad {

    public DisparoCritico() {
        this.nombre = "Disparo Critico";
        this.descripcion = "Un disparo preciso que ignora la defensa del objetivo.";
        this.costeMana = 20;
        this.tipoObjetivo = TipoObjetivo.ENEMIGO;
    }

    @Override
    public String ejecutar(Entidad origen, Entidad objetivo, GestorCombate gestor) {
        int danio = gestor.calcularDanioSinDefensa(origen, 2.0f);
        gestor.infligirDanio(objetivo, danio);
        return origen.getNombre() + " usa Disparo Critico sobre " + objetivo.getNombre()
                + " causando " + danio + " de danio (ignora defensa)!";
    }
}
