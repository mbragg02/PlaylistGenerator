//package playlistGenerator.features;
//
///**
// * Created by Michael Bragg on 28/02/2014.
// *
// */
//public class SpectralCentroid extends Feature {
//
//    @Override
//    public double[] extractFeature(double[] samples, double sampling_rate) {
////		FFT fft = new FFT(samples, null, false, true);
//
//        double[] pow_spectrum = fft.getPowerSpectrum();
//
//        double total = 0.0;
//        double weighted_total = 0.0;
//        for (int bin = 0; bin < pow_spectrum.length; bin++)
//        {
//            weighted_total += bin * pow_spectrum[bin];
//            total += pow_spectrum[bin];
//        }
//
//        double[] result = new double[1];
//        if(total != 0.0){
//            result[0] = weighted_total / total;
//        }else{
//            result[0] = 0.0;
//        }
//        return result;
//    }
//
//}
