package playlistGenerator.controllers;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class AudioStreamController {

    private File file;
    private float sampleRate;
    private int sampleSizeInBits;
    private int channels;
    private float frameRate;


    public AudioStreamController(File file) {
        this.file = file;
    }

    public AudioInputStream setAudioInputStream() throws IOException, UnsupportedAudioFileException {

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat audioFormat           = audioInputStream.getFormat();

        sampleRate = audioFormat.getSampleRate();
        sampleSizeInBits = audioFormat.getSampleSizeInBits();
        channels = audioFormat.getChannels();
        frameRate = audioFormat.getFrameRate();

        AudioFormat decodedAudioFormat = getAudioFormat();

        return AudioSystem.getAudioInputStream(decodedAudioFormat, audioInputStream);
    }

    public float getSampleRate() {
        return sampleRate;
    }

    private AudioFormat getAudioFormat() {

        // Create a new PCM_SIGNED audio format
        return new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sampleRate, sampleSizeInBits, channels,
                channels * 2, frameRate,
                true);
    }


}
