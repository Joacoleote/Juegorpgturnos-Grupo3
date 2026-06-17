package vista;

import modelo.entidades.Personaje;
import modelo.items.Item;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class PantallaResultados extends JPanel {

    private JLabel lblTitulo;
    private JPanel panelContenido;
    private JButton btnSiguiente;
    private JButton btnGuardar;
    private JButton btnMenu;
    private boolean esVictoria;

    public PantallaResultados() {
        setLayout(new BorderLayout(10, 10));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        lblTitulo = new JLabel("", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 48));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setOpaque(false);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));

        JScrollPane scroll = new JScrollPane(panelContenido);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        btnSiguiente = crearBoton("SIGUIENTE BATALLA", new Color(50, 140, 50));
        btnGuardar = crearBoton("GUARDAR PARTIDA", new Color(50, 80, 160));
        btnMenu = crearBoton("MENU PRINCIPAL", new Color(60, 60, 80));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.setOpaque(false);
        panelBotones.add(btnSiguiente);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnMenu);

        add(lblTitulo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public void mostrarVictoria(int expTotal, int oroTotal,
                                Map<Personaje, Boolean> subieroNivel,
                                List<Item> itemsGanados, int escenario) {
        esVictoria = true;
        setBackground(new Color(15, 30, 15));
        lblTitulo.setText("¡VICTORIA!");
        lblTitulo.setForeground(new Color(100, 255, 100));

        panelContenido.removeAll();

        agregarLinea("Escenario " + escenario + " completado!", new Color(255, 215, 0), Font.BOLD, 20);
        agregarLinea("Experiencia ganada: " + expTotal + " XP  |  Oro: " + oroTotal, new Color(200, 200, 100), Font.PLAIN, 15);
        agregarSeparador();

        agregarLinea("Progreso de la Party:", new Color(180, 180, 255), Font.BOLD, 15);
        for (Map.Entry<Personaje, Boolean> entry : subieroNivel.entrySet()) {
            Personaje p = entry.getKey();
            boolean subioNivel = entry.getValue();
            String texto = p.getNombreClase() + " " + p.getNombre() +
                    " | Nv." + p.getNivel() +
                    " | EXP: " + p.getExperiencia() + "/" + p.getExpParaSiguienteNivel() +
                    (subioNivel ? "  ★ ¡NIVEL!" : "");
            Color color = subioNivel ? new Color(255, 215, 0) : new Color(180, 220, 180);
            agregarLinea(texto, color, subioNivel ? Font.BOLD : Font.PLAIN, 14);
        }

        if (!itemsGanados.isEmpty()) {
            agregarSeparador();
            agregarLinea("Items obtenidos:", new Color(180, 255, 180), Font.BOLD, 15);
            for (Item item : itemsGanados) {
                agregarLinea("  + " + item.getNombre() + " — " + item.getDescripcion(),
                        new Color(150, 200, 150), Font.PLAIN, 13);
            }
        }

        btnSiguiente.setEnabled(true);
        btnSiguiente.setText("SIGUIENTE BATALLA");
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    public void mostrarDerrota(int escenario) {
        esVictoria = false;
        setBackground(new Color(30, 10, 10));
        lblTitulo.setText("DERROTA");
        lblTitulo.setForeground(new Color(220, 50, 50));

        panelContenido.removeAll();

        agregarLinea("La party fue derrotada en el Escenario " + escenario + ".", new Color(200, 100, 100), Font.BOLD, 17);
        agregarSeparador();
        agregarLinea("El viaje termino aqui...", new Color(180, 130, 130), Font.ITALIC, 15);
        agregarLinea("Puedes volver al menu e iniciar una nueva partida.", new Color(160, 160, 160), Font.PLAIN, 13);

        btnSiguiente.setEnabled(false);
        btnSiguiente.setText("(Party eliminada)");
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void agregarLinea(String texto, Color color, int estilo, int size) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", estilo, size));
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        panelContenido.add(lbl);
    }

    private void agregarSeparador() {
        panelContenido.add(Box.createVerticalStrut(8));
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(80, 80, 100));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        panelContenido.add(sep);
        panelContenido.add(Box.createVerticalStrut(8));
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.brighter(), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public JButton getBtnSiguiente() { return btnSiguiente; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnMenu() { return btnMenu; }
}
