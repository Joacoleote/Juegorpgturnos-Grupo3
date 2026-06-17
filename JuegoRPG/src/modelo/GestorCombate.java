package modelo;

import modelo.entidades.Dragon;
import modelo.entidades.Enemigo;
import modelo.entidades.Entidad;
import modelo.entidades.Personaje;
import modelo.habilidades.Habilidad;
import modelo.items.Consumible;
import modelo.items.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GestorCombate {

    private Party party;
    private List<Enemigo> enemigos;
    private List<Entidad> ordenTurnos;
    private Random random;

    // Tracking del ultimo turno enemigo para animacion
    private Entidad ultimoObjetivo;
    private boolean ultimoAtaqueEsArea;

    public GestorCombate() {
        this.random = new Random();
        this.ordenTurnos = new ArrayList<>();
    }

    public void iniciarBatalla(Party party, List<Enemigo> enemigos) {
        this.party = party;
        this.enemigos = new ArrayList<>(enemigos);
        for (Personaje p : party.getPersonajes()) {
            p.desactivarDefensa();
        }
        generarOrdenTurnos();
    }

    public List<Entidad> generarOrdenTurnos() {
        ordenTurnos = new ArrayList<>();
        ordenTurnos.addAll(party.getPersonajes());
        ordenTurnos.addAll(enemigos);
        ordenTurnos.sort(Comparator.comparingInt(Entidad::getVelocidad).reversed());
        return new ArrayList<>(ordenTurnos);
    }

    // === ACCIONES COORDINADAS (llamadas por ControladorBatalla) ===

    public String ejecutarAtaque(Entidad atacante, Entidad objetivo) {
        atacante.desactivarDefensa();
        int danio = calcularDanioFisico(atacante, objetivo, 1.0f);
        infligirDanio(objetivo, danio);
        return atacante.getNombre() + " ataca a " + objetivo.getNombre()
                + " causando " + danio + " de danio!";
    }

    public String ejecutarHabilidad(Personaje personaje, Habilidad habilidad, Entidad objetivo) {
        if (!personaje.consumirMana(habilidad.getCosteMana())) {
            return personaje.getNombre() + " no tiene suficiente mana! (necesita "
                    + habilidad.getCosteMana() + " MP)";
        }
        personaje.desactivarDefensa();
        return habilidad.ejecutar(personaje, objetivo, this);
    }

    public String ejecutarDefensa(Personaje personaje) {
        personaje.activarDefensa();
        return personaje.getNombre() + " adopta una postura defensiva! (DEF x2 hasta su proximo turno)";
    }

    public String usarItem(Personaje personaje, Item item, Entidad objetivo) {
        String resultado = item.usar(objetivo);
        if (item instanceof Consumible) {
            party.getInventario().remover(item);
        }
        return resultado;
    }

    public String ejecutarTurnoEnemigo(Enemigo enemigo) {
        enemigo.desactivarDefensa();
        ultimoObjetivo = null;
        ultimoAtaqueEsArea = false;

        // Dragon usa Soplo de Fuego cada 3 turnos
        if (enemigo instanceof Dragon) {
            Dragon dragon = (Dragon) enemigo;
            if (dragon.usarSoplo()) {
                ultimoAtaqueEsArea = true;
                return ejecutarSoploDragon(dragon);
            }
        }

        Entidad objetivo = enemigo.elegirObjetivo(party);
        ultimoObjetivo = objetivo;

        if (objetivo == null) {
            return enemigo.getNombre() + " no encontro objetivo.";
        }

        int danio = calcularDanioFisico(enemigo, objetivo, 1.0f);
        infligirDanio(objetivo, danio);
        return enemigo.getNombre() + " ataca a " + objetivo.getNombre()
                + " causando " + danio + " de danio!";
    }

    private String ejecutarSoploDragon(Dragon dragon) {
        StringBuilder sb = new StringBuilder();
        sb.append(dragon.getNombre()).append(" usa Soplo de Fuego sobre toda la party!\n");
        for (Personaje p : party.obtenerPersonajesVivos()) {
            int danio = calcularDanioFisico(dragon, p, 0.7f);
            infligirDanio(p, danio);
            sb.append("  ").append(p.getNombre()).append(" recibe ").append(danio).append(" de danio!\n");
        }
        return sb.toString().trim();
    }

    // === METODOS DE CALCULO (llamados por Habilidad) ===

    public int calcularDanioFisico(Entidad atacante, Entidad objetivo, float multiplicador) {
        int reduccion = objetivo.estaEnDefensa() ? objetivo.getDefensa() : objetivo.getDefensa() / 2;
        int base = Math.max(1, atacante.getAtaque() - reduccion);
        int variacion = Math.max(1, (int) (base * 0.15));
        int danio = base + random.nextInt(variacion * 2 + 1) - variacion;
        return Math.max(1, (int) (danio * multiplicador));
    }

    public int calcularDanioMagico(Entidad atacante, Entidad objetivo, float multiplicador) {
        int base = Math.max(1, atacante.getAtaque() + atacante.getNivel() * 3 - objetivo.getDefensa() / 3);
        int variacion = Math.max(1, (int) (base * 0.20));
        int danio = base + random.nextInt(variacion * 2 + 1) - variacion;
        return Math.max(1, (int) (danio * multiplicador));
    }

    public int calcularDanioSinDefensa(Entidad atacante, float multiplicador) {
        int base = atacante.getAtaque();
        int variacion = Math.max(1, (int) (base * 0.10));
        int danio = base + random.nextInt(variacion * 2 + 1) - variacion;
        return Math.max(1, (int) (danio * multiplicador));
    }

    // === METODOS DE APLICACION (llamados por Habilidad) ===

    public void infligirDanio(Entidad objetivo, int danio) {
        objetivo.recibirDanio(danio);
    }

    public void aplicarCuracion(Entidad objetivo, int cantidad) {
        objetivo.curar(cantidad);
    }

    public void aplicarBuffAtaque(Entidad entidad, int bonus) {
        entidad.setAtaque(entidad.getAtaque() + bonus);
    }

    public void aplicarBuffDefensa(Entidad entidad, int bonus) {
        entidad.setDefensa(entidad.getDefensa() + bonus);
    }

    public void aplicarDebuffVelocidad(Entidad entidad, int reduccion) {
        entidad.setVelocidad(entidad.getVelocidad() - reduccion);
    }

    // === ESTADO DEL COMBATE ===

    public ResultadoBatalla verificarFinCombate() {
        if (party.todosEliminados()) return ResultadoBatalla.DERROTA;
        for (Enemigo e : enemigos) {
            if (e.estaVivo()) return ResultadoBatalla.CONTINUA;
        }
        return ResultadoBatalla.VICTORIA;
    }

    public int calcularExpTotal() {
        int total = 0;
        for (Enemigo e : enemigos) {
            if (!e.estaVivo()) {
                total += e.getExperienciaOtorgada();
            }
        }
        return total;
    }

    public int calcularOroTotal() {
        int total = 0;
        for (Enemigo e : enemigos) {
            if (!e.estaVivo()) {
                total += e.getOroOtorgado();
            }
        }
        return total;
    }

    // === GETTERS ===

    public Party getParty() { return party; }
    public List<Enemigo> getEnemigos() { return enemigos; }
    public List<Entidad> getOrdenTurnos() { return new ArrayList<>(ordenTurnos); }
    public Entidad getUltimoObjetivo() { return ultimoObjetivo; }
    public boolean isUltimoAtaqueEsArea() { return ultimoAtaqueEsArea; }
}
