import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

// --- 1. MODELO DE DATOS (POO) ---

abstract class Entidad implements Serializable {
    protected String nombre;
    protected int hp, hpMax, mana, ataque, defensa, velocidad, nivel;

    public Entidad(String nombre, int hp, int mana, int ataque, int defensa, int velocidad) {
        this.nombre = nombre;
        this.hp = hp;
        this.hpMax = hp;
        this.mana = mana;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.nivel = 1;
    }

    public boolean estaVivo() { return hp > 0; }

    public String recibirDanio(int danioEntrante) {
        int danioReal;
        if (this instanceof Personaje && ((Personaje)this).defendiendo) {
            danioReal = Math.max(1, danioEntrante - this.defensa);
            ((Personaje)this).defendiendo = false;
        } else {
            danioReal = Math.max(1, danioEntrante - (this.defensa / 2));
        }
        this.hp -= danioReal;
        if (this.hp < 0) this.hp = 0;
        return nombre + " recibió " + danioReal + " de daño.";
    }

    public abstract String realizarAccion(Entidad objetivo);
}

class Personaje extends Entidad {
    private int exp = 0;
    private String clase;
    public boolean defendiendo = false;

    public Personaje(String nombre, String clase, int hp, int mana, int atk, int def, int vel) {
        super(nombre, hp, mana, atk, def, vel);
        this.clase = clase;
    }

    @Override
    public String realizarAccion(Entidad objetivo) {
        return objetivo.recibirDanio(this.ataque);
    }

    public String defender() {
        this.defendiendo = true;
        return nombre + " se defiende y reducirá el daño del próximo ataque.";
    }

    public String usarHabilidad(Entidad objetivo) {
        if (this.mana >= 5) {
            this.mana -= 5;
            return nombre + " usa habilidad especial: " + objetivo.recibirDanio(this.ataque * 2);
        } else {
            return nombre + " no tiene suficiente mana (necesita 10).";
        }
    }

    public void ganarExp(int cantidad) {
        this.exp += cantidad;
        if (this.exp >= 100) {
            this.nivel++;
            this.exp = 0;
            this.hpMax += 20;
            this.hp = hpMax;
            this.ataque += 5;
            this.mana += 10; // Aumentar mana al subir nivel
        }
    }

    @Override
    public String toString() {
        return nombre + " (" + clase + ") LVL:" + nivel + " HP:" + hp + "/" + hpMax + " Mana:" + mana;
    }
}

class Enemigo extends Entidad {
    public Enemigo(String nombre, int hp, int atk, int def, int vel) {
        super(nombre, hp, 0, atk, def, vel);
    }

    @Override
    public String realizarAccion(Entidad objetivo) {
        return "Enemigo " + nombre + " ataca: " + objetivo.recibirDanio(this.ataque);
    }
}

// --- 2. INTERFAZ GRÁFICA Y LÓGICA DE JUEGO ---

public class JuegoRPG extends JFrame {
    private List<Personaje> party = new ArrayList<>();
    private List<Enemigo> enemigos = new ArrayList<>();
    private List<Entidad> ordenTurnos = new ArrayList<>();
    private int turnoActual = 0;

    private JTextArea logArea;
    private JLabel lblStatus;
    private JPanel panelAcciones;

    public JuegoRPG() {
        setTitle("RPG por Turnos - TPI");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Inicializar Jugadores
        party.add(new Personaje("Fuster", "Guerrero", 150, 20, 12, 15, 10));
        party.add(new Personaje("Joaco", "Mago", 80, 100, 20, 5, 12));

        // UI
        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        lblStatus = new JLabel("¡Comienza la batalla!");
        add(lblStatus, BorderLayout.NORTH);

        panelAcciones = new JPanel();
        JButton btnAtacar = new JButton("Atacar");
        JButton btnHabilidad = new JButton("Habilidad");
        JButton btnDefender = new JButton("Defender");
        JButton btnGuardar = new JButton("Guardar Partida");
        
        btnAtacar.addActionListener(e -> procesarAtacar());
        btnHabilidad.addActionListener(e -> procesarHabilidad());
        btnDefender.addActionListener(e -> procesarDefender());
        btnGuardar.addActionListener(e -> guardarPartida());

        panelAcciones.add(btnAtacar);
        panelAcciones.add(btnHabilidad);
        panelAcciones.add(btnDefender);
        panelAcciones.add(btnGuardar);
        add(panelAcciones, BorderLayout.SOUTH);

        iniciarCombate();
    }

