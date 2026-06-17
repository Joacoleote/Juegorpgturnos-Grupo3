package modelo.habilidades;

import modelo.GestorCombate;
import modelo.TipoObjetivo;
import modelo.entidades.Entidad;

public class DefensaFortificada extends Habilidad {

    public DefensaFortificada() {
        this.nombre = "Defensa Fortificada";
        this.descripcion = "Postura defensiva extrema: +25 DEF y activa modo defensa.";
        this.costeMana = 25;
        this.tipoObjetivo = TipoObjetivo.YO_MISMO;
    }

    @Override
    public String ejecutar(Entidad origen, Entidad objetivo, GestorCombate gestor) {
        gestor.aplicarBuffDefensa(origen, 25);
        origen.activarDefensa();
        return origen.getNombre() + " usa Defensa Fortificada! +25 DEF y modo defensa activo!";
    }
}
