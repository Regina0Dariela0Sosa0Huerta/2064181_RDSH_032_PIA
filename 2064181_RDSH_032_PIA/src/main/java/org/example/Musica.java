package org.example;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class Musica {
    private Clip menuMusicClip;
    private Clip winSoundClip;
    private Clip loseSoundClip;

    public Musica() {
        loadAudioClips();
    }

    private void loadAudioClips() {
        try {// Cargar sonido inicial
            URL menuMusicUrl = getClass().getResource("/Candy Crush Menu.wav");
            if (menuMusicUrl != null) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(menuMusicUrl);
                menuMusicClip = AudioSystem.getClip();
                menuMusicClip.open(audioInputStream);
            } else {
                System.err.println("Error: Archivo 'Candy Crush Menu.wav' no encontrado.");
            }// Cargar sonido de victoria
            URL winSoundUrl = getClass().getResource("/Candy Crush Win.wav");
            if (winSoundUrl != null) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(winSoundUrl);
                winSoundClip = AudioSystem.getClip();
                winSoundClip.open(audioInputStream);
            } else {
                System.err.println("Error: Archivo 'Candy Crush Win.wav' no encontrado.");
            }// Cargar sonido de derrota
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

    public void playMenuMusic() {playLoop(menuMusicClip);}

    public void playWinSound() {stopMenuMusic();playOnce(winSoundClip);}

    public void playLoseSound() {stopMenuMusic();playOnce(loseSoundClip);}

    public void stopMenuMusic() {stopClip(menuMusicClip);}

    public void stopAllSounds() {stopAndClose(menuMusicClip);stopAndClose(winSoundClip);
        stopAndClose(loseSoundClip);
    }
    private void playOnce(Clip clip) {if (clip != null) {clip.stop();
            clip.setFramePosition(0);
            clip.start();}
    }

    private void playLoop(Clip clip) {if (clip != null) {clip.stop();
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);}
    }

    private void stopClip(Clip clip) {if (clip != null && clip.isRunning()) {clip.stop();}}

    private void stopAndClose(Clip clip) {if (clip != null && clip.isRunning()) {clip.stop();}
        if (clip != null) {
            clip.close();
        }
    }
}