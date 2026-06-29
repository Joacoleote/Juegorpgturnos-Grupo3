package vista;

import modelo.GestorCombate;
import modelo.Party;
import modelo.entidades.*;
import vista.sprite.GestorSprites;
import vista.sprite.TipoAnimacion;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class PanelArena extends JPanel {

    private static final int BOX_W = 62;
    private static final int BOX_H = 78;
    private static final int HERO_X  = 60;
    private static final int ENEMY_X = 530;

private static final int SPR_W = 80;
    private static final int SPR_H = 96;

    private GestorCombate gestorCombate;
    private Party party;
    private List<Enemigo> enemigos;

private Entidad entidadAnimada;
    private int animOffsetX;
    private int animOffsetY;
    private Entidad entidadGolpeada;
    private boolean mostrarGolpe;

private boolean modoSeleccionEnemigo;
    private boolean modoSeleccionAliado;
    private Consumer<Entidad> callbackSeleccion;

    private final Map<Entidad, Point> posiciones = new HashMap<Entidad, Point>();

private final BufferedImage imagenFondo;

private final GestorSprites gestorSprites = GestorSprites.getInstancia();
    private final Map<Entidad, EstadoAnim> estadosAnim = new HashMap<Entidad, EstadoAnim>();
    private final javax.swing.Timer frameTimer;

private static class EstadoAnim {
        TipoAnimacion tipo = TipoAnimacion.IDLE;
        int frame = 0;
        boolean bucle = true;
        boolean terminado = false;

        void setAnimacion(TipoAnimacion t, boolean loop) {
            tipo = t;
            frame = 0;
            bucle = loop;
            terminado = false;
        }

        void avanzar(int numFrames) {
            if (numFrames <= 0 || terminado) return;
            frame++;
            if (frame >= numFrames) {
                if (bucle) {
                    frame = 0;
                } else {
                    frame = numFrames - 1;
                    terminado = true;
                }
            }
        }

        int frameSeguro(int numFrames) {
            return Math.min(frame, Math.max(0, numFrames - 1));
        }
    }

public PanelArena() {
        setPreferredSize(new Dimension(700, 300));
        setBackground(new Color(25, 20, 42));

        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("recursos/fondo.png"));
        } catch (IOException ignored) {}
        imagenFondo = img;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                manejarClick(e.getX(), e.getY());
            }
        });

frameTimer = new javax.swing.Timer(125, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                avanzarFrames();
                repaint();
            }
        });
        frameTimer.start();
    }

private EstadoAnim getEstado(Entidad entidad) {
        if (!estadosAnim.containsKey(entidad)) {
            estadosAnim.put(entidad, new EstadoAnim());
        }
        return estadosAnim.get(entidad);
    }

    private void setAnimacion(Entidad entidad, TipoAnimacion tipo, boolean bucle) {
        EstadoAnim e = getEstado(entidad);
        if (e.tipo != tipo || (tipo == TipoAnimacion.IDLE && e.terminado)) {
            e.setAnimacion(tipo, bucle);
        }
    }

    private void avanzarFrames() {
        if (party == null || enemigos == null) return;

        for (Personaje p : party.getPersonajes()) {
            avanzarEntidad(p, obtenerClasePersonaje(p));
        }
        for (Enemigo e : enemigos) {
            avanzarEntidad(e, obtenerClaseEnemigo(e));
        }
    }

    private void avanzarEntidad(Entidad entidad, String clase) {
        EstadoAnim estado = getEstado(entidad);

if (!entidad.estaVivo() && estado.tipo != TipoAnimacion.MUERTE) {
            estado.setAnimacion(TipoAnimacion.MUERTE, false);
        }

if (estado.terminado && estado.tipo != TipoAnimacion.MUERTE) {
            estado.setAnimacion(TipoAnimacion.IDLE, true);
        }

if (estado.tipo == TipoAnimacion.IDLE) return;

        if (gestorSprites.tieneSprites(clase)) {
            BufferedImage[] frames = gestorSprites.getFrames(clase, estado.tipo);
            int n = (frames != null) ? frames.length : 0;
            estado.avanzar(n);
        }
    }

    private String obtenerClasePersonaje(Personaje p) {
        return p.getNombreClase();
    }

    private String obtenerClaseEnemigo(Enemigo e) {
        return e.getNombreTipo();
    }

