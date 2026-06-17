package vista;

import modelo.GestorCombate;
import modelo.Party;
import modelo.entidades.Enemigo;
import modelo.entidades.Entidad;
import modelo.entidades.Personaje;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class PantallaBatalla extends JPanel {

    private JLabel lblTitulo;
    private JLabel lblTurno;
    private JLabel lblRonda;

    private PanelArena panelArena;
    private JPanel panelPartyStatus;
    private JPanel panelEnemyStatus;

    private JTextArea areaLog;
    private JScrollPane scrollLog;

    private JButton btnAtacar;
    private JButton btnDefender;
    private JButton btnHabilidad;
    private JButton btnItem;
    private JButton btnVolverMenu;
    private JButton btnGuardarBatalla;
    private JPanel panelBotones;

    public PantallaBatalla() {
        setBackground(new Color(20, 18, 38));
        setLayout(new BorderLayout(4, 4));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // === NORTH: title bar ===
        JPanel panelNorth = new JPanel(new BorderLayout());
        panelNorth.setBackground(new Color(15, 12, 30));
        panelNorth.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        lblTitulo = new JLabel("Batalla", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(255, 215, 0));

        lblRonda = new JLabel("Ronda 1", SwingConstants.RIGHT);
        lblRonda.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblRonda.setForeground(new Color(180, 180, 220));

        btnVolverMenu = new JButton("< Menu");
        btnVolverMenu.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnVolverMenu.setForeground(new Color(220, 180, 180));
        btnVolverMenu.setBackground(new Color(60, 30, 30));
        btnVolverMenu.setFocusPainted(false);
        btnVolverMenu.setOpaque(true);
        btnVolverMenu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 50, 50), 1),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        btnVolverMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnGuardarBatalla = new JButton("💾 Guardar");
        btnGuardarBatalla.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnGuardarBatalla.setForeground(new Color(180, 220, 180));
        btnGuardarBatalla.setBackground(new Color(25, 55, 25));
        btnGuardarBatalla.setFocusPainted(false);
        btnGuardarBatalla.setOpaque(true);
        btnGuardarBatalla.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 100, 50), 1),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        btnGuardarBatalla.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel panelEast = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelEast.setOpaque(false);
        panelEast.add(lblRonda);
        panelEast.add(btnGuardarBatalla);

        panelNorth.add(btnVolverMenu, BorderLayout.WEST);
        panelNorth.add(lblTitulo, BorderLayout.CENTER);
        panelNorth.add(panelEast, BorderLayout.EAST);
        add(panelNorth, BorderLayout.NORTH);

        // === CENTER: arena + side panels ===
        panelArena = new PanelArena();

        panelPartyStatus = new JPanel();
        panelPartyStatus.setLayout(new BoxLayout(panelPartyStatus, BoxLayout.Y_AXIS));
        panelPartyStatus.setBackground(new Color(15, 12, 28));
        panelPartyStatus.setPreferredSize(new Dimension(175, 0));
        panelPartyStatus.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 60, 120), 1),
                "Party", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 11), new Color(180, 160, 220)));

        panelEnemyStatus = new JPanel();
        panelEnemyStatus.setLayout(new BoxLayout(panelEnemyStatus, BoxLayout.Y_AXIS));
        panelEnemyStatus.setBackground(new Color(28, 12, 15));
        panelEnemyStatus.setPreferredSize(new Dimension(155, 0));
        panelEnemyStatus.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(120, 50, 50), 1),
                "Enemigos", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 11), new Color(220, 150, 150)));

        JPanel centroCombate = new JPanel(new BorderLayout(4, 0));
        centroCombate.setOpaque(false);
        centroCombate.add(panelPartyStatus, BorderLayout.WEST);
        centroCombate.add(panelArena, BorderLayout.CENTER);
        centroCombate.add(panelEnemyStatus, BorderLayout.EAST);
        add(centroCombate, BorderLayout.CENTER);

        // === SOUTH: log + buttons ===
        JPanel panelSouth = new JPanel(new BorderLayout(0, 4));
        panelSouth.setOpaque(false);
        panelSouth.setBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6));

        // Log
        areaLog = new JTextArea(5, 50);
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaLog.setBackground(new Color(10, 10, 18));
        areaLog.setForeground(new Color(180, 220, 180));
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);
        areaLog.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 60)));
        scrollLog.setPreferredSize(new Dimension(0, 100));

        // Turn indicator + buttons
        lblTurno = new JLabel("Esperando...", SwingConstants.LEFT);
        lblTurno.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTurno.setForeground(new Color(255, 230, 100));
        lblTurno.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        btnAtacar = crearBotonAccion("⚔ ATACAR", new Color(180, 40, 40));
        btnDefender = crearBotonAccion("🛡 DEFENDER", new Color(40, 80, 180));
        btnHabilidad = crearBotonAccion("✨ HABILIDAD", new Color(120, 40, 180));
        btnItem = crearBotonAccion("🧪 ITEM", new Color(40, 130, 70));

        panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelBotones.setBackground(new Color(15, 12, 28));
        panelBotones.add(lblTurno);
        panelBotones.add(btnAtacar);
        panelBotones.add(btnDefender);
        panelBotones.add(btnHabilidad);
        panelBotones.add(btnItem);

        panelSouth.add(scrollLog, BorderLayout.CENTER);
        panelSouth.add(panelBotones, BorderLayout.SOUTH);
        add(panelSouth, BorderLayout.SOUTH);

        habilitarAcciones(false);
    }

    public void prepararBatalla(int escenario, String nombreEncuentro) {
        lblTitulo.setText("Escenario " + escenario + ": " + nombreEncuentro);
        lblRonda.setText("Ronda 1");
        lblTurno.setText("Iniciando batalla...");
        areaLog.setText("");
        habilitarAcciones(false);
        panelPartyStatus.removeAll();
        panelEnemyStatus.removeAll();
        panelPartyStatus.revalidate();
        panelEnemyStatus.revalidate();
    }

    public void setGestorCombate(GestorCombate gestor) {
        panelArena.setGestorCombate(gestor);
        actualizarEstados(gestor);
    }

    public void actualizarEstados(GestorCombate gestor) {
        panelPartyStatus.removeAll();
        for (Personaje p : gestor.getParty().getPersonajes()) {
            panelPartyStatus.add(crearTarjetaPersonaje(p));
            panelPartyStatus.add(Box.createVerticalStrut(4));
        }
        panelPartyStatus.revalidate();
        panelPartyStatus.repaint();

        panelEnemyStatus.removeAll();
        for (Enemigo e : gestor.getEnemigos()) {
            panelEnemyStatus.add(crearTarjetaEnemigo(e));
            panelEnemyStatus.add(Box.createVerticalStrut(4));
        }
        panelEnemyStatus.revalidate();
        panelEnemyStatus.repaint();

        panelArena.repaint();
    }

    private JPanel crearTarjetaPersonaje(Personaje p) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(p.estaVivo() ? new Color(22, 20, 45) : new Color(30, 20, 20));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(p.estaVivo() ? new Color(60, 50, 100) : new Color(80, 30, 30), 1),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nombre = new JLabel(p.getNombreClase() + " - " + p.getNombre());
        nombre.setFont(new Font("SansSerif", Font.BOLD, 11));
        nombre.setForeground(p.estaVivo() ? new Color(200, 180, 255) : new Color(150, 100, 100));

        JLabel nivel = new JLabel("Nv." + p.getNivel() + " | EXP: " + p.getExperiencia() + "/" + p.getExpParaSiguienteNivel());
        nivel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        nivel.setForeground(new Color(160, 160, 200));

        JPanel hpPanel = crearMiniBar("HP", p.getVidaActual(), p.getVidaMaxima(), new Color(50, 180, 70), new Color(180, 50, 50));
        JPanel mpPanel = crearMiniBar("MP", p.getManaActual(), p.getManaMaxima(), new Color(50, 100, 200), new Color(30, 60, 150));

        card.add(nombre);
        card.add(nivel);
        card.add(hpPanel);
        card.add(mpPanel);
        if (p.estaEnDefensa()) {
            JLabel defLabel = new JLabel("[ DEFENDIENDO ]");
            defLabel.setFont(new Font("SansSerif", Font.ITALIC, 9));
            defLabel.setForeground(new Color(100, 160, 255));
            card.add(defLabel);
        }
        return card;
    }

    private JPanel crearTarjetaEnemigo(Enemigo e) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(e.estaVivo() ? new Color(35, 18, 18) : new Color(25, 25, 25));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(e.estaVivo() ? new Color(100, 40, 40) : new Color(50, 50, 50), 1),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nombre = new JLabel(e.getNombreTipo() + " - " + e.getNombre());
        nombre.setFont(new Font("SansSerif", Font.BOLD, 10));
        nombre.setForeground(e.estaVivo() ? new Color(255, 180, 180) : new Color(120, 100, 100));

        JPanel hpPanel = crearMiniBar("HP", e.getVidaActual(), e.getVidaMaxima(), new Color(180, 50, 50), new Color(80, 20, 20));

        card.add(nombre);
        card.add(hpPanel);
        if (!e.estaVivo()) {
            JLabel muerto = new JLabel("VENCIDO");
            muerto.setFont(new Font("SansSerif", Font.BOLD, 9));
            muerto.setForeground(new Color(180, 60, 60));
            card.add(muerto);
        }
        return card;
    }

    private JPanel crearMiniBar(String etiqueta, int actual, int maximo, Color llena, Color baja) {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 9));
        lbl.setForeground(new Color(160, 160, 180));
        lbl.setPreferredSize(new Dimension(18, 12));

        JPanel barWrapper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 20, 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                if (maximo > 0 && actual > 0) {
                    int fw = Math.max(2, (int) ((double) actual / maximo * getWidth()));
                    float ratio = (float) actual / maximo;
                    g2.setColor(ratio > 0.5f ? llena : ratio > 0.25f ? new Color(220, 180, 30) : baja);
                    g2.fillRoundRect(0, 0, fw, getHeight(), 4, 4);
                }
                g2.setColor(new Color(50, 50, 60));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 4, 4);
            }
        };
        barWrapper.setOpaque(false);
        barWrapper.setPreferredSize(new Dimension(80, 8));

        JLabel numLbl = new JLabel(actual + "/" + maximo);
        numLbl.setFont(new Font("Monospaced", Font.PLAIN, 8));
        numLbl.setForeground(new Color(150, 150, 180));

        row.add(lbl, BorderLayout.WEST);
        row.add(barWrapper, BorderLayout.CENTER);
        row.add(numLbl, BorderLayout.EAST);
        return row;
    }

    public void agregarLog(String mensaje) {
        areaLog.append("» " + mensaje + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    public void setTurnoActual(Entidad entidad) {
        lblTurno.setText("Turno: " + entidad.getNombre());
    }

    public void setRonda(int ronda) {
        lblRonda.setText("Ronda " + ronda);
    }

    public void habilitarAcciones(boolean habilitar) {
        btnAtacar.setEnabled(habilitar);
        btnDefender.setEnabled(habilitar);
        btnHabilidad.setEnabled(habilitar);
        btnItem.setEnabled(habilitar);
    }

    private JButton crearBotonAccion(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.brighter(), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setEnabled(false);
        return btn;
    }

    public PanelArena getPanelArena() { return panelArena; }
    public JButton getBtnAtacar() { return btnAtacar; }
    public JButton getBtnDefender() { return btnDefender; }
    public JButton getBtnHabilidad() { return btnHabilidad; }
    public JButton getBtnItem() { return btnItem; }
    public JButton getBtnVolverMenu() { return btnVolverMenu; }
    public JButton getBtnGuardarBatalla() { return btnGuardarBatalla; }
}
