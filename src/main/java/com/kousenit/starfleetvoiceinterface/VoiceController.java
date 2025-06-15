package com.kousenit.starfleetvoiceinterface;

import com.kousenit.starfleetvoiceinterface.config.UIConstants;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class VoiceController {

    private final TranscriptionService transcriptionService;

    private final McpClientService mcpService;
    private final AudioPlayerService audioPlayerService;

    private Circle recordButton;
    private Label statusLabel;
    private Label transcriptLabel;
    private TextArea responseArea;
    private Timeline pulseAnimation;

    public VoiceController(TranscriptionService transcriptionService, McpClientService mcpService, AudioPlayerService audioPlayerService) {
        this.transcriptionService = transcriptionService;
        this.mcpService = mcpService;
        this.audioPlayerService = audioPlayerService;
    }

    public void initialize(Circle recordButton, Label statusLabel,
                           Label transcriptLabel, TextArea responseArea) {
        this.recordButton = recordButton;
        this.statusLabel = statusLabel;
        this.transcriptLabel = transcriptLabel;
        this.responseArea = responseArea;

        setupAnimations();
        setupEventHandlers();
    }

    private void setupAnimations() {
        // Pulsing animation when recording
        pulseAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(recordButton.fillProperty(), Color.web(UIConstants.BUTTON_BACKGROUND_COLOR)),
                        new KeyValue(recordButton.strokeWidthProperty(), UIConstants.BUTTON_STROKE_WIDTH)),
                new KeyFrame(Duration.seconds(UIConstants.PULSE_ANIMATION_DURATION),
                        new KeyValue(recordButton.fillProperty(), Color.web(UIConstants.STATUS_RECORDING_COLOR)),
                        new KeyValue(recordButton.strokeWidthProperty(), UIConstants.BUTTON_STROKE_WIDTH_ACTIVE))
        );
        pulseAnimation.setCycleCount(Timeline.INDEFINITE);
        pulseAnimation.setAutoReverse(true);
    }

    private void setupEventHandlers() {
        recordButton.setOnMousePressed(e -> startRecording());
        recordButton.setOnMouseReleased(e -> stopRecording());
    }

    private void startRecording() {
        Platform.runLater(() -> {
            statusLabel.setText("RECORDING...");
            statusLabel.setTextFill(Color.web(UIConstants.STATUS_RECORDING_COLOR));
            pulseAnimation.play();
        });

        transcriptionService.startRecording();
    }

    private void stopRecording() {
        Platform.runLater(() -> {
            statusLabel.setText("PROCESSING...");
            statusLabel.setTextFill(Color.web(UIConstants.STATUS_PROCESSING_COLOR));
            pulseAnimation.stop();
            recordButton.setFill(Color.web(UIConstants.BUTTON_BACKGROUND_COLOR));
        });

        CompletableFuture
                .supplyAsync(transcriptionService::stopAndTranscribe)
                .thenCompose(transcription -> {
                    Platform.runLater(() -> {
                        transcriptLabel.setText("Command: \"" + transcription + "\"");
                        audioPlayerService.playWorkingSound();
                    });
                    return mcpService.processCommand(transcription);
                })
                .thenAccept(response -> Platform.runLater(() -> {
                    responseArea.setText(response);
                    statusLabel.setText("READY");
                    statusLabel.setTextFill(Color.web(UIConstants.STATUS_READY_COLOR));
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        responseArea.setText("ERROR: " + throwable.getMessage());
                        statusLabel.setText("ERROR");
                        statusLabel.setTextFill(Color.web(UIConstants.STATUS_RECORDING_COLOR)); // Using recording color for errors
                    });
                    return null;
                });
    }
}
