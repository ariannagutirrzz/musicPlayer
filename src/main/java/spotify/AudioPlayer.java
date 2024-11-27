package spotify;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.sound.sampled.FloatControl;
import java.io.FileInputStream;
import java.io.IOException;

public class AudioPlayer {
    private Player player;
    private FloatControl volumeControl;
    private String currentSongPath;
    private boolean isPlaying;

    public void play(String songPath) {
        stop(); // Detener cualquier reproducción en curso
        this.currentSongPath = songPath;
        isPlaying = true;

        new Thread(() -> {
            try (FileInputStream fis = new FileInputStream(songPath)) {
                player = new Player(fis);
                player.play();
            } catch (JavaLayerException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        if (player != null) {
            player.close();
            isPlaying = false;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setVolume(int volume) {
        if (volumeControl != null) {
            // El volumen de FloatControl va de 0.0 (mudo) a 1.0 (volumen máximo)
            float normalizedVolume = volume / 100.0f; // Normalizar el valor de 0 a 1
            volumeControl.setValue(normalizedVolume); // Ajustar el volumen
            System.out.println("Volumen ajustado a: " + volume + "%");
        }
    }

}
