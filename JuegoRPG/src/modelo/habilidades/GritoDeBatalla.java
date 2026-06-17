package modelo.habilidades;

import modelo.GestorCombate;
import modelo.TipoObjetivo;
import modelo.entidades.Entidad;

public class GritoDeBatalla extends Habilidad {

    public GritoDeBatalla() {
        this.nombre = "Grito de Batalla";
        this.descripcion = "Aumenta el ataque propio en 8 puntos durante la batalla.";
        this.costeMana = 20;
        this.tipoObjetivo = TipoObjetivo.YO_MISMO;
    }

    @Override
    public String ejecutar(Entidad origen, Entidad objetivo, GestorCombate gestor) {
        gestor.aplicarBuffAtaque(origen, 8);
        return origen.getNombre() + " lanza un Grito de Batalla! +8 ATK!";
    }
}
