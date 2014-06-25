package playlistGenerator.features;

import org.oc.ocvolume.dsp.featureExtraction;

/**
 * Created by Michael Bragg on 04/03/2014.
 *
 *
 */
public class MFCC extends Feature {

    private featureExtraction fe;

    public MFCC() {
        fe = new featureExtraction();
    }

    @Override
    public double[] extractFeature(double[] samples, double sampling_rate) {

        double[] magnitudeSpectrum       = fe.magnitudeSpectrum(samples);
        int[] binIndices                 = fe.fftBinIndices(sampling_rate, magnitudeSpectrum.length);
        double[] fbank                   = fe.melFilter(magnitudeSpectrum, binIndices);
        double[] nonLinearTransformation = fe.nonLinearTransformation(fbank);

        return fe.cepCoefficients(nonLinearTransformation);
    }
}
