package vista.sprite;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton. Loads both sprite sheets and exposes frames per class + animation type.
 *
 * ============ HEROES.PNG LAYOUT (2048x2048) ============
 * 3-column × 2-row grid.  Each character cell = 683 × 1024 px.
 *
 *  Top row  (y=0):    Mago (col 0) | Tanque (col 1) | Guerrero (col 2)
 *  Bottom row (y=1024): Arquera (col 0) | Orco (col 1) | Slime (col 2)
 *
 *  Frame size: H_FRAME_W × H_FRAME_H  (adjust if sprites are clipped)
 *  Top chars have 5 animation rows; bottom chars have 6 (includes Walk/Jump).
 *
 * ============ CURANDERO.JPEG LAYOUT (1254x1254) ============
 *  Left panel (showcase): x=0..C_X_START
 *  Right grid: 3 frames per row, 6 animation rows stacked vertically.
 *  Frame size: C_FRAME_W × C_FRAME_H
 */
public class GestorSprites {

    private static GestorSprites instancia;

    // ─── heroes.png config ────────────────────────────────────────────────────
    private static final int H_FRAME_W     = 170;
    private static final int H_FRAME_H_TOP = 130; // covers char body after label skip (~122px max)
    private static final int H_FRAME_H_BOT = 125; // covers tallest bottom-row block (123px)

    // Column X-starts
    private static final int H_COL_0 = 0;
    private static final int H_COL_1 = 683;
    private static final int H_COL_2 = 1366;

    // Section Y-starts
    private static final int H_Y_TOP = 0;
    private static final int H_Y_BOT = 1024;

    // Animation row offsets relative to section start — TOP chars
    // Each block has a text label ("IDLE", "HEAL") in the first ~33px; we skip it.
    private static final int H_T_IDLE   = 101; // block y=68, skip "IDLE" label → char at y=101
    private static final int H_T_ATAQ   = 269; // block y=269..390, no label
    private static final int H_T_RECIBE = 455; // block y=455..573, no label
    private static final int H_T_CURAR  = 637; // block y=604, skip "HEAL" label → char at y=637
    private static final int H_T_MUERTE = 836; // block y=836..947, no label

    // Animation row offsets — BOTTOM chars (row 1=WALK skipped)
    // Measured from absolute y, stored relative to H_Y_BOT=1024
    private static final int H_B_IDLE   =  93; // abs y=1117 (was 75)
    private static final int H_B_ATAQ   = 266; // abs y=1290 (was 225)
    private static final int H_B_RECIBE = 441; // abs y=1465 (was 300)
    private static final int H_B_CURAR  = 621; // abs y=1645 (was 375)
    private static final int H_B_MUERTE = 770; // abs y=1794 (was 450)

    // ─── curandero.jpeg config ─────────────────────────────────────────────────
    // Image is 1254×1254, 3 frames per row (cols), 6 animation rows.
    // Left panel showcase: x=0..C_X_START. Grid starts at C_X_START.
    private static final int C_X_START  = 310;
    private static final int C_FRAME_W  = 290;
    private static final int C_FRAME_H  = 180; // capped so last row fits: 1068+180=1248 ≤ 1254

    // Row Y-starts measured from actual 1254×1254 image (proportional to original 2048px sheet)
    private static final int C_Y_IDLE   =  26;  // row 0
    private static final int C_Y_ATAQ   = 430;  // row 2 (row 1 is WALK, skipped)
    private static final int C_Y_CURAR  = 635;  // row 3
    private static final int C_Y_RECIBE = 870;  // row 4
    private static final int C_Y_MUERTE = 1068; // row 5

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
        SpriteHoja heroes    = new SpriteHoja("recursos/heroes.png",    H_FRAME_W, H_FRAME_H_TOP);
        SpriteHoja curandero = new SpriteHoja("recursos/curandero.jpeg", C_FRAME_W, C_FRAME_H);

        if (heroes.estaCargada()) {
            // ── Top row characters (5 animation rows) ──
            registrarTop(heroes, "Mago",     H_COL_0, H_Y_TOP, 3, 4, 4, 4, 4);
            registrarTop(heroes, "Tanque",   H_COL_1, H_Y_TOP, 4, 4, 4, 4, 4);
            registrarTop(heroes, "Guerrero", H_COL_2, H_Y_TOP, 4, 4, 4, 4, 4);

            // ── Bottom row characters (row 1=WALK skipped) ──
            registrarBot(heroes, "Arquero", H_COL_0, H_Y_BOT, 4, 4, 4, 4, 4);
            registrarBot(heroes, "Goblin",  H_COL_1, H_Y_BOT, 4, 4, 4, 4, 4);
        }

        if (curandero.estaCargada()) {
            Map<TipoAnimacion, BufferedImage[]> c = new HashMap<>();
            c.put(TipoAnimacion.IDLE,         curandero.extraerFila(C_X_START, C_Y_IDLE,   3));
            c.put(TipoAnimacion.ATAQUE,       curandero.extraerFila(C_X_START, C_Y_ATAQ,   3));
            c.put(TipoAnimacion.RECIBE_DANIO, curandero.extraerFila(C_X_START, C_Y_RECIBE, 3));
            c.put(TipoAnimacion.CURAR,        curandero.extraerFila(C_X_START, C_Y_CURAR,  3));
            c.put(TipoAnimacion.MUERTE,       curandero.extraerFila(C_X_START, C_Y_MUERTE, 3));
            datos.put("Curandero", c);
        }
    }

    private void registrarTop(SpriteHoja h, String clase, int colX, int secY,
            int nIdle, int nAtaq, int nRecibe, int nCurar, int nMuerte) {
        Map<TipoAnimacion, BufferedImage[]> m = new HashMap<>();
        m.put(TipoAnimacion.IDLE,        h.extraerFila(colX, secY + H_T_IDLE,   nIdle));
        m.put(TipoAnimacion.ATAQUE,      h.extraerFila(colX, secY + H_T_ATAQ,   nAtaq));
        m.put(TipoAnimacion.RECIBE_DANIO, h.extraerFila(colX, secY + H_T_RECIBE, nRecibe));
        m.put(TipoAnimacion.CURAR,       h.extraerFila(colX, secY + H_T_CURAR,  nCurar));
        m.put(TipoAnimacion.MUERTE,      h.extraerFila(colX, secY + H_T_MUERTE, nMuerte));
        datos.put(clase, m);
    }

    private void registrarBot(SpriteHoja h, String clase, int colX, int secY,
            int nIdle, int nAtaq, int nRecibe, int nCurar, int nMuerte) {
        Map<TipoAnimacion, BufferedImage[]> m = new HashMap<>();
        m.put(TipoAnimacion.IDLE,        h.extraerFila(colX, secY + H_B_IDLE,   H_FRAME_H_BOT, nIdle));
        m.put(TipoAnimacion.ATAQUE,      h.extraerFila(colX, secY + H_B_ATAQ,   H_FRAME_H_BOT, nAtaq));
        m.put(TipoAnimacion.RECIBE_DANIO, h.extraerFila(colX, secY + H_B_RECIBE, H_FRAME_H_BOT, nRecibe));
        m.put(TipoAnimacion.CURAR,       h.extraerFila(colX, secY + H_B_CURAR,  H_FRAME_H_BOT, nCurar));
        m.put(TipoAnimacion.MUERTE,      h.extraerFila(colX, secY + H_B_MUERTE, H_FRAME_H_BOT, nMuerte));
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
        for (BufferedImage f : frames) {
            if (f != null) return true;
        }
        return false;
    }

    public boolean tieneSprites(String clase) {
        return datos.containsKey(clase);
    }
}
