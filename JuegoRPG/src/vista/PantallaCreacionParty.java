package vista;

import modelo.Party;
import modelo.entidades.*;
import modelo.entidades.Tanque;
import vista.sprite.GestorSprites;
import vista.sprite.TipoAnimacion;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PantallaCreacionParty extends JPanel {

    private static final String[] CLASES = {"Guerrero", "Mago", "Arquero", "Curandero", "Tanque"};
    private static final int MAX_HEROES = 4;
    private static final int MIN_HEROES = 2;

    private JPanel panelHeroes;
    private List<JComboBox<String>> combosClase;
    private List<JTextField> camposNombre;
    private JLabel lblStats;
    private PanelSpritePreview spritePreview;
    private JButton btnAgregar;
    private JButton btnIniciar;
    private JButton btnVolver;

    public PantallaCreacionParty() {
        setBackground(new Color(20, 18, 45));
        setLayout(new BorderLayout(10, 10));
        combosClase = new ArrayList<JComboBox<String>>();
        camposNombre = new ArrayList<JTextField>();
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JLabel titulo = new JLabel("CREAR TU PARTY", SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 36));
        titulo.setForeground(new Color(255, 215, 0));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        panelHeroes = new JPanel();
        panelHeroes.setLayout(new BoxLayout(panelHeroes, BoxLayout.Y_AXIS));
        panelHeroes.setOpaque(false);

        JScrollPane scrollHeroes = new JScrollPane(panelHeroes);
        scrollHeroes.setOpaque(false);
        scrollHeroes.getViewport().setOpaque(false);
        scrollHeroes.setBorder(BorderFactory.createEmptyBorder());

        JPanel panelStats = new JPanel(new BorderLayout(0, 4));
        panelStats.setBackground(new Color(30, 28, 60));
        panelStats.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 80, 150)),
                "Stats de la Clase",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                new Color(200, 180, 255)));

        spritePreview = new PanelSpritePreview();
        panelStats.add(spritePreview, BorderLayout.NORTH);

        lblStats = new JLabel("<html><b>Selecciona una clase para ver sus stats</b></html>", SwingConstants.CENTER);
        lblStats.setForeground(Color.WHITE);
        lblStats.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblStats.setBorder(BorderFactory.createEmptyBorder(4, 10, 10, 10));
        panelStats.add(lblStats, BorderLayout.CENTER);
        panelStats.setPreferredSize(new Dimension(300, 300));

        btnAgregar = crearBoton("+ Agregar Heroe", new Color(40, 140, 60));
        btnIniciar = crearBoton("INICIAR AVENTURA!", new Color(180, 120, 30));
        btnVolver = crearBoton("Volver", new Color(60, 60, 80));

        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarFilaHeroe();
            }
        });

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setOpaque(false);
        panelBotones.add(btnVolver);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnIniciar);

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setOpaque(false);
        panelIzquierdo.add(scrollHeroes, BorderLayout.CENTER);
        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout(15, 0));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        centro.add(panelIzquierdo, BorderLayout.CENTER);
        centro.add(panelStats, BorderLayout.EAST);

        add(titulo, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        agregarFilaHeroe();
        agregarFilaHeroe();
    }

    private void agregarFilaHeroe() {
        if (combosClase.size() >= MAX_HEROES) {
            btnAgregar.setEnabled(false);
            return;
        }

        final int numero = combosClase.size() + 1;
        final JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JLabel lblNum = new JLabel("Heroe " + numero + ":");
        lblNum.setForeground(new Color(200, 180, 255));
        lblNum.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNum.setPreferredSize(new Dimension(65, 25));

        final JComboBox<String> combo = new JComboBox<String>(CLASES);
        combo.setPreferredSize(new Dimension(130, 30));
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarStats((String) combo.getSelectedItem());
            }
        });

        final JTextField campo = new JTextField("Heroe " + numero, 12);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setPreferredSize(new Dimension(120, 30));

        JButton btnEliminar = new JButton("X");
        btnEliminar.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnEliminar.setForeground(new Color(200, 60, 60));
        btnEliminar.setPreferredSize(new Dimension(30, 30));
        btnEliminar.setContentAreaFilled(false);
        btnEliminar.setBorder(BorderFactory.createLineBorder(new Color(200, 60, 60)));
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarFila(fila, combo, campo);
            }
        });

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setForeground(new Color(200, 200, 200));
        lblNombre.setFont(new Font("SansSerif", Font.PLAIN, 13));

        fila.add(lblNum);
        fila.add(combo);
        fila.add(lblNombre);
        fila.add(campo);
        fila.add(btnEliminar);

        combosClase.add(combo);
        camposNombre.add(campo);
        panelHeroes.add(fila);
        panelHeroes.revalidate();
        panelHeroes.repaint();

        actualizarStats((String) combo.getSelectedItem());
        btnAgregar.setEnabled(combosClase.size() < MAX_HEROES);
    }

    private void eliminarFila(JPanel fila, JComboBox<String> combo, JTextField campo) {
        if (combosClase.size() <= MIN_HEROES) {
            JOptionPane.showMessageDialog(this, "La party debe tener al menos " + MIN_HEROES + " heroes.");
            return;
        }
        combosClase.remove(combo);
        camposNombre.remove(campo);
        panelHeroes.remove(fila);
        panelHeroes.revalidate();
        panelHeroes.repaint();
        btnAgregar.setEnabled(true);
    }

    private void actualizarStats(String clase) {
        spritePreview.setClase(clase);
        String stats;
        if ("Guerrero".equals(clase)) {
            stats = "<html><b style='color:#ff6666'>GUERRERO</b><br>"
                    + "HP: 130 &nbsp; MP: 30<br>"
                    + "ATK: 25 &nbsp; DEF: 15 &nbsp; VEL: 10<br>"
                    + "<i>Habilidades: Golpe Fuerte, Grito de Batalla</i><br>"
                    + "Especialista en combate cuerpo a cuerpo.</html>";
        } else if ("Mago".equals(clase)) {
            stats = "<html><b style='color:#6699ff'>MAGO</b><br>"
                    + "HP: 80 &nbsp; MP: 90<br>"
                    + "ATK: 15 &nbsp; DEF: 8 &nbsp; VEL: 13<br>"
                    + "<i>Habilidades: Bola de Fuego (area), Rayo Helado</i><br>"
                    + "Poderoso daño magico en area.</html>";
        } else if ("Arquero".equals(clase)) {
            stats = "<html><b style='color:#66cc66'>ARQUERO</b><br>"
                    + "HP: 95 &nbsp; MP: 50<br>"
                    + "ATK: 22 &nbsp; DEF: 10 &nbsp; VEL: 16<br>"
                    + "<i>Habilidades: Disparo Critico, Lluvia de Flechas</i><br>"
                    + "Alta velocidad e ignora defensa.</html>";
        } else if ("Curandero".equals(clase)) {
            stats = "<html><b style='color:#ffdd44'>CURANDERO</b><br>"
                    + "HP: 90 &nbsp; MP: 80<br>"
                    + "ATK: 12 &nbsp; DEF: 12 &nbsp; VEL: 11<br>"
                    + "<i>Habilidades: Curar, Bendicion (+DEF)</i><br>"
                    + "Soporte vital para la party.</html>";
        } else {
            stats = "<html><b style='color:#aa88ff'>TANQUE</b><br>"
                    + "HP: 160 &nbsp; MP: 40<br>"
                    + "ATK: 18 &nbsp; DEF: 20 &nbsp; VEL: 7<br>"
                    + "<i>Habilidades: Defensa Fortificada, Golpe de Escudo</i><br>"
                    + "Absorbe el daño y protege al equipo.</html>";
        }
        lblStats.setText(stats);
    }

    public Party crearParty() {
        if (combosClase.size() < MIN_HEROES) return null;

        Party party = new Party("Grupo 03");
        for (int i = 0; i < combosClase.size(); i++) {
            String clase = (String) combosClase.get(i).getSelectedItem();
            String nombre = camposNombre.get(i).getText().trim();
            if (nombre.isEmpty()) nombre = clase + " " + (i + 1);

            Personaje p;
            if ("Guerrero".equals(clase)) {
                p = new Guerrero(nombre);
            } else if ("Mago".equals(clase)) {
                p = new Mago(nombre);
            } else if ("Arquero".equals(clase)) {
                p = new Arquero(nombre);
            } else if ("Curandero".equals(clase)) {
                p = new Curandero(nombre);
            } else {
                p = new Tanque(nombre);
            }
            party.agregarPersonaje(p);
        }
        return party;
    }

    public void limpiar() {
        combosClase.clear();
        camposNombre.clear();
        panelHeroes.removeAll();
        agregarFilaHeroe();
        agregarFilaHeroe();
        btnAgregar.setEnabled(true);
        panelHeroes.revalidate();
        panelHeroes.repaint();
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.brighter(), 1),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public JButton getBtnIniciar() { return btnIniciar; }
    public JButton getBtnVolver() { return btnVolver; }