public void setGestorCombate(GestorCombate gestor) {
        this.gestorCombate = gestor;
        this.party = gestor.getParty();
        this.enemigos = gestor.getEnemigos();
        estadosAnim.clear();
        recalcularPosiciones();
        repaint();
    }

    private void recalcularPosiciones() {
        posiciones.clear();
        if (party == null || enemigos == null) return;

        int h = Math.max(getHeight(), 300);

        List<Personaje> personajes = party.getPersonajes();
        int n = personajes.size();
        int spacing = Math.min(90, (h - 60) / Math.max(n, 1));
        int heroStartY = (h - n * spacing) / 2 - 10;
        for (int i = 0; i < n; i++) {
            posiciones.put(personajes.get(i), new Point(HERO_X, heroStartY + i * spacing));
        }

        int m = enemigos.size();
        int espacing = Math.min(90, (h - 60) / Math.max(m, 1));
        int enemyStartY = (h - m * espacing) / 2 - 10;
        for (int i = 0; i < m; i++) {
            posiciones.put(enemigos.get(i), new Point(ENEMY_X, enemyStartY + i * espacing));
        }
    }

    public void activarModoSeleccionEnemigo(Consumer<Entidad> callback) {
        modoSeleccionEnemigo = true;
        modoSeleccionAliado = false;
        callbackSeleccion = callback;
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        repaint();
    }

    public void activarModoSeleccionAliado(Consumer<Entidad> callback) {
        modoSeleccionAliado = true;
        modoSeleccionEnemigo = false;
        callbackSeleccion = callback;
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        repaint();
    }

    public void desactivarModoSeleccion() {
        modoSeleccionEnemigo = false;
        modoSeleccionAliado = false;
        callbackSeleccion = null;
        setCursor(Cursor.getDefaultCursor());
        repaint();
    }

    public void animarAtaque(Entidad atacante, Entidad objetivo, Runnable alTerminar) {
        recalcularPosiciones();
        Point posAtacante = posiciones.get(atacante);
        Point posObjetivo = objetivo != null ? posiciones.get(objetivo) : null;

        if (posAtacante == null || posObjetivo == null) {
            alTerminar.run();
            return;
        }

        final int tDX = (int) ((posObjetivo.x - posAtacante.x) * 0.55);
        final int tDY = (int) ((posObjetivo.y - posAtacante.y) * 0.55);

        entidadAnimada = atacante;
        entidadGolpeada = null;
        mostrarGolpe = false;
        animOffsetX = 0;
        animOffsetY = 0;

        setAnimacion(atacante, TipoAnimacion.ATAQUE, false);

        final int[] step = {0};
        final int totalSteps = 22;

        javax.swing.Timer timer = new javax.swing.Timer(32, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                step[0]++;
                if (step[0] <= 10) {
                    animOffsetX = tDX * step[0] / 10;
                    animOffsetY = tDY * step[0] / 10;
                } else if (step[0] == 11) {
                    animOffsetX = tDX;
                    animOffsetY = tDY;
                    mostrarGolpe = true;
                    entidadGolpeada = objetivo;
                    setAnimacion(objetivo, TipoAnimacion.RECIBE_DAÑO, false);
                } else if (step[0] <= totalSteps) {
                    mostrarGolpe = false;
                    int back = step[0] - 11;
                    animOffsetX = (int) (tDX * (1.0 - (double) back / (totalSteps - 11)));
                    animOffsetY = (int) (tDY * (1.0 - (double) back / (totalSteps - 11)));
                } else {
                    timer.stop();
                    entidadAnimada = null;
                    entidadGolpeada = null;
                    mostrarGolpe = false;
                    animOffsetX = 0;
                    animOffsetY = 0;
                    repaint();
                    alTerminar.run();
                    return;
                }
                repaint();
            }
        });
        timer.start();
    }

    public void animarHabilidadArea(Entidad atacante, Runnable alTerminar) {
        entidadAnimada = atacante;
        mostrarGolpe = false;
        animOffsetX = 0;
        animOffsetY = 0;

        setAnimacion(atacante, TipoAnimacion.ATAQUE, false);

        final int[] step = {0};
        javax.swing.Timer timer = new javax.swing.Timer(45, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                step[0]++;
                if (step[0] <= 6) {
                    animOffsetX = step[0] * 5;
                } else if (step[0] <= 10) {
                    mostrarGolpe = true;
                    entidadGolpeada = null;
                    animOffsetX = 0;
                    
                    if (enemigos != null) {
                        for (Enemigo en : enemigos) {
                            if (en.estaVivo()) setAnimacion(en, TipoAnimacion.RECIBE_DAÑO, false);
                        }
                    }
                } else {
                    timer.stop();
                    entidadAnimada = null;
                    mostrarGolpe = false;
                    repaint();
                    alTerminar.run();
                    return;
                }
                repaint();
            }
        });
        timer.start();
    }

