import controlador.ControladorJuego;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}

            ControladorJuego controlador = new ControladorJuego();
            controlador.iniciar();
        });
    }
}
