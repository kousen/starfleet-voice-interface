package com.kousenit.starfleetvoiceinterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class TranscriptionService {

    private static final Logger log = LoggerFactory.getLogger(TranscriptionService.class);

    private TargetDataLine microphone;
    private ByteArrayOutputStream audioBuffer;
    private boolean isRecording = false;
    private Thread recordingThread;
    private final OpenAiAudioTranscriptionModel transcriptionModel;


    // Audio format: 16 kHz, 16-bit, mono (Whisper prefers this)
    private final AudioFormat audioFormat = new AudioFormat(
            16000.0f,  // Sample rate
            16,        // Sample size in bits
            1,         // Channels (mono)
            true,      // Signed
            false      // Big endian
    );

    public TranscriptionService(@Autowired OpenAiAudioTranscriptionModel transcriptionModel) {
        // Initialize the audio format
        log.info("TranscriptionService initialized with audio format: {}", audioFormat);
        this.transcriptionModel = transcriptionModel;
    }

    public void startRecording() {
        try {
            // Get microphone
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            if (!AudioSystem.isLineSupported(info)) {
                throw new RuntimeException("Microphone not supported");
            }

            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(audioFormat);
            microphone.start();

            audioBuffer = new ByteArrayOutputStream();
            isRecording = true;

            // Start recording in separate thread
            recordingThread = new Thread(this::recordAudio);
            recordingThread.start();

            log.info("Recording started");

        } catch (LineUnavailableException e) {
            log.error("Failed to access microphone", e);
            throw new RuntimeException("Cannot access microphone: " + e.getMessage());
        }
    }

    private void recordAudio() {
        byte[] buffer = new byte[4096];

        while (isRecording) {
            int bytesRead = microphone.read(buffer, 0, buffer.length);
            if (bytesRead > 0) {
                audioBuffer.write(buffer, 0, bytesRead);
            }
        }
    }

    public String stopAndTranscribe() {
        if (!isRecording) {
            return "No recording in progress";
        }

        try {
            // Stop recording
            isRecording = false;
            recordingThread.join(1000); // Wait up to 1 second

            // Ensure microphone resources are properly closed
            try {
                if (microphone != null) {
                    microphone.stop();
                    microphone.close();
                }
            } catch (Exception e) {
                log.warn("Error closing microphone resources", e);
                // Continue processing with the data we have
            }

            byte[] audioData = audioBuffer != null ? audioBuffer.toByteArray() : new byte[0];

            if (audioData.length == 0) {
                log.warn("No audio data recorded");
                return "No audio data recorded. Please try again.";
            }

            log.info("Recorded {} bytes of audio", audioData.length);

            // Convert to WAV format
            byte[] wavData = convertToWav(audioData);

            // Create resource for Spring AI
            Resource audioResource = new ByteArrayResource(wavData) {
                @Override
                public String getFilename() {
                    return "recording.wav";
                }
            };

            // Transcribe
            var prompt = new AudioTranscriptionPrompt(
                    audioResource,
                    OpenAiAudioTranscriptionOptions.builder()
                            .language("en")
                            .temperature(0.0f)
                            .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                            .build()
            );

            AudioTranscriptionResponse response = transcriptionModel.call(prompt);
            String transcription = response.getResult().getOutput().trim();

            log.info("Transcription: {}", transcription);
            return transcription;

        } catch (Exception e) {
            log.error("Failed to transcribe audio", e);
            return "Error: " + e.getMessage();
        }
    }

    private byte[] convertToWav(byte[] audioData) throws IOException {
        ByteArrayOutputStream wavOutput = new ByteArrayOutputStream();

        // WAV header
        int sampleRate = (int) audioFormat.getSampleRate();
        int channels = audioFormat.getChannels();
        int bitsPerSample = audioFormat.getSampleSizeInBits();
        int byteRate = sampleRate * channels * bitsPerSample / 8;
        int blockAlign = channels * bitsPerSample / 8;
        int dataSize = audioData.length;
        int fileSize = dataSize + 36; // 36 = header size - 8

        // Write WAV header
        wavOutput.write("RIFF".getBytes());
        writeLittleEndianInt(wavOutput, fileSize);
        wavOutput.write("WAVE".getBytes());
        wavOutput.write("fmt ".getBytes());
        writeLittleEndianInt(wavOutput, 16); // Subchunk1Size
        writeLittleEndianShort(wavOutput, (short) 1); // AudioFormat (1 = PCM)
        writeLittleEndianShort(wavOutput, (short) channels);
        writeLittleEndianInt(wavOutput, sampleRate);
        writeLittleEndianInt(wavOutput, byteRate);
        writeLittleEndianShort(wavOutput, (short) blockAlign);
        writeLittleEndianShort(wavOutput, (short) bitsPerSample);
        wavOutput.write("data".getBytes());
        writeLittleEndianInt(wavOutput, dataSize);
        wavOutput.write(audioData);

        return wavOutput.toByteArray();
    }

    private void writeLittleEndianInt(ByteArrayOutputStream out, int value) {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 24) & 0xFF);
    }

    private void writeLittleEndianShort(ByteArrayOutputStream out, short value) {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
    }
}