@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        recalcularPosiciones();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        dibujarFondo(g2);
        if (gestorCombate == null) return;

        for (Personaje p : party.getPersonajes()) {
            Point pos = posiciones.get(p);
            if (pos == null) continue;
            int dx = (entidadAnimada == p) ? animOffsetX : 0;
            int dy = (entidadAnimada == p) ? animOffsetY : 0;
            boolean golpe = mostrarGolpe && (entidadGolpeada == p || entidadGolpeada == null);
            dibujarPersonaje(g2, p, pos.x + dx, pos.y + dy, golpe);
        }

        for (Enemigo e : enemigos) {
            Point pos = posiciones.get(e);
            if (pos == null) continue;
            int dx = (entidadAnimada == e) ? animOffsetX : 0;
            int dy = (entidadAnimada == e) ? animOffsetY : 0;
            boolean golpe = mostrarGolpe && (entidadGolpeada == e || entidadGolpeada == null);
            boolean selec = modoSeleccionEnemigo && e.estaVivo();
            dibujarEnemigo(g2, e, pos.x + dx, pos.y + dy, golpe, selec);
        }

        dibujarIndicadoresSeleccion(g2);
    }

    private void dibujarFondo(Graphics2D g2) {
        if (imagenFondo != null) {
            g2.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), null);
        } else {
            
            GradientPaint gp = new GradientPaint(0, 0, new Color(18, 14, 32), getWidth(), getHeight(), new Color(32, 18, 50));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

g2.setColor(new Color(0, 0, 0, 80));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{8, 6}, 0));
        g2.drawLine(getWidth() / 2, 10, getWidth() / 2, getHeight() - 10);

        g2.setFont(new Font("Serif", Font.BOLD, 18));
        g2.setColor(new Color(255, 255, 255, 180));
        g2.drawString("VS", getWidth() / 2 - 11, getHeight() / 2 + 7);
    }

    private void dibujarPersonaje(Graphics2D g2, Personaje p, int x, int y, boolean golpeado) {
        String clase = p.getNombreClase();

        if (!p.estaVivo()) {
            if (gestorSprites.tieneSprites(clase)) {
                dibujarSprite(g2, clase, TipoAnimacion.MUERTE, getEstado(p), x, y, false, false);
            } else {
                dibujarRectanguloMuerto(g2, x, y, "K.O.");
            }
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(new Color(160, 160, 160));
            String nombre = cortar(p.getNombre(), 9);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(nombre, x + (BOX_W - fm.stringWidth(nombre)) / 2, y + BOX_H + 13);
            return;
        }

        if (gestorSprites.tieneSprites(clase)) {
            dibujarSprite(g2, clase, getEstado(p).tipo, getEstado(p), x, y, golpeado, p.estaEnDefensa());
        } else {
            dibujarRectanguloPersonaje(g2, p, x, y, golpeado);
        }

g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.setColor(new Color(220, 220, 255));
        String nombre = cortar(p.getNombre(), 9);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(nombre, x + (BOX_W - fm.stringWidth(nombre)) / 2, y + BOX_H + 13);
        dibujarBarra(g2, x, y + BOX_H + 16, BOX_W, 6,
                p.getVidaActual(), p.getVidaMaxima(), new Color(50, 200, 80), new Color(200, 50, 50));
        dibujarBarra(g2, x, y + BOX_H + 25, BOX_W, 5,
                p.getManaActual(), p.getManaMaxima(), new Color(60, 120, 220), new Color(30, 60, 120));
    }

    private void dibujarEnemigo(Graphics2D g2, Enemigo e, int x, int y, boolean golpeado, boolean seleccionable) {
        String tipo = e.getNombreTipo();

        if (!e.estaVivo()) {
            if (gestorSprites.tieneSprites(tipo)) {
                dibujarSprite(g2, tipo, TipoAnimacion.MUERTE, getEstado(e), x, y, false, false);
            } else {
                dibujarRectanguloMuerto(g2, x, y, "VENCIDO");
            }
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(new Color(160, 160, 160));
            String nombre = cortar(e.getNombre(), 9);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(nombre, x + (BOX_W - fm.stringWidth(nombre)) / 2, y + BOX_H + 13);
            return;
        }

        if (seleccionable) {
            g2.setColor(new Color(255, 220, 50, 80));
            g2.fillRoundRect(x - 5, y - 5, BOX_W + 10, BOX_H + 10, 16, 16);
            g2.setColor(new Color(255, 200, 50));
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{4, 3}, 0));
            g2.drawRoundRect(x - 5, y - 5, BOX_W + 10, BOX_H + 10, 16, 16);
        }

        if (gestorSprites.tieneSprites(tipo)) {
            dibujarSprite(g2, tipo, getEstado(e).tipo, getEstado(e), x, y, golpeado, false);
        } else {
            dibujarRectanguloEnemigo(g2, e, x, y, golpeado, seleccionable);
        }

        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.setColor(new Color(255, 200, 200));
        String nombre = cortar(e.getNombre(), 9);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(nombre, x + (BOX_W - fm.stringWidth(nombre)) / 2, y + BOX_H + 13);
        dibujarBarra(g2, x, y + BOX_H + 16, BOX_W, 6,
                e.getVidaActual(), e.getVidaMaxima(), new Color(220, 60, 60), new Color(120, 20, 20));
    }

