package vista.sprite;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class GestorSprites {

    private static GestorSprites instancia;

private static final int N_FW        = 100;  
    private static final int N_FX_RECIBE = 540;  
    private static final int N_FX_MUERTE = 920;  

private static final int[] R_CURANDERO = { 20,  94, 130, 106};
    private static final int[] R_MAGO      = {257,  87, 363,  81};
    private static final int[] R_TANQUE    = {467,  89, 572,  87};
    private static final int[] R_GUERRERO  = {678,  87, 786,  89};
    private static final int[] R_ARQUERO   = {895,  88,1005,  78};

private static final int N_GOBLIN_Y = 1117;
    private static final int N_GOBLIN_H =   83;

private static final int N_SLIME_Y       = 1235;
    private static final int N_SLIME_H       =   74;
    private static final int N_SLIME_MUERTE_X = 300;

private final Map<String, Map<TipoAnimacion, BufferedImage[]>> datos = new HashMap<>();

    private GestorSprites() {
        cargar();
    }

    public static GestorSprites getInstancia() {
        if (instancia == null) instancia = new GestorSprites();
        return instancia;
    }

    private void cargar() {
        SpriteHoja hoja = new SpriteHoja("recursos/nuevos sprites.png", N_FW, 100);
        if (!hoja.estaCargada()) return;

        registrar(hoja, "Curandero", R_CURANDERO);
        registrar(hoja, "Mago",      R_MAGO);
        registrar(hoja, "Tanque",    R_TANQUE);
        registrar(hoja, "Guerrero",  R_GUERRERO);
        registrar(hoja, "Arquero",   R_ARQUERO);

Map<TipoAnimacion, BufferedImage[]> g = new HashMap<>();
        g.put(TipoAnimacion.IDLE,   hoja.extraerFila(0, N_GOBLIN_Y, N_GOBLIN_H, 3));
        g.put(TipoAnimacion.ATAQUE, hoja.extraerFila(0, N_GOBLIN_Y, N_GOBLIN_H, 3));
        datos.put("Goblin", g);

Map<TipoAnimacion, BufferedImage[]> s = new HashMap<>();
        s.put(TipoAnimacion.IDLE,   hoja.extraerFila(0,              N_SLIME_Y, N_SLIME_H, 3));
        s.put(TipoAnimacion.ATAQUE, hoja.extraerFila(0,              N_SLIME_Y, N_SLIME_H, 3));
        s.put(TipoAnimacion.MUERTE, hoja.extraerFila(N_SLIME_MUERTE_X, N_SLIME_Y, N_SLIME_H, 1));
        datos.put("Slime", s);
    }

    private void registrar(SpriteHoja h, String clase, int[] r) {
        int yA = r[0], hA = r[1], yB = r[2], hB = r[3];
        Map<TipoAnimacion, BufferedImage[]> m = new HashMap<>();
        m.put(TipoAnimacion.IDLE,         h.extraerFila(0,           yA, hA, 5));
        m.put(TipoAnimacion.ATAQUE,       h.extraerFila(0,           yB, hB, 5));
        m.put(TipoAnimacion.RECIBE_DAÑO, h.extraerFila(N_FX_RECIBE, yB, hB, 1));
        m.put(TipoAnimacion.CURAR,        h.extraerFila(0,           yA, hA, 1));
        m.put(TipoAnimacion.MUERTE,       h.extraerFila(N_FX_MUERTE, yB, hB, 1));
        datos.put(clase, m);
    }

public BufferedImage[] getFrames(String clase, TipoAnimacion tipo) {
        Map<TipoAnimacion, BufferedImage[]> anim = datos.get(clase);
        if (anim == null) return null;
        BufferedImage[] frames = anim.get(tipo);
        if (hayFramesValidos(frames)) return frames;
        return anim.get(TipoAnimacion.IDLE);
    }

    private static boolean hayFramesValidos(BufferedImage[] frames) {
        if (frames == null) return false;
        for (BufferedImage f : frames) { if (f != null) return true; }
        return false;
    }

    public boolean tieneSprites(String clase) {
        return datos.containsKey(clase);
    }
}
