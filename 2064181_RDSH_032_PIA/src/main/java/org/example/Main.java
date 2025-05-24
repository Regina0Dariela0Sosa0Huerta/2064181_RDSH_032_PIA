package org.example;
//Regina Dariela Sosa Huerta
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main extends JFrame {

    private Juego juego;
    private Animaciones animaciones;
    private Musica sonidos;
    private boolean showWelcomeScreen;
    private boolean showLoseScreen;
    private boolean showWinScreen;
    private Timer gameTimer;

    public Main() {
        int WIDTH = 740;
        int HEIGHT = 480;
        int TILE_SIZE = 49;
        int OFFSET_X = 180;
        int OFFSET_Y = 60;

        juego = new Juego(6, TILE_SIZE);
        animaciones = new Animaciones(WIDTH, HEIGHT, TILE_SIZE, OFFSET_X, OFFSET_Y);
        sonidos = new Musica(); // iniciar los audios

        setTitle("Candy Crush");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(animaciones);
        pack();
        setLocationRelativeTo(null); // Centrar la ventana
        setVisible(true);

        showWelcomeScreen = true; // Mostrar pantalla de bienvenida al inicio
        showLoseScreen = false;
        showWinScreen = false;

        animaciones.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (showWelcomeScreen) {
                    showWelcomeScreen = false; // Quitar pantalla de bienvenida al primer clic
                    juego.rellenar_tablero(); // Inicializar el tablero
                    startGameLoop(); // Iniciar el bucle del juego
                } else if (showLoseScreen) {
                    dispose(); // Cierra el JFrame
                } else if (showWinScreen) {
                    dispose();
                } else {
                    if (!juego.perdedor() && !juego.ganador()) {
                        juego.procesamiento_clics(e.getX(), e.getY(), OFFSET_X, OFFSET_Y);
                    }
                }
                animaciones.updateAnimacion(juego.getTablero(),
                        juego.getX0(),juego.getY0(), juego.getClick(),
                        juego.getPuntaje(), juego.getMovimientos(),
                        showWelcomeScreen, showLoseScreen, showWinScreen);
            }
        });

        // pantalla de bienvenida se dibuje al inicio
        animaciones.updateAnimacion(null, 0, 0, 0, 0, 0, showWelcomeScreen, showLoseScreen, showWinScreen);
        sonidos.playMenuMusic(); // Iniciar música del menú
    }

    private void startGameLoop() {
        // velocidad del juego (ms por cuadro)
        gameTimer = new Timer(30, e -> {
            if (juego.ganador()) {
                showWinScreen = true; // Establecer estado de victoria
                animaciones.updateAnimacion(juego.getTablero(),
                        juego.getX0(), juego.getY0(), juego.getClick(),
                        juego.getPuntaje(), juego.getMovimientos(),
                        showWelcomeScreen, showLoseScreen, showWinScreen);
                gameTimer.stop(); // Detener el timer
                sonidos.playWinSound(); // Reproducir sonido de victoria
            } else if (juego.perdedor()) {
                showLoseScreen = true; // Establecer pantalla de derrota
                animaciones.updateAnimacion(juego.getTablero(),
                        juego.getX0(), juego.getY0(), juego.getClick(),
                        juego.getPuntaje(), juego.getMovimientos(),
                        showWelcomeScreen, showLoseScreen, showWinScreen);
                gameTimer.stop();
                sonidos.playLoseSound(); // Reproducir sonido de derrota
            } else {
                juego.updateGame();
                animaciones.updateAnimacion(juego.getTablero(),
                        juego.getX0(), juego.getY0(), juego.getClick(),
                        juego.getPuntaje(), juego.getMovimientos(),
                        showWelcomeScreen, showLoseScreen, showWinScreen);
            }
        });
        gameTimer.start();
    }

    @Override
    public void dispose() {
        super.dispose();
        // Cerrar todos los clips de audio al cerrar la ventana
        sonidos.stopAllSounds();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });

    }
}