private void dibujarSprite(Graphics2D g2, String clase, TipoAnimacion animTipo,
                                EstadoAnim estado, int x, int y,
                                boolean golpeado, boolean enDefensa) {
        BufferedImage[] frames = gestorSprites.getFrames(clase, animTipo);
        BufferedImage frame = null;
        if (frames != null && frames.length > 0) {
            frame = frames[estado.frameSeguro(frames.length)];
        }
        if (frame == null) return;

int sx = x - (SPR_W - BOX_W) / 2;
        int sy = y - (SPR_H - BOX_H) / 2;

        boolean esEnemigo = isEnemigo(clase);

        if (golpeado) {
            
            g2.setColor(new Color(255, 255, 255, 120));
            g2.fillRoundRect(sx, sy, SPR_W, SPR_H, 8, 8);
        }

        if (esEnemigo) {
            
            AffineTransform at = AffineTransform.getTranslateInstance(sx + SPR_W, sy);
            at.scale(-1, 1);
            at.scale((double) SPR_W / frame.getWidth(), (double) SPR_H / frame.getHeight());
            g2.drawImage(frame, at, null);
        } else {
            g2.drawImage(frame, sx, sy, SPR_W, SPR_H, null);
        }

        if (enDefensa) {
            g2.setColor(new Color(80, 160, 255, 100));
            g2.fillRoundRect(sx, sy, SPR_W, SPR_H, 8, 8);
            g2.setFont(new Font("SansSerif", Font.BOLD, 9));
            g2.setColor(Color.WHITE);
            g2.drawString("DEF", sx + SPR_W / 2 - 8, sy + 12);
        }
    }

    private boolean isEnemigo(String clase) {
        return "Goblin".equals(clase) || "Slime".equals(clase);
    }

private void dibujarRectanguloMuerto(Graphics2D g2, int x, int y, String etiqueta) {
        g2.setColor(new Color(70, 70, 70, 180));
        g2.fillRoundRect(x, y + BOX_H / 2, BOX_W, BOX_H / 2, 10, 10);
        g2.setFont(new Font("SansSerif", Font.BOLD, 9));
        g2.setColor(new Color(200, 60, 60));
        g2.drawString(etiqueta, x + 4, y + BOX_H - 8);
    }

    private void dibujarRectanguloPersonaje(Graphics2D g2, Personaje p, int x, int y, boolean golpeado) {
        Color base = golpeado ? Color.WHITE : colorPersonaje(p);
        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillRoundRect(x + 3, y + 4, BOX_W, BOX_H, 12, 12);
        g2.setColor(base);
        g2.fillRoundRect(x, y, BOX_W, BOX_H, 12, 12);
        GradientPaint shine = new GradientPaint(x, y, new Color(255, 255, 255, 50), x, y + 30, new Color(255, 255, 255, 0));
        g2.setPaint(shine);
        g2.fillRoundRect(x, y, BOX_W, 30, 12, 12);
        g2.setColor(base.brighter().brighter());
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(x, y, BOX_W, BOX_H, 12, 12);
        String letra = p.getNombreClase().substring(0, 1);
        g2.setFont(new Font("Serif", Font.BOLD, 30));
        g2.setColor(new Color(255, 255, 255, 220));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(letra, x + (BOX_W - fm.stringWidth(letra)) / 2, y + BOX_H / 2 + 10);
        if (p.estaEnDefensa()) {
            g2.setColor(new Color(80, 160, 255, 140));
            g2.fillRoundRect(x, y, BOX_W, BOX_H, 12, 12);
            g2.setFont(new Font("SansSerif", Font.BOLD, 9));
            g2.setColor(Color.WHITE);
            g2.drawString("DEFENSA", x + 4, y + 12);
        }
    }

    private void dibujarRectanguloEnemigo(Graphics2D g2, Enemigo e, int x, int y,
                                           boolean golpeado, boolean seleccionable) {
        Color base = golpeado ? Color.WHITE : colorEnemigo(e);
        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillRoundRect(x + 3, y + 4, BOX_W, BOX_H, 12, 12);
        g2.setColor(base);
        g2.fillRoundRect(x, y, BOX_W, BOX_H, 12, 12);
        GradientPaint shine = new GradientPaint(x, y, new Color(255, 255, 255, 40), x, y + 30, new Color(0, 0, 0, 0));
        g2.setPaint(shine);
        g2.fillRoundRect(x, y, BOX_W, 30, 12, 12);
        g2.setColor(seleccionable ? new Color(255, 220, 50) : base.brighter().brighter());
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(x, y, BOX_W, BOX_H, 12, 12);
        String letra = e.getNombreTipo().substring(0, 1);
        g2.setFont(new Font("Serif", Font.BOLD, 30));
        g2.setColor(new Color(255, 255, 255, 220));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(letra, x + (BOX_W - fm.stringWidth(letra)) / 2, y + BOX_H / 2 + 10);
    }

    private void dibujarIndicadoresSeleccion(Graphics2D g2) {
        if (modoSeleccionEnemigo) {
            g2.setColor(new Color(255, 100, 100, 40));
            g2.fillRect(getWidth() / 2 + 5, 0, getWidth() / 2 - 5, getHeight());
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.setColor(new Color(255, 150, 150));
            g2.drawString("Selecciona un enemigo", getWidth() / 2 + 20, 22);
        } else if (modoSeleccionAliado) {
            g2.setColor(new Color(100, 200, 100, 40));
            g2.fillRect(0, 0, getWidth() / 2 - 5, getHeight());
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.setColor(new Color(150, 255, 150));
            g2.drawString("Selecciona un aliado", 10, 22);
        }
    }

