package vista.sprite;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton. Loads "nuevos sprites.png" and exposes frames per class + animation type.
 *
 * ======= nuevos sprites.png (848 x 1264, transparent background) =======
 *
 * Each character occupies TWO horizontal rows:
 *   Row A (yA, hA) → IDLE animation  (5 frames × 106 px, x = 0..529)
 *   Row B (yB, hB) → ATAQUE animation (5 frames × 106 px, x = 0..529)
 *                     + right section: RECIBE @ x=530 (106 px), stagger @ x=636 (106 px),
 *                       dying @ x=742 (53 px), dead/lying @ x=795 (53 px = MUERTE)
 *
 * Characters top-to-bottom:
 *   Curandero  yA=22,   hA=72,  yB=125,  hB=70
 *   Mago       yA=217,  hA=65,  yB=300,  hB=64
 *   Tanque     yA=386,  hA=70,  yB=472,  hB=69
 *   Guerrero   yA=562,  hA=67,  yB=650,  hB=67
 *   Arquero    yA=742,  hA=66,  yB=826,  hB=66
 *   Goblin     yA=920,  hA=68,  yB=1007, hB=69
 *   Slime      yA=1112, hA=52,  yB=1205, hB=51
 */
public class GestorSprites {

    private static GestorSprites instancia;

    // Left section: 5 animation frames of 106 px each (x = 0..529)
    private static final int N_FW        = 106;
    // Right section: special frames of 53 px each
    private static final int N_FW_RIGHT  = 53;
    private static final int N_FX_RECIBE = 530;
    private static final int N_FX_MUERTE = 795;

    // [yA, hA, yB, hB]
    private static final int[] R_CURANDERO = { 22, 72, 125, 70};
    private static final int[] R_MAGO      = {217, 65, 300, 64};
    private static final int[] R_TANQUE    = {386, 70, 472, 69};
    private static final int[] R_GUERRERO  = {562, 67, 650, 67};
    private static final int[] R_ARQUERO   = {742, 66, 826, 66};
    private static final int[] R_GOBLIN    = {920, 68,1007, 69};
    private static final int[] R_SLIME     = {1112,52,1205, 51};

    // ─────────────────────────────────────────────────────────────────────────

    private final Map<String, Map<TipoAnimacion, BufferedImage[]>> datos = new HashMap<>();

    private GestorSprites() {
        cargar();
    }

    public static GestorSprites getInstancia() {
        if (instancia == null) instancia = new GestorSprites();
        return instancia;
    }

    private void cargar() {
        SpriteHoja hoja      = new SpriteHoja("recursos/nuevos sprites.png", N_FW,       100);
        SpriteHoja hojaRight = new SpriteHoja("recursos/nuevos sprites.png", N_FW_RIGHT, 100);
        if (!hoja.estaCargada()) return;

        registrar(hoja, hojaRight, "Curandero", R_CURANDERO);
        registrar(hoja, hojaRight, "Mago",      R_MAGO);
        registrar(hoja, hojaRight, "Tanque",    R_TANQUE);
        registrar(hoja, hojaRight, "Guerrero",  R_GUERRERO);
        registrar(hoja, hojaRight, "Arquero",   R_ARQUERO);
        registrar(hoja, hojaRight, "Goblin",    R_GOBLIN);

        // Slime: 3 alive frames for idle/attack; specials from right section of row A
        int sYA = R_SLIME[0], sHA = R_SLIME[1], sYB = R_SLIME[2], sHB = R_SLIME[3];
        Map<TipoAnimacion, BufferedImage[]> s = new HashMap<>();
        s.put(TipoAnimacion.IDLE,        hoja.extraerFila(0,            sYA, sHA, 3));
        s.put(TipoAnimacion.ATAQUE,      hoja.extraerFila(0,            sYB, sHB, 3));
        s.put(TipoAnimacion.CURAR,       hoja.extraerFila(0,            sYA, sHA, 1));
        s.put(TipoAnimacion.RECIBE_DAÑO, hoja.extraerFila(N_FX_RECIBE, sYA, sHA, 1));
        s.put(TipoAnimacion.MUERTE,      hojaRight.extraerFila(N_FX_MUERTE, sYA, sHA, 1));
        datos.put("Slime", s);
    }

    private void registrar(SpriteHoja h, SpriteHoja hRight, String clase, int[] r) {
        int yA = r[0], hA = r[1], yB = r[2], hB = r[3];
        Map<TipoAnimacion, BufferedImage[]> m = new HashMap<>();
        m.put(TipoAnimacion.IDLE,        h.extraerFila(0,            yA, hA, 5));
        m.put(TipoAnimacion.ATAQUE,      h.extraerFila(0,            yB, hB, 5));
        m.put(TipoAnimacion.RECIBE_DAÑO, h.extraerFila(N_FX_RECIBE, yB, hB, 1));  // RECIBE is 106px wide
        m.put(TipoAnimacion.CURAR,       h.extraerFila(0,            yA, hA, 1));
        m.put(TipoAnimacion.MUERTE,      hRight.extraerFila(N_FX_MUERTE, yB, hB, 1));  // MUERTE is 53px wide
        datos.put(clase, m);
    }

    /**
     * Returns frames for the given class and animation type.
     * Falls back to IDLE if the specific animation has no valid frames.
     * Returns null if no sprites exist for this class at all.
     */
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
