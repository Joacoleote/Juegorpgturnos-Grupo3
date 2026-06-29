package modelo;

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

    private Entidad ultimoObjetivo;
    private boolean ultimoAtaqueEsArea;

    private List<Runnable> revertirEfectos;

    public GestorCombate() {
        this.random = new Random();
        this.ordenTurnos = new ArrayList<>();
        this.revertirEfectos = new ArrayList<>();
    }

    public void iniciarBatalla(Party party, List<Enemigo> enemigos) {
        this.party = party;
        this.enemigos = new ArrayList<>(enemigos);
        this.revertirEfectos = new ArrayList<>();
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

    public String ejecutarAtaque(Entidad atacante, Entidad objetivo) {
        atacante.desactivarDefensa();
        int daño = calcularDañoFisico(atacante, objetivo, 1.0f);
        infligirDaño(objetivo, daño);
        return atacante.getNombre() + " ataca a " + objetivo.getNombre()
                + " causando " + daño + " de daño!";
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

        Entidad objetivo = enemigo.elegirObjetivo(party);
        ultimoObjetivo = objetivo;

        if (objetivo == null) {
            return enemigo.getNombre() + " no encontro objetivo.";
        }

        int daño = calcularDañoFisico(enemigo, objetivo, 1.0f);
        infligirDaño(objetivo, daño);
        return enemigo.getNombre() + " ataca a " + objetivo.getNombre()
                + " causando " + daño + " de daño!";
    }

    public int calcularDañoFisico(Entidad atacante, Entidad objetivo, float multiplicador) {
        int reduccion = objetivo.estaEnDefensa() ? objetivo.getDefensa() : objetivo.getDefensa() / 2;
        int base = Math.max(1, atacante.getAtaque() - reduccion);
        int variacion = Math.max(1, (int) (base * 0.15));
        int daño = base + random.nextInt(variacion * 2 + 1) - variacion;
        return Math.max(1, (int) (daño * multiplicador));
    }

    public int calcularDañoMagico(Entidad atacante, Entidad objetivo, float multiplicador) {
        int base = Math.max(1, atacante.getAtaque() + atacante.getNivel() * 3 - objetivo.getDefensa() / 3);
        int variacion = Math.max(1, (int) (base * 0.20));
        int daño = base + random.nextInt(variacion * 2 + 1) - variacion;
        return Math.max(1, (int) (daño * multiplicador));
    }

    public int calcularDañoSinDefensa(Entidad atacante, float multiplicador) {
        int base = atacante.getAtaque();
        int variacion = Math.max(1, (int) (base * 0.10));
        int daño = base + random.nextInt(variacion * 2 + 1) - variacion;
        return Math.max(1, (int) (daño * multiplicador));
    }

    public void infligirDaño(Entidad objetivo, int daño) {
        objetivo.recibirDaño(daño);
    }

    public void aplicarCuracion(Entidad objetivo, int cantidad) {
        objetivo.curar(cantidad);
    }

    public void aplicarBuffAtaque(Entidad entidad, int bonus) {
        final int anteriorAtaque = entidad.getAtaque();
        entidad.setAtaque(anteriorAtaque + bonus);
        revertirEfectos.add(new Runnable() {
            @Override public void run() { entidad.setAtaque(anteriorAtaque); }
        });
    }

    public void aplicarBuffDefensa(Entidad entidad, int bonus) {
        final int anteriorDefensa = entidad.getDefensa();
        entidad.setDefensa(anteriorDefensa + bonus);
        revertirEfectos.add(new Runnable() {
            @Override public void run() { entidad.setDefensa(anteriorDefensa); }
        });
    }

    public void aplicarDebuffVelocidad(Entidad entidad, int reduccion) {
        final int anteriorVelocidad = entidad.getVelocidad();
        entidad.setVelocidad(anteriorVelocidad - reduccion);
        revertirEfectos.add(new Runnable() {
            @Override public void run() { entidad.setVelocidad(anteriorVelocidad); }
        });
    }

    public void limpiarEfectosTemporales() {
        for (Runnable revertir : revertirEfectos) {
            revertir.run();
        }
        revertirEfectos.clear();
    }

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

    public Party getParty() { return party; }
    public List<Enemigo> getEnemigos() { return enemigos; }
    public List<Entidad> getOrdenTurnos() { return new ArrayList<>(ordenTurnos); }
    public Entidad getUltimoObjetivo() { return ultimoObjetivo; }
    public boolean isUltimoAtaqueEsArea() { return ultimoAtaqueEsArea; }
}