private void manejarClick(int mx, int my) {
        if (callbackSeleccion == null) return;
        if (modoSeleccionEnemigo) {
            for (Enemigo e : enemigos) {
                if (!e.estaVivo()) continue;
                Point p = posiciones.get(e);
                if (p != null && dentroDelBox(mx, my, p)) {
                    Consumer<Entidad> cb = callbackSeleccion;
                    desactivarModoSeleccion();
                    cb.accept(e);
                    return;
                }
            }
        } else if (modoSeleccionAliado && party != null) {
            for (Personaje per : party.obtenerPersonajesVivos()) {
                Point p = posiciones.get(per);
                if (p != null && dentroDelBox(mx, my, p)) {
                    Consumer<Entidad> cb = callbackSeleccion;
                    desactivarModoSeleccion();
                    cb.accept(per);
                    return;
                }
            }
        }
    }

    private boolean dentroDelBox(int mx, int my, Point pos) {
        int sx = pos.x - (SPR_W - BOX_W) / 2;
        int sy = pos.y - (SPR_H - BOX_H) / 2;
        return mx >= sx && mx <= sx + SPR_W && my >= sy && my <= sy + SPR_H;
    }

    private void dibujarBarra(Graphics2D g2, int x, int y, int w, int h,
                               int actual, int maximo, Color llena, Color vacia) {
        g2.setColor(new Color(20, 20, 20));
        g2.fillRoundRect(x, y, w, h, 4, 4);
        if (maximo > 0 && actual > 0) {
            int fw = Math.max(2, (int) ((double) actual / maximo * w));
            float r = (float) actual / maximo;
            Color c = r > 0.5f ? llena : r > 0.25f ? new Color(220, 190, 30) : vacia;
            g2.setColor(c);
            g2.fillRoundRect(x, y, fw, h, 4, 4);
        }
        g2.setColor(new Color(60, 60, 60));
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(x, y, w, h, 4, 4);
    }

    private Color colorPersonaje(Personaje p) {
        String clase = p.getNombreClase();
        if ("Guerrero".equals(clase))  return new Color(180, 40, 40);
        if ("Mago".equals(clase))      return new Color(40, 80, 200);
        if ("Arquero".equals(clase))   return new Color(40, 160, 60);
        if ("Curandero".equals(clase)) return new Color(200, 170, 30);
        if ("Tanque".equals(clase))    return new Color(100, 60, 180);
        return new Color(120, 120, 120);
    }

    private Color colorEnemigo(Enemigo e) {
        String tipo = e.getNombreTipo();
        if ("Goblin".equals(tipo)) return new Color(50, 110, 55);
        if ("Slime".equals(tipo))  return new Color(40, 160, 60);
        return new Color(100, 60, 60);
    }

    private String cortar(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "." : s;
    }
}
