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

    public void seekTo(int seconds) {
        if (currentSongPath == null) {
            System.out.println("No hay canción cargada para realizar seek.");
            return;
        }

        try {
            stop(); // Detener cualquier reproducción en curso
            FileInputStream fis = new FileInputStream(currentSongPath);
            player = new Player(fis);

            // Calcular la cantidad de bytes a saltar basándonos en la duración y el bitrate
            int bitrate = 128; // Suponiendo un bitrate promedio de 128 kbps (puedes ajustar esto según tu archivo)
            int bytesPerSecond = (bitrate * 1000) / 8; // Bytes por segundo

            long skipBytes = seconds * bytesPerSecond; // Calcular los bytes que deben saltarse
            fis.skip(skipBytes);

            isPlaying = true;

            // Reanuda la reproducción desde el punto deseado
            new Thread(() -> {
                try {
                    player.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
