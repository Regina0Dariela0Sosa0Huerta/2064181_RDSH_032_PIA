package org.example;
//Regina Dariela Sosa Huerta
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;


public class Main extends JFrame {

    private Juego juego;
    private Animaciones animaciones;
    private boolean showWelcomeScreen;
    private boolean showLoseScreen;
    private boolean showWinScreen;
    private Timer gameTimer;

    private Clip menuMusicClip;
    private Clip winSoundClip;
    private Clip loseSoundClip;

    public Main() {
        int WIDTH = 740;
        int HEIGHT = 480;
        int TILE_SIZE = 49;
        int OFFSET_X = 180;
        int OFFSET_Y = 60;

        juego = new Juego(6, TILE_SIZE);
        animaciones = new Animaciones(WIDTH, HEIGHT, TILE_SIZE, OFFSET_X, OFFSET_Y);

        setTitle("Candy Crush");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(animaciones);
        pack();
        setLocationRelativeTo(null); // Centrar la ventana
        setVisible(true);

        showWelcomeScreen = true; // Mostrar pantalla de bienvenida al inicio
        showLoseScreen = false; // Inicialmente no mostrar pantalla de derrota
        showWinScreen = false;  // Inicializar estado de victoria como false

        loadAudioClips(); // Cargar los clips de audio al inicio

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
        playLoop(menuMusicClip); // bucle de musica al iniciar el juego
    }

    private void loadAudioClips() {
        try {
            URL menuMusicUrl = getClass().getResource("/Candy Crush Menu.wav");
            if (menuMusicUrl != null) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(menuMusicUrl);
                menuMusicClip = AudioSystem.getClip();
                menuMusicClip.open(audioInputStream);
            } else {
                System.err.println("Error: Archivo 'Candy Crush Menu.wav' no encontrado.");
            }
            URL winSoundUrl = getClass().getResource("/Candy Crush Win.wav");
            if (winSoundUrl != null) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(winSoundUrl);
                winSoundClip = AudioSystem.getClip();
                winSoundClip.open(audioInputStream);
            } else {
                System.err.println("Error: Archivo 'Candy Crush Win.wav' no encontrado.");
            }
            URL loseSoundUrl = getClass().getResource("/Candy Crush Lose.wav");
            if (loseSoundUrl != null) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(loseSoundUrl);
                loseSoundClip = AudioSystem.getClip();
                loseSoundClip.open(audioInputStream);
            } else {
                System.err.println("Error: Archivo 'Candy Crush Lose.wav' no encontrado.");
            }

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error al cargar los clips de audio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void playOnce(Clip clip) {
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }

    private void playLoop(Clip clip) {
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Reproducir en bucle
        }
    }

    private void stopAndClose(Clip clip) {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
        if (clip != null) {
            clip.close();
        }
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
                stopAndClose(menuMusicClip);
                playOnce(winSoundClip);
                stopAndClose(loseSoundClip);
            } else if (juego.perdedor()) {
                showLoseScreen = true; // Establecer pantalla de derrota
                animaciones.updateAnimacion(juego.getTablero(),
                        juego.getX0(), juego.getY0(), juego.getClick(),
                        juego.getPuntaje(), juego.getMovimientos(),
                        showWelcomeScreen, showLoseScreen, showWinScreen);
                gameTimer.stop();
                stopAndClose(menuMusicClip);
                playOnce(loseSoundClip);
                stopAndClose(winSoundClip);
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
        // Asegurarse de que todos los clips se cierren al cerrar la ventana
        stopAndClose(menuMusicClip);
        stopAndClose(winSoundClip);
        stopAndClose(loseSoundClip);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}