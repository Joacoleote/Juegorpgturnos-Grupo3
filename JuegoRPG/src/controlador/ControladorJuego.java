package controlador;

import modelo.*;
import modelo.entidades.*;
import modelo.items.*;
import vista.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ControladorJuego {

    private JFrame ventana;
    private CardLayout cardLayout;
    private JPanel contenedor;

    private PantallaInicio pantallaInicio;
    private PantallaCreacionParty pantallaCreacion;
    private PantallaBatalla pantallaBatalla;
    private PantallaResultados pantallaResultados;

    private Partida partidaActual;
    private final GestorPersistencia gestorPersistencia;
    private final GestorRecompensas gestorRecompensas;
    private ControladorBatalla controladorBatalla;

    private static final String PANTALLA_INICIO = "inicio";
    private static final String PANTALLA_CREACION = "creacion";
    private static final String PANTALLA_BATALLA = "batalla";
    private static final String PANTALLA_RESULTADOS = "resultados";

    public ControladorJuego() {
        gestorPersistencia = new GestorPersistencia();
        gestorRecompensas = new GestorRecompensas();
    }

    public void iniciar() {
        ventana = new JFrame("RPG por Turnos — UADE POO 2026 — Grupo 03");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(930, 660);
        ventana.setMinimumSize(new Dimension(850, 620));
        ventana.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);

        pantallaInicio = new PantallaInicio();
        pantallaCreacion = new PantallaCreacionParty();
        pantallaBatalla = new PantallaBatalla();
        pantallaResultados = new PantallaResultados();

        contenedor.add(pantallaInicio, PANTALLA_INICIO);
        contenedor.add(pantallaCreacion, PANTALLA_CREACION);
        contenedor.add(pantallaBatalla, PANTALLA_BATALLA);
        contenedor.add(pantallaResultados, PANTALLA_RESULTADOS);

        ventana.add(contenedor);

        controladorBatalla = new ControladorBatalla(this, pantallaBatalla);

        configurarListeners();

        cardLayout.show(contenedor, PANTALLA_INICIO);
        ventana.setVisible(true);
    }

    private void configurarListeners() {
        pantallaInicio.getBtnNuevaPartida().addActionListener(e -> mostrarCreacionParty());
        pantallaInicio.getBtnCargarPartida().addActionListener(e -> cargarPartida());

        pantallaCreacion.getBtnIniciar().addActionListener(e -> iniciarNuevaPartida());
        pantallaCreacion.getBtnVolver().addActionListener(e -> cardLayout.show(contenedor, PANTALLA_INICIO));

        pantallaResultados.getBtnSiguiente().addActionListener(e -> siguienteBatalla());
        pantallaResultados.getBtnGuardar().addActionListener(e -> guardarPartida());
        pantallaResultados.getBtnMenu().addActionListener(e -> cardLayout.show(contenedor, PANTALLA_INICIO));

        pantallaBatalla.getBtnVolverMenu().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(ventana,
                    "Abandonar la batalla y volver al menu?\nPerderas el progreso de esta batalla.",
                    "Volver al Menu",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                cardLayout.show(contenedor, PANTALLA_INICIO);
            }
        });

        pantallaBatalla.getBtnGuardarBatalla().addActionListener(e -> guardarPartida());
    }

    private void mostrarCreacionParty() {
        pantallaCreacion.limpiar();
        cardLayout.show(contenedor, PANTALLA_CREACION);
    }

    private void iniciarNuevaPartida() {
        Party party = pantallaCreacion.crearParty();
        if (party == null) {
            JOptionPane.showMessageDialog(ventana,
                    "La party debe tener al menos 2 heroes.", "Atencion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        party.getInventario().agregar(new Pocion("Pocion de Vida", 40));
        party.getInventario().agregar(new Pocion("Pocion de Vida", 40));
        party.getInventario().agregar(new PocionMana("Pocion de Mana", 30));
        party.getInventario().agregar(new PocionMana("Pocion de Mana", 30));

        partidaActual = new Partida(party);
        iniciarBatalla();
    }

    private void cargarPartida() {
        if (!gestorPersistencia.existeGuardado()) {
            JOptionPane.showMessageDialog(ventana,
                    "No se encontro ninguna partida guardada.", "Cargar Partida", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            partidaActual = gestorPersistencia.cargar();
            JOptionPane.showMessageDialog(ventana,
                    "Partida cargada! Escenario " + partidaActual.getEscenarioActual(), "Cargado", JOptionPane.INFORMATION_MESSAGE);
            iniciarBatalla();
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(ventana,
                    "Error al cargar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void iniciarBatalla() {
        int escenario = partidaActual.getEscenarioActual();
        List<Enemigo> enemigos = CatalogoJuego.getInstancia().crearEncuentro(escenario);
        String nombreEncuentro = CatalogoJuego.getInstancia().getNombreEncuentro(escenario);

        pantallaBatalla.prepararBatalla(escenario, nombreEncuentro);
        cardLayout.show(contenedor, PANTALLA_BATALLA);

        controladorBatalla.iniciarBatalla(partidaActual.getParty(), enemigos, escenario);
    }

    public void onBatallaTerminada(ResultadoBatalla resultado, GestorCombate gestorCombate) {
        int escenario = partidaActual.getEscenarioActual();

        gestorCombate.limpiarEfectosTemporales();

        Batalla batalla = new Batalla(escenario, gestorCombate.getEnemigos());
        batalla.completar(resultado == ResultadoBatalla.VICTORIA);
        partidaActual.registrarBatalla(batalla);

        if (resultado == ResultadoBatalla.VICTORIA) {
            int expTotal = gestorCombate.calcularExpTotal();
            int oroTotal = gestorCombate.calcularOroTotal();

            List<Personaje> vivos = partidaActual.getParty().obtenerPersonajesVivos();
            Map<Personaje, Boolean> subieroNivel = gestorRecompensas.distribuirXP(vivos, expTotal);
            List<Item> itemsGanados = gestorRecompensas.generarRecompensasItems(escenario);

            for (Item item : itemsGanados) {
                partidaActual.getParty().getInventario().agregar(item);
            }

            partidaActual.agregarOro(oroTotal);
            gestorRecompensas.restaurarVidaParcialment(vivos);
            partidaActual.avanzarEscenario();

            pantallaResultados.mostrarVictoria(expTotal, oroTotal, subieroNivel, itemsGanados, escenario);
        } else {
            pantallaResultados.mostrarDerrota(escenario);
        }

        cardLayout.show(contenedor, PANTALLA_RESULTADOS);
    }

    private void siguienteBatalla() {
        if (partidaActual.getParty().todosEliminados()) {
            cardLayout.show(contenedor, PANTALLA_INICIO);
            return;
        }
        iniciarBatalla();
    }

    private void guardarPartida() {
        try {
            gestorPersistencia.guardar(partidaActual);
            JOptionPane.showMessageDialog(ventana,
                    "Partida guardada correctamente!", "Guardado", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(ventana,
                    "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
