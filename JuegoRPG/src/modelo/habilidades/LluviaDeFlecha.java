package modelo.habilidades;

import modelo.GestorCombate;
import modelo.TipoObjetivo;
import modelo.entidades.Enemigo;
import modelo.entidades.Entidad;

import java.util.List;

public class LluviaDeFlecha extends Habilidad {

    public LluviaDeFlecha() {
        this.nombre = "Lluvia de Flechas";
        this.descripcion = "Dispara una rafaga de flechas que golpea a todos los enemigos.";
        this.costeMana = 25;
        this.tipoObjetivo = TipoObjetivo.TODOS_ENEMIGOS;
    }

    @Override
    public String ejecutar(Entidad origen, Entidad objetivo, GestorCombate gestor) {
        List<Enemigo> enemigos = gestor.getEnemigos();
        StringBuilder sb = new StringBuilder();
        sb.append(origen.getNombre()).append(" usa Lluvia de Flechas!\n");
        for (Enemigo e : enemigos) {
            if (e.estaVivo()) {
                int daño = gestor.calcularDañoFisico(origen, e, 0.9f);
                gestor.infligirDaño(e, daño);
                sb.append("  ").append(e.getNombre()).append(" recibe ").append(daño).append(" de daño!\n");
            }
        }
        return sb.toString().trim();
    }
}
