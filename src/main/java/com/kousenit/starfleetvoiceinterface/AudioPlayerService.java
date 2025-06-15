package com.kousenit.starfleetvoiceinterface;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Service
public class AudioPlayerService {

    private static final Logger log = LoggerFactory.getLogger(AudioPlayerService.class);

    public void playWorkingSound() {
        playAudioFile("/sounds/tos_working.mp3");
    }

    public void playAudioFile(String resourcePath) {
        CompletableFuture.runAsync(() -> {
            try {
                InputStream audioStream = getClass().getResourceAsStream(resourcePath);
                if (audioStream == null) {
                    log.warn("Audio file not found: {}", resourcePath);
                    return;
                }

                Player player = new Player(audioStream);
                player.play();
                
                log.info("Audio playback completed for: {}", resourcePath);
            } catch (JavaLayerException e) {
                log.warn("Audio playback error: {}", e.getMessage());
                // Don't interrupt main workflow on audio failure
            }
        });
    }
}