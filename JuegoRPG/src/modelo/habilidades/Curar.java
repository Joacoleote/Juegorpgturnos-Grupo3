package modelo.habilidades;

import modelo.GestorCombate;
import modelo.TipoObjetivo;
import modelo.entidades.Entidad;

public class Curar extends Habilidad {

    public Curar() {
        this.nombre = "Curar";
        this.descripcion = "Restaura una cantidad significativa de vida a un aliado.";
        this.costeMana = 20;
        this.tipoObjetivo = TipoObjetivo.ALIADO;
    }

    @Override
    public String ejecutar(Entidad origen, Entidad objetivo, GestorCombate gestor) {
        int cantidad = 30 + origen.getNivel() * 10;
        gestor.aplicarCuracion(objetivo, cantidad);
        return origen.getNombre() + " usa Curar en " + objetivo.getNombre()
                + " restaurando " + cantidad + " de vida!";
    }
}
