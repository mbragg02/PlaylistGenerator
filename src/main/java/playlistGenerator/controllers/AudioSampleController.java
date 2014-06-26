package playlistGenerator.controllers;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class AudioSampleController {

    public double[][] getSamplesInStereo(AudioInputStream audioInputStream) throws Exception {

        byte[] audioBytes = getBytesFromAudioInputStream(audioInputStream);

        AudioFormat format = audioInputStream.getFormat();

        // Extract information from format
        int numberOfChannels = format.getChannels();
        int bitDepth = format.getSampleSizeInBits();

        // Throw exception if incompatible format provided
        if ((bitDepth != 16 && bitDepth != 8) ||
                !format.isBigEndian() ||
                format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED)
            throw new Exception("Only 8 or 16 bit signed PCM samples with a big-byte order can be analyzed");

        // Find the number of samples in the audioBytes
        int numberOfBytes = audioBytes.length;
        int bytesPerSample = bitDepth / 8;
        int numberOfSamples = numberOfBytes / bytesPerSample / numberOfChannels;

        // Throw exception if incorrect number of bytes given
        if (((numberOfSamples == 2 || bytesPerSample == 2) && (numberOfBytes % 2 != 0)) ||
                ((numberOfSamples == 2 && bytesPerSample == 2) && (numberOfBytes % 4 != 0)))
            throw new Exception("Uneven number of bytes for given bit depth and number of channels");

        // Find the maximum possible value that a sample may have with the given bit depth
        double maximumSampleValue = findMaximumSampleValue(bitDepth) + 2.0;

        // Instantiate the sample value holder
        double[][] samples = new double[numberOfChannels][numberOfSamples];

        // Convert the bytes to double samples
        ByteBuffer byteBuffer = ByteBuffer.wrap(audioBytes);
        if (bitDepth == 8) {
            for (int sample = 0; sample < numberOfSamples; sample++)
                for (int chan = 0; chan < numberOfChannels; chan++)
                    samples[chan][sample] = (double) byteBuffer.get() / maximumSampleValue;
        } else {
            // bitDepth == 16
            ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
            for (int samp = 0; samp < numberOfSamples; samp++)
                for (int chan = 0; chan < numberOfChannels; chan++)
                    samples[chan][samp] = (double) shortBuffer.get() / maximumSampleValue;
        }
        return samples;

    }

    private byte[] getBytesFromAudioInputStream(AudioInputStream audioInputStream) throws IOException {

        // Calculate the buffer size to use
        float bufferDuration = 0.25F;
        int bufferOverlap = 2;

        int bufferSize = getNumberBytesNeeded(bufferDuration, audioInputStream.getFormat());
        byte[] byteBuffer = new byte[bufferSize + bufferOverlap];

        // Read the bytes into the byteBuffer and then into the ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int position = audioInputStream.read(byteBuffer, 0, byteBuffer.length);

        // Bottleneck here
        while (position > 0) {
            position = audioInputStream.read(byteBuffer, 0, byteBuffer.length);
            byteArrayOutputStream.write(byteBuffer, 0, position);
        }

        byte[] byteArray = byteArrayOutputStream.toByteArray();

        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();

            // ONLY FOR TESTING
            System.exit(0);
        }

        return byteArray;
    }

    private int getNumberBytesNeeded(double bufferDuration, AudioFormat audioFormat) {
        int frameSizeInBytes = audioFormat.getFrameSize();
        float frameRate = audioFormat.getFrameRate();
        return (int) (frameSizeInBytes * frameRate * bufferDuration);
    }


    private double findMaximumSampleValue(int bitDepth) {
        int maxSampleValue = 1;
        for (int i = 0; i < (bitDepth - 1); i++)
            maxSampleValue *= 2;
        maxSampleValue--;
        return ((double) maxSampleValue) - 1.0;
    }


    public double[] getSamplesInMono(AudioInputStream audioInputStream) throws Exception {

        double[][] audioSamples = getSamplesInStereo(audioInputStream);

        if (audioSamples.length == 1)
            return audioSamples[0];

        double numberOfSamples = (double) audioSamples.length;
        int channels = audioSamples[0].length;

        double[] samplesInMono = new double[channels];

        for (int sample = 0; sample < channels; sample++) {
            double runningSampleTotal = 0.0;
            for (int chan = 0; chan < numberOfSamples; chan++) {
                runningSampleTotal += audioSamples[chan][sample];
            }
            samplesInMono[sample] = runningSampleTotal / numberOfSamples;
        }
        return samplesInMono;
    }


}
