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
                int danio = gestor.calcularDanioMagico(origen, e, 1.3f);
                gestor.infligirDanio(e, danio);
                sb.append("  ").append(e.getNombre()).append(" recibe ").append(danio).append(" de danio!\n");
            }
        }
        return sb.toString().trim();
    }
}
