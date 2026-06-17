package modelo.habilidades;

import modelo.GestorCombate;
import modelo.TipoObjetivo;
import modelo.entidades.Enemigo;
import modelo.entidades.Entidad;

import java.util.List;

public class BolaDeFuego extends Habilidad {

    public BolaDeFuego() {
        this.nombre = "Bola de Fuego";
        this.descripcion = "Lanza una bola de fuego que golpea a todos los enemigos.";
        this.costeMana = 30;
        this.tipoObjetivo = TipoObjetivo.TODOS_ENEMIGOS;
    }

    @Override
    public String ejecutar(Entidad origen, Entidad objetivo, GestorCombate gestor) {
        List<Enemigo> enemigos = gestor.getEnemigos();
        StringBuilder sb = new StringBuilder();
        sb.append(origen.getNombre()).append(" lanza Bola de Fuego!\n");
        for (Enemigo e : enemigos) {
            if (e.estaVivo()) {
                int daño = gestor.calcularDañoMagico(origen, e, 1.3f);
                gestor.infligirDaño(e, daño);
                sb.append("  ").append(e.getNombre()).append(" recibe ").append(daño).append(" de daño!\n");
            }
        }
        return sb.toString().trim();
    }
}
