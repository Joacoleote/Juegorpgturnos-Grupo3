package controlador;

import modelo.*;
import modelo.entidades.*;
import modelo.habilidades.Habilidad;
import modelo.items.Consumible;
import modelo.items.Item;
import vista.PantallaBatalla;

import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ControladorBatalla {

    private final ControladorJuego controladorJuego;
    private final PantallaBatalla pantallaBatalla;
    private final GestorCombate gestorCombate;

    private List<Entidad> ordenTurnos;
    private int indiceTurno;
    private int ronda;

    private Personaje personajeActual;
    private Habilidad habilidadSeleccionada;

    public ControladorBatalla(ControladorJuego controladorJuego, PantallaBatalla pantallaBatalla) {
        this.controladorJuego = controladorJuego;
        this.pantallaBatalla = pantallaBatalla;
        this.gestorCombate = new GestorCombate();
        configurarBotones();
    }

    private void configurarBotones() {
        pantallaBatalla.getBtnAtacar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { accionAtacar(); }
        });
        pantallaBatalla.getBtnDefender().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { accionDefender(); }
        });
        pantallaBatalla.getBtnHabilidad().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { accionHabilidad(); }
        });
        pantallaBatalla.getBtnItem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { accionItem(); }
        });
    }

    public void iniciarBatalla(Party party, List<Enemigo> enemigos, int escenario) {
        gestorCombate.iniciarBatalla(party, enemigos);
        pantallaBatalla.setGestorCombate(gestorCombate);
        ordenTurnos = gestorCombate.generarOrdenTurnos();
        indiceTurno = 0;
        ronda = 1;
        pantallaBatalla.setRonda(ronda);
        pantallaBatalla.agregarLog("=== BATALLA INICIADA ===");
        pantallaBatalla.agregarLog("Orden de turnos por velocidad:");
        for (Entidad e : ordenTurnos) {
            pantallaBatalla.agregarLog("  " + e.getNombre() + " (VEL " + e.getVelocidad() + ")");
        }
        pantallaBatalla.agregarLog("========================");
        procesarSiguienteTurno();
    }

    private void procesarSiguienteTurno() {
        ResultadoBatalla resultado = gestorCombate.verificarFinCombate();
        if (resultado != ResultadoBatalla.CONTINUA) {
            terminarBatalla(resultado);
            return;
        }

        while (indiceTurno < ordenTurnos.size() && !ordenTurnos.get(indiceTurno).estaVivo()) {
            indiceTurno++;
        }

        if (indiceTurno >= ordenTurnos.size()) {
            ronda++;
            pantallaBatalla.setRonda(ronda);
            indiceTurno = 0;
            ordenTurnos = gestorCombate.generarOrdenTurnos();
            pantallaBatalla.agregarLog("--- Ronda " + ronda + " ---");
            for (Personaje p : gestorCombate.getParty().getPersonajes()) {
                p.desactivarDefensa();
            }
            procesarSiguienteTurno();
            return;
        }

        Entidad actual = ordenTurnos.get(indiceTurno);
        pantallaBatalla.setTurnoActual(actual);
        pantallaBatalla.actualizarOrdenTurnos(ordenTurnos, indiceTurno);

        if (actual instanceof Personaje) {
            personajeActual = (Personaje) actual;
            pantallaBatalla.habilitarAcciones(true);
            pantallaBatalla.actualizarEstados(gestorCombate);
        } else if (actual instanceof Enemigo) {
            final Enemigo enemigo = (Enemigo) actual;
            pantallaBatalla.habilitarAcciones(false);
            pantallaBatalla.getPanelArena().desactivarModoSeleccion();
            Timer timer = new Timer(1100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((Timer) e.getSource()).stop();
                    ejecutarTurnoEnemigo(enemigo);
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

private void accionAtacar() {
        pantallaBatalla.habilitarAcciones(false);
        pantallaBatalla.agregarLog("Selecciona un enemigo...");
        pantallaBatalla.getPanelArena().activarModoSeleccionEnemigo(new java.util.function.Consumer<Entidad>() {
            @Override
            public void accept(Entidad objetivo) {
                final String log = gestorCombate.ejecutarAtaque(personajeActual, objetivo);
                pantallaBatalla.getPanelArena().animarAtaque(personajeActual, objetivo, new Runnable() {
                    @Override
                    public void run() {
                        pantallaBatalla.agregarLog(log);
                        pantallaBatalla.actualizarEstados(gestorCombate);
                        finalizarTurnoJugador();
                    }
                });
            }
        });
    }

    private void accionDefender() {
        pantallaBatalla.habilitarAcciones(false);
        String log = gestorCombate.ejecutarDefensa(personajeActual);
        pantallaBatalla.agregarLog(log);
        pantallaBatalla.actualizarEstados(gestorCombate);
        Timer t = new Timer(600, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Timer) e.getSource()).stop();
                finalizarTurnoJugador();
            }
        });
        t.setRepeats(false);
        t.start();
    }

    private void accionHabilidad() {
        List<Habilidad> habilidades = personajeActual.getHabilidades();
        if (habilidades.isEmpty()) {
            pantallaBatalla.agregarLog(personajeActual.getNombre() + " no tiene habilidades disponibles.");
            return;
        }

        String[] opciones = new String[habilidades.size()];
        for (int i = 0; i < habilidades.size(); i++) {
            Habilidad h = habilidades.get(i);
            opciones[i] = h.getNombre() + " (MP:" + h.getCosteMana() + ") - " + h.getDescripcion();
        }

        JList<String> lista = new JList<String>(opciones);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setSelectedIndex(0);
        lista.setFont(new Font("SansSerif", Font.PLAIN, 13));

        int res = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(pantallaBatalla),
                new JScrollPane(lista),
                "Habilidades de " + personajeActual.getNombre(),
                JOptionPane.OK_CANCEL_OPTION);

        if (res != JOptionPane.OK_OPTION || lista.getSelectedIndex() < 0) return;

        habilidadSeleccionada = habilidades.get(lista.getSelectedIndex());

        if (personajeActual.getManaActual() < habilidadSeleccionada.getCosteMana()) {
            pantallaBatalla.agregarLog("Mana insuficiente! Necesitas " + habilidadSeleccionada.getCosteMana() + " MP.");
            return;
        }

        pantallaBatalla.habilitarAcciones(false);

        TipoObjetivo tipo = habilidadSeleccionada.getTipoObjetivo();

        if (tipo == TipoObjetivo.ENEMIGO) {
            pantallaBatalla.agregarLog("Selecciona un enemigo para " + habilidadSeleccionada.getNombre() + "...");
            pantallaBatalla.getPanelArena().activarModoSeleccionEnemigo(new java.util.function.Consumer<Entidad>() {
                @Override
                public void accept(Entidad objetivo) {
                    final String log = gestorCombate.ejecutarHabilidad(personajeActual, habilidadSeleccionada, objetivo);
                    pantallaBatalla.getPanelArena().animarAtaque(personajeActual, objetivo, new Runnable() {
                        @Override
                        public void run() {
                            pantallaBatalla.agregarLog(log);
                            pantallaBatalla.actualizarEstados(gestorCombate);
                            finalizarTurnoJugador();
                        }
                    });
                }
            });
        } else if (tipo == TipoObjetivo.ALIADO) {
            pantallaBatalla.agregarLog("Selecciona un aliado para " + habilidadSeleccionada.getNombre() + "...");
            pantallaBatalla.getPanelArena().activarModoSeleccionAliado(new java.util.function.Consumer<Entidad>() {
                @Override
                public void accept(Entidad objetivo) {
                    final String log = gestorCombate.ejecutarHabilidad(personajeActual, habilidadSeleccionada, objetivo);
                    pantallaBatalla.getPanelArena().animarAtaque(personajeActual, objetivo, new Runnable() {
                        @Override
                        public void run() {
                            pantallaBatalla.agregarLog(log);
                            pantallaBatalla.actualizarEstados(gestorCombate);
                            finalizarTurnoJugador();
                        }
                    });
                }
            });
        } else if (tipo == TipoObjetivo.TODOS_ENEMIGOS) {
            final String log = gestorCombate.ejecutarHabilidad(personajeActual, habilidadSeleccionada, null);
            pantallaBatalla.getPanelArena().animarHabilidadArea(personajeActual, new Runnable() {
                @Override
                public void run() {
                    pantallaBatalla.agregarLog(log);
                    pantallaBatalla.actualizarEstados(gestorCombate);
                    finalizarTurnoJugador();
                }
            });
        } else {
            
            final String log = gestorCombate.ejecutarHabilidad(personajeActual, habilidadSeleccionada, personajeActual);
            pantallaBatalla.agregarLog(log);
            pantallaBatalla.actualizarEstados(gestorCombate);
            Timer t = new Timer(600, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((Timer) e.getSource()).stop();
                    finalizarTurnoJugador();
                }
            });
            t.setRepeats(false);
            t.start();
        }
    }

    private void accionItem() {
        List<Consumible> consumibles = gestorCombate.getParty().getInventario().getConsumibles();
        if (consumibles.isEmpty()) {
            pantallaBatalla.agregarLog("No hay items consumibles en el inventario.");
            return;
        }

        String[] opciones = new String[consumibles.size()];
        for (int i = 0; i < consumibles.size(); i++) {
            Consumible c = consumibles.get(i);
            opciones[i] = c.getNombre() + " - " + c.getDescripcion();
        }

        JList<String> lista = new JList<String>(opciones);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setSelectedIndex(0);
        lista.setFont(new Font("SansSerif", Font.PLAIN, 13));

        int res = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(pantallaBatalla),
                new JScrollPane(lista),
                "Inventario - Selecciona un item",
                JOptionPane.OK_CANCEL_OPTION);

        if (res != JOptionPane.OK_OPTION || lista.getSelectedIndex() < 0) return;

        final Item itemSeleccionado = consumibles.get(lista.getSelectedIndex());
        pantallaBatalla.habilitarAcciones(false);
        pantallaBatalla.agregarLog("Selecciona el objetivo del item...");

        pantallaBatalla.getPanelArena().activarModoSeleccionAliado(new java.util.function.Consumer<Entidad>() {
            @Override
            public void accept(Entidad objetivo) {
                String log = gestorCombate.usarItem(personajeActual, itemSeleccionado, objetivo);
                pantallaBatalla.agregarLog(log);
                pantallaBatalla.actualizarEstados(gestorCombate);
                finalizarTurnoJugador();
            }
        });
    }

    private void finalizarTurnoJugador() {
        indiceTurno++;
        Timer t = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Timer) e.getSource()).stop();
                procesarSiguienteTurno();
            }
        });
        t.setRepeats(false);
        t.start();
    }