    private void iniciarCombate() {
        enemigos.clear();
        enemigos.add(new Enemigo("Orco", 100, 15, 5, 8));
        enemigos.add(new Enemigo("Slime", 50, 10, 2, 15));

        ordenTurnos.clear();
        ordenTurnos.addAll(party);
        ordenTurnos.addAll(enemigos);
        
        // Ordenar por velocidad (Polimorfismo)
        ordenTurnos.sort((a, b) -> Integer.compare(b.velocidad, a.velocidad));
        
        actualizarStatus();
        log("¡Han aparecido enemigos!");
    }

    private void procesarAtacar() {
        Entidad actual = ordenTurnos.get(turnoActual);

        if (actual instanceof Personaje) {
            // El jugador ataca al primer enemigo vivo
            for (Enemigo e : enemigos) {
                if (e.estaVivo()) {
                    log(actual.nombre + " ataca a " + e.nombre + ". " + actual.realizarAccion(e));
                    break;
                }
            }
        }
        pasarTurno();
    }

    private void procesarHabilidad() {
        Entidad actual = ordenTurnos.get(turnoActual);

        if (actual instanceof Personaje) {
            // El jugador usa habilidad en el primer enemigo vivo
            for (Enemigo e : enemigos) {
                if (e.estaVivo()) {
                    log(((Personaje)actual).usarHabilidad(e));
                    break;
                }
            }
        }
        pasarTurno();
    }

    private void procesarDefender() {
        Entidad actual = ordenTurnos.get(turnoActual);

        if (actual instanceof Personaje) {
            log(((Personaje)actual).defender());
        }
        pasarTurno();
    }

    private void pasarTurno() {
        turnoActual++;
        if (turnoActual >= ordenTurnos.size()) turnoActual = 0;

        Entidad siguiente = ordenTurnos.get(turnoActual);
        
        if (!siguiente.estaVivo()) {
            pasarTurno();
            return;
        }

        if (siguiente instanceof Enemigo) {
            // IA Simple: Ataca al primer personaje vivo
            for (Personaje p : party) {
                if (p.estaVivo()) {
                    log(siguiente.realizarAccion(p));
                    break;
                }
            }
            actualizarStatus();
            pasarTurno();
        }
        
        verificarVictoria();
        actualizarStatus();
    }

    private void verificarVictoria() {
        boolean enemigosVivos = enemigos.stream().anyMatch(Entidad::estaVivo);
        boolean partyViva = party.stream().anyMatch(Entidad::estaVivo);

        if (!enemigosVivos) {
            log("¡Victoria! Ganas 50 EXP.");
            party.forEach(p -> p.ganarExp(50));
            iniciarCombate(); // Siguiente oleada
        } else if (!partyViva) {
            log("GAME OVER.");
            panelAcciones.setVisible(false);
        }
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
    }

    private void actualizarStatus() {
        StringBuilder sb = new StringBuilder("<html>(Turno de: " + ordenTurnos.get(turnoActual).nombre + ")<br>");
        for (Personaje p : party) sb.append(p.toString()).append("<br>");
        sb.append("</html>");
        lblStatus.setText(sb.toString());
    }

    // --- PERSISTENCIA ---

    private void guardarPartida() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("partida.dat"))) {
            out.writeObject(party);
            log("Sistema: Partida guardada correctamente.");
        } catch (IOException e) {
            log("Error al guardar.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JuegoRPG().setVisible(true));
    }
}

joaco puto