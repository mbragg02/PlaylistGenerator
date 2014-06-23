//package playlistGenerator.features;
//
///**
// * Created by Michael Bragg on 28/02/2014.
// *
// */
//public class SpectralRolloffPoint extends Feature {
//    protected double cutoff = 0.85;
//
//    @Override
//    public double[] extractFeature(double[] samples, double sampling_rate) {
//        double[] pow_spectrum = fft.getPowerSpectrum();
//
//        double total = 0.0;
//        for (double aPow_spectrum : pow_spectrum) total += aPow_spectrum;
//        double threshold = total * cutoff;
//
//        total = 0.0;
//        int point = 0;
//        for (int bin = 0; bin < pow_spectrum.length; bin++) {
//            total += pow_spectrum[bin];
//            if (total >= threshold) {
//                point = bin;
//                bin = pow_spectrum.length;
//            }
//        }
//
//        double[] result = new double[1];
//        result[0] = ((double) point) / ((double) pow_spectrum.length);
//        return result;
//    }
//
//}