private static class PanelSpritePreview extends JPanel {

        private String clase = "Guerrero";

        PanelSpritePreview() {
            setOpaque(false);
            setPreferredSize(new Dimension(300, 120));
        }

        void setClase(String nuevaClase) {
            clase = nuevaClase;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            GestorSprites gs = GestorSprites.getInstancia();
            BufferedImage[] frames = gs.getFrames(clase, TipoAnimacion.IDLE);
            if (frames == null || frames.length == 0 || frames[0] == null) return;

            final int SPR = 96;
            int x = (getWidth() - SPR) / 2;
            int y = (getHeight() - SPR) / 2;

g2.setColor(colorClase(clase, 80));
            g2.fillOval(x - 6, y + SPR / 2, SPR + 12, SPR / 2 + 4);

            g2.drawImage(frames[0], x, y, SPR, SPR, null);
        }

        private static Color colorClase(String clase, int alpha) {
            return switch (clase) {
                case "Guerrero"  -> new Color(180,  40,  40, alpha);
                case "Mago"      -> new Color( 40,  80, 200, alpha);
                case "Arquero"   -> new Color( 40, 160,  60, alpha);
                case "Curandero" -> new Color(200, 170,  30, alpha);
                case "Tanque"    -> new Color(100,  60, 180, alpha);
                default          -> new Color(120, 120, 120, alpha);
            };
        }
    }
}
