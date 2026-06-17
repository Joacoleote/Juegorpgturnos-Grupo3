package vista;

import vista.sprite.GestorSprites;
import vista.sprite.TipoAnimacion;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class PantallaInicio extends JPanel {

    private JButton btnNuevaPartida;
    private JButton btnCargarPartida;

    public PantallaInicio() {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 15, 40));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panelTitulo = crearPanelTitulo();
        JPanel panelBotones = crearPanelBotones();

        JLabel footer = new JLabel(
                "UADE - Paradigma Orientado a Objetos 2026 | Grupo 03", SwingConstants.CENTER);
        footer.setForeground(new Color(100, 100, 140));
        footer.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(new PanelShowcaseHeroes(), BorderLayout.NORTH);
        centro.add(panelBotones, BorderLayout.CENTER);

        add(panelTitulo, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                g2d.setFont(new Font("Serif", Font.BOLD, 62));
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 215, 0), getWidth(), 0, new Color(255, 120, 0));
                g2d.setPaint(gp);
                FontMetrics fm = g2d.getFontMetrics();
                String titulo = "RPG POR TURNOS";
                g2d.drawString(titulo, (getWidth() - fm.stringWidth(titulo)) / 2, 90);

                g2d.setFont(new Font("Serif", Font.ITALIC, 20));
                g2d.setColor(new Color(180, 180, 220));
                String sub = "Combate por turnos con heroes, clases y enemigos";
                fm = g2d.getFontMetrics();
                g2d.drawString(sub, (getWidth() - fm.stringWidth(sub)) / 2, 125);

                // Decorative separator
                g2d.setColor(new Color(255, 215, 0, 80));
                g2d.setStroke(new BasicStroke(1.5f));
                int lineW = 300;
                g2d.drawLine((getWidth() - lineW) / 2, 145, (getWidth() + lineW) / 2, 145);
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(900, 170));
        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

        btnNuevaPartida = crearBoton("NUEVA PARTIDA", new Color(60, 120, 200));
        btnCargarPartida = crearBoton("CARGAR PARTIDA", new Color(70, 70, 90));

        panel.add(btnNuevaPartida);
        panel.add(Box.createVerticalStrut(18));
        panel.add(btnCargarPartida);
        return panel;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed() ? color.darker().darker()
                        : getModel().isRollover() ? color.brighter()
                        : color;
                g2d.setColor(c);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 17));
                FontMetrics fm = g2d.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), tx, ty);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(270, 52));
        btn.setMaximumSize(new Dimension(270, 52));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public JButton getBtnNuevaPartida() { return btnNuevaPartida; }
    public JButton getBtnCargarPartida() { return btnCargarPartida; }

    // ── Showcase estático de héroes (frame 0 del IDLE) ─────────────────────
    private static class PanelShowcaseHeroes extends JPanel {

        private static final String[] CLASES = {"Guerrero", "Mago", "Tanque", "Arquero", "Curandero"};
        private static final Color[] BG = {
            new Color(180,  40,  40, 70),
            new Color( 40,  80, 200, 70),
            new Color(100,  60, 180, 70),
            new Color( 40, 160,  60, 70),
            new Color(200, 170,  30, 70)
        };

        PanelShowcaseHeroes() {
            setOpaque(false);
            setPreferredSize(new Dimension(900, 148));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,     RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            GestorSprites gs = GestorSprites.getInstancia();

            final int SPR_W = 88, SPR_H = 88, CELL_W = 112, GAP = 10;
            int totalW = CLASES.length * CELL_W + (CLASES.length - 1) * GAP;
            int sx = (getWidth() - totalW) / 2;
            int sy = (getHeight() - SPR_H - 20) / 2;

            for (int i = 0; i < CLASES.length; i++) {
                int cx = sx + i * (CELL_W + GAP);

                // fondo semitransparente con borde sutil
                g2.setColor(BG[i]);
                g2.fillRoundRect(cx, sy - 6, CELL_W, SPR_H + 12, 14, 14);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.drawRoundRect(cx, sy - 6, CELL_W, SPR_H + 12, 14, 14);

                // frame 0 del IDLE (imagen estática)
                BufferedImage[] frames = gs.getFrames(CLASES[i], TipoAnimacion.IDLE);
                if (frames != null && frames.length > 0 && frames[0] != null) {
                    int imgX = cx + (CELL_W - SPR_W) / 2;
                    g2.drawImage(frames[0], imgX, sy, SPR_W, SPR_H, null);
                }

                // nombre de clase
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.setColor(new Color(210, 195, 255));
                FontMetrics fm = g2.getFontMetrics();
                String lbl = CLASES[i].toUpperCase();
                g2.drawString(lbl, cx + (CELL_W - fm.stringWidth(lbl)) / 2, sy + SPR_H + 16);
            }
        }
    }
}
