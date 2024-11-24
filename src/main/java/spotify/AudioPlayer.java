package spotify;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AudioPlayer {
    private Player player;
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
}
