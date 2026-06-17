package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Partida implements Serializable {

    private Party party;
    private int escenarioActual;
    private int oro;
    private List<Batalla> historiaBatallas;

    public Partida(Party party) {
        this.party = party;
        this.escenarioActual = 1;
        this.oro = 0;
        this.historiaBatallas = new ArrayList<>();
    }

    public void avanzarEscenario() {
        escenarioActual++;
    }

    public void agregarOro(int cantidad) {
        oro += cantidad;
    }

    public void registrarBatalla(Batalla batalla) {
        historiaBatallas.add(batalla);
    }

    public Party getParty() { return party; }
    public int getEscenarioActual() { return escenarioActual; }
    public int getOro() { return oro; }
    public List<Batalla> getHistoriaBatallas() { return historiaBatallas; }
    public void setEscenarioActual(int e) { escenarioActual = e; }
    public void setOro(int o) { oro = o; }
}
