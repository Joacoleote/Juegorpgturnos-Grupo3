package vista.sprite;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SpriteHoja {

    private final BufferedImage imagen;
    private final int anchoFrame;
    private final int altoFrame;

private final boolean usarColorKey;

    public SpriteHoja(String ruta, int anchoFrame, int altoFrame) {
        this.anchoFrame = anchoFrame;
        this.altoFrame  = altoFrame;
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(ruta));
        } catch (IOException ignored) {}
        this.imagen = img;
        
        this.usarColorKey = (img != null) && (((img.getRGB(0, 0) >> 24) & 0xFF) > 10);
    }

    public boolean estaCargada() {
        return imagen != null;
    }

public BufferedImage[] extraerFila(int xInicio, int y, int numFrames) {
        return extraerFila(xInicio, y, altoFrame, numFrames);
    }

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

private BufferedImage procesarFrame(BufferedImage src) {
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgba = src.getRGB(x, y);
                int a = (rgba >> 24) & 0xFF;
                int r = (rgba >> 16) & 0xFF, g = (rgba >> 8) & 0xFF, b = rgba & 0xFF;
                if (a < 10) {
                    dst.setRGB(x, y, 0); 
                } else if (usarColorKey && r > 230 && g > 230 && b > 230) {
                    dst.setRGB(x, y, 0); 
                } else {
                    dst.setRGB(x, y, 0xFF000000 | (rgba & 0x00FFFFFF));
                }
            }
        }
        return dst;
    }
}
