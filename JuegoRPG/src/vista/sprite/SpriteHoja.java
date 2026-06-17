package vista.sprite;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SpriteHoja {

    private final BufferedImage imagen;
    private final int anchoFrame;
    private final int altoFrame;

    public SpriteHoja(String ruta, int anchoFrame, int altoFrame) {
        this.anchoFrame = anchoFrame;
        this.altoFrame = altoFrame;
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(ruta));
        } catch (IOException ignored) {}
        this.imagen = img;
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
                if (fx + anchoFrame <= imagen.getWidth() && y + altoOverride <= imagen.getHeight() && y >= 0) {
                    BufferedImage sub = imagen.getSubimage(fx, y, anchoFrame, altoOverride);
                    frames[i] = aplicarColorKey(sub);
                }
            } catch (Exception ignored) {}
        }
        return frames;
    }

    /**
     * Replaces near-white background pixels with transparent ones.
     * Sprite sheets from this project use an opaque white background (R,G,B > 230)
     * with no alpha channel — this converts them to proper ARGB with transparency.
     */
    private static BufferedImage aplicarColorKey(BufferedImage src) {
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                if (r > 230 && g > 230 && b > 230) {
                    dst.setRGB(x, y, 0); // transparent
                } else {
                    dst.setRGB(x, y, 0xFF000000 | (rgb & 0x00FFFFFF));
                }
            }
        }
        return dst;
    }
}
