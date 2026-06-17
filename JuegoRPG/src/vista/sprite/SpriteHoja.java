package vista.sprite;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SpriteHoja {

    private final BufferedImage imagen;
    private final int anchoFrame;
    private final int altoFrame;
    // True when the sheet has an opaque background that needs color-key removal.
    // False when the sheet already has proper alpha transparency.
    private final boolean usarColorKey;

    public SpriteHoja(String ruta, int anchoFrame, int altoFrame) {
        this.anchoFrame = anchoFrame;
        this.altoFrame  = altoFrame;
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(ruta));
        } catch (IOException ignored) {}
        this.imagen = img;
        // Auto-detect: if the corner pixel is transparent the sheet already has alpha
        this.usarColorKey = (img != null) && (((img.getRGB(0, 0) >> 24) & 0xFF) > 10);
    }

    public boolean estaCargada() {
        return imagen != null;
    }

    /** Extracts a horizontal strip of frames starting at (xInicio, y). */
    public BufferedImage[] extraerFila(int xInicio, int y, int numFrames) {
        return extraerFila(xInicio, y, altoFrame, numFrames);
    }

    /** Extracts a horizontal strip of frames using an explicit frame height override. */
    public BufferedImage[] extraerFila(int xInicio, int y, int altoOverride, int numFrames) {
        if (!estaCargada()) return new BufferedImage[0];
        BufferedImage[] frames = new BufferedImage[numFrames];
        for (int i = 0; i < numFrames; i++) {
            int fx = xInicio + i * anchoFrame;
            try {
                if (fx + anchoFrame <= imagen.getWidth()
                        && y + altoOverride <= imagen.getHeight()
                        && y >= 0 && fx >= 0) {
                    frames[i] = procesarFrame(imagen.getSubimage(fx, y, anchoFrame, altoOverride));
                }
            } catch (Exception ignored) {}
        }
        return frames;
    }

    /**
     * Converts a subimage to TYPE_INT_ARGB.
     * - If the sheet has a transparent background (usarColorKey=false): copies pixels as-is,
     *   preserving existing alpha.
     * - If the sheet has an opaque white background (usarColorKey=true): any pixel with
     *   R,G,B > 230 is made transparent (color-key removal).
     */
    private BufferedImage procesarFrame(BufferedImage src) {
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgba = src.getRGB(x, y);
                int a = (rgba >> 24) & 0xFF;
                int r = (rgba >> 16) & 0xFF, g = (rgba >> 8) & 0xFF, b = rgba & 0xFF;
                if (a < 10) {
                    dst.setRGB(x, y, 0); // already transparent
                } else if (usarColorKey && r > 230 && g > 230 && b > 230) {
                    dst.setRGB(x, y, 0); // opaque near-white background → transparent
                } else {
                    dst.setRGB(x, y, 0xFF000000 | (rgba & 0x00FFFFFF));
                }
            }
        }
        return dst;
    }
}