private void ejecutarTurnoEnemigo(Enemigo enemigo) {
        final String log = gestorCombate.ejecutarTurnoEnemigo(enemigo);
        final Entidad objetivo = gestorCombate.getUltimoObjetivo();
        final boolean esArea = gestorCombate.isUltimoAtaqueEsArea();

        if (esArea || objetivo == null) {
            pantallaBatalla.getPanelArena().animarHabilidadArea(enemigo, new Runnable() {
                @Override
                public void run() {
                    pantallaBatalla.agregarLog(log);
                    pantallaBatalla.actualizarEstados(gestorCombate);
                    finalizarTurnoEnemigo();
                }
            });
        } else {
            pantallaBatalla.getPanelArena().animarAtaque(enemigo, objetivo, new Runnable() {
                @Override
                public void run() {
                    pantallaBatalla.agregarLog(log);
                    pantallaBatalla.actualizarEstados(gestorCombate);
                    finalizarTurnoEnemigo();
                }
            });
        }
    }

    private void finalizarTurnoEnemigo() {
        indiceTurno++;
        Timer t = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Timer) e.getSource()).stop();
                procesarSiguienteTurno();
            }
        });
        t.setRepeats(false);
        t.start();
    }

    private void terminarBatalla(ResultadoBatalla resultado) {
        pantallaBatalla.habilitarAcciones(false);
        if (resultado == ResultadoBatalla.VICTORIA) {
            pantallaBatalla.agregarLog("=== VICTORIA! La party supero el desafio! ===");
        } else {
            pantallaBatalla.agregarLog("=== DERROTA. La party fue eliminada. ===");
        }
        Timer t = new Timer(1800, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Timer) e.getSource()).stop();
                controladorJuego.onBatallaTerminada(resultado, gestorCombate);
            }
        });
        t.setRepeats(false);
        t.start();
    }

    public GestorCombate getGestorCombate() { return gestorCombate; }
}
