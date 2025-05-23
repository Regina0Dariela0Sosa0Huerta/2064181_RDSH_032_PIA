package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Animaciones extends JPanel {

    private final int WIDTH;
    private final int HEIGHT;

    private BufferedImage background, cursor, welcomeBackground, loseBackground, winBackground;
    private BufferedImage[] dulcesImages;
    private BufferedImage chocolateImage;
    private BufferedImage[] stripedVerticalImages;
    private BufferedImage[] stripedHorizontalImages;
    private int medidas_px;
    private int offsetX, offsetY;
    private Color sweetBackgroundColor;

    private Dulce[][] tablero;
    private int currentX0, currentY0, currentClick;
    private int puntaje_actual;
    private int movimientos_actuales;
    private boolean isWelcomeScreenActive;
    private boolean isLoseScreenActive;
    private boolean isWinScreenActive;

    public Animaciones(int width, int height, int tileSize, int offsetX, int offsetY) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.medidas_px = tileSize;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.sweetBackgroundColor = new Color(255, 100, 150, 200);

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);

        loadImages();
    }

    private void loadImages() {
        try {
            welcomeBackground = ImageIO.read(Animaciones.class.getResource("/welcome_background.jpg"));
        } catch (IOException e) {
            welcomeBackground = createPlaceholderImage(WIDTH, HEIGHT, new Color(100, 100, 150));
            System.err.println("Error cargando el fondo de bienvenida");
            e.printStackTrace();
        }

        try {
            background = ImageIO.read(Animaciones.class.getResource("/background.jpg"));
        } catch (Exception e) {
            background = createPlaceholderImage(WIDTH, HEIGHT, new Color(50, 50, 100));
            System.err.println("Error cargando el fondo principal");
            e.printStackTrace();
        }

        try {
            loseBackground = ImageIO.read(Animaciones.class.getResource("/lose_background.jpg"));
        } catch (IOException e) {
            loseBackground = createPlaceholderImage(WIDTH, HEIGHT, Color.BLACK);
            System.err.println("Error cargando el fondo de derrota");
            e.printStackTrace();
        }

        try {
            winBackground = ImageIO.read(Animaciones.class.getResource("/wins_background.jpg"));
        } catch (IOException e) {
            winBackground = createPlaceholderImage(WIDTH, HEIGHT, Color.GREEN.darker());
            System.err.println("Error cargando el fondo de victoria");
            e.printStackTrace();
        }

        int totalDulces = 6;
        dulcesImages = new BufferedImage[totalDulces];
        stripedVerticalImages = new BufferedImage[totalDulces];
        stripedHorizontalImages = new BufferedImage[totalDulces];

        try {
            dulcesImages[0] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Orange.png")), medidas_px, medidas_px);
            dulcesImages[1] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Purple.png")), medidas_px, medidas_px);
            dulcesImages[2] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Red.png")), medidas_px, medidas_px);
            dulcesImages[3] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Yellow.png")), medidas_px, medidas_px);
            dulcesImages[4] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Blue.png")), medidas_px, medidas_px);
            dulcesImages[5] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Green.png")), medidas_px, medidas_px);

            stripedVerticalImages[0] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Orange-Striped-Vertical.png")), medidas_px, medidas_px);
            stripedVerticalImages[1] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Purple-Striped-Vertical.png")), medidas_px, medidas_px);
            stripedVerticalImages[2] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Red-Striped-Vertical.png")), medidas_px, medidas_px);
            stripedVerticalImages[3] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Yellow-Striped-Vertical.png")), medidas_px, medidas_px);
            stripedVerticalImages[4] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Blue-Striped-Vertical.png")), medidas_px, medidas_px);
            stripedVerticalImages[5] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Green-Striped-Vertical.png")), medidas_px, medidas_px);

            stripedHorizontalImages[0] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Orange-Striped-Horizontal.png")), medidas_px, medidas_px);
            stripedHorizontalImages[1] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Purple-Striped-Horizontal.png")), medidas_px, medidas_px);
            stripedHorizontalImages[2] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Red-Striped-Horizontal.png")), medidas_px, medidas_px);
            stripedHorizontalImages[3] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Yellow-Striped-Horizontal.png")), medidas_px, medidas_px);
            stripedHorizontalImages[4] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Blue-Striped-Horizontal.png")), medidas_px, medidas_px);
            stripedHorizontalImages[5] = resizeImage(ImageIO.read(Animaciones.class.getResource("/Green-Striped-Horizontal.png")), medidas_px, medidas_px);

        } catch (IOException e) {
            System.err.println("Error cargando imágenes de dulces o rayadas. Usando marcador de posición.");
            dulcesImages = null; // <-- dulcesImages
            stripedVerticalImages = null;
            stripedHorizontalImages = null;
            e.printStackTrace();
        }

        try {
            chocolateImage = resizeImage(ImageIO.read(Animaciones.class.getResource("/Choco.png")), medidas_px, medidas_px);
        } catch (IOException e) {
            System.err.println("Error cargando imagen de chocolate. Usando marcador de posición.");
            chocolateImage = createPlaceholderImage(medidas_px, medidas_px, new Color(139, 69, 19));
            e.printStackTrace();
        }

        try {
            cursor = resizeImage(ImageIO.read(getClass().getResource("/cursor.png")), medidas_px, medidas_px);
        } catch (Exception e) {
            System.err.println("Error cargando imagen de cursor");
            cursor = createPlaceholderImage(medidas_px, medidas_px, new Color(255, 255, 255, 203));
            e.printStackTrace();
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();
        return outputImage;
    }

    private BufferedImage createPlaceholderImage(int width, int height, Color color) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);
        g.dispose();
        return img;
    }

    public void updateAnimacion(Dulce[][] grid, int x0, int y0, int click, int score, int moves, boolean showWelcome, boolean showLose, boolean showWin) {
        this.tablero = grid;
        this.currentX0 = x0;
        this.currentY0 = y0;
        this.currentClick = click;
        this.puntaje_actual = score;
        this.movimientos_actuales = moves;
        this.isWelcomeScreenActive = showWelcome;
        this.isLoseScreenActive = showLose;
        this.isWinScreenActive = showWin;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (isWelcomeScreenActive) {
            if (welcomeBackground != null) {
                g2.drawImage(welcomeBackground, 0, 0, WIDTH, HEIGHT, null);
            }
        } else if (isLoseScreenActive) {
            if (loseBackground != null) {
                g2.drawImage(loseBackground, 0, 0, WIDTH, HEIGHT, null);
            }
        } else if (isWinScreenActive) {
            if (winBackground != null) {
                g2.drawImage(winBackground, 0, 0, WIDTH, HEIGHT, null);
            }
        } else {
            if (background != null) {
                g2.drawImage(background, 0, 0, WIDTH, HEIGHT, null);
            } else {
                g2.setColor(new Color(50, 50, 100));
                g2.fillRect(0, 0, WIDTH, HEIGHT);
            }

            // Dibujar los cuadrados de fondo de las celdas
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    g2.setColor(sweetBackgroundColor);
                    int x = offsetX + (j - 1) * medidas_px;
                    int y = offsetY + (i - 1) * medidas_px;
                    g2.fillRect(x, y, medidas_px, medidas_px);
                }
            }

            if (tablero != null) {
                // Dibujar los dulces
                for (int i = 1; i <= 8; i++) {
                    for (int j = 1; j <= 8; j++) {
                        if (tablero[i][j] != null) {
                            BufferedImage dulceImage = getDulceImage(tablero[i][j].tipo, tablero[i][j].isChocolate, tablero[i][j].isStripedVertical, tablero[i][j].isStripedHorizontal);
                            if (dulceImage != null) {
                                int x = tablero[i][j].x + (offsetX - medidas_px);
                                int y = tablero[i][j].y + (offsetY - medidas_px);
                                g2.drawImage(dulceImage, x, y, medidas_px, medidas_px, null);
                            }
                        }

                        // Dibujar el cursor
                        if (currentClick == 1 && currentX0 == j && currentY0 == i && cursor != null) {
                            int cursorX = tablero[i][j].x + (offsetX - medidas_px); // tablero, medidas_px
                            int cursorY = tablero[i][j].y + (offsetY - medidas_px); // tablero, medidas_px
                            g2.drawImage(cursor, cursorX, cursorY, medidas_px, medidas_px, null); // medidas_px
                        }
                    }
                }
            }

            // Dibujar el puntaje y los movimientos restantes
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("Score: " + puntaje_actual, 20, 30);
            g2.drawString("Moves: " + movimientos_actuales, 20, 60);
        }
    }

    private BufferedImage getDulceImage(int dulceIndex, boolean isChocolate, boolean isStripedVertical, boolean isStripedHorizontal) {
        if (isChocolate) {
            return chocolateImage;
        }

        if (stripedVerticalImages != null && isStripedVertical && dulceIndex >= 0 && dulceIndex < stripedVerticalImages.length) {
            return stripedVerticalImages[dulceIndex];
        }
        if (stripedHorizontalImages != null && isStripedHorizontal && dulceIndex >= 0 && dulceIndex < stripedHorizontalImages.length) {
            return stripedHorizontalImages[dulceIndex];
        }

        if (dulcesImages != null && dulceIndex >= 0 && dulceIndex < dulcesImages.length) {
            return dulcesImages[dulceIndex];
        }

        return createPlaceholderImage(medidas_px, medidas_px, Color.MAGENTA);
    }
}