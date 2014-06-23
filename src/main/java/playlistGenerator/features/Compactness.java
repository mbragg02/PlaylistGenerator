//package playlistGenerator.features;
//
///**
// * Created by Michael Bragg on 28/02/2014.
// *
// */
//public class Compactness extends Feature {
//
//    @Override
//    public double[] extractFeature(double[] samples, double sampling_rate) {
//        double[] mag_spec = fft.getMagnitudeSpectrum();
//
//
//
//        double compactness = 0.0;
//        for (int i = 1; i < mag_spec.length - 1; i++) {
//            if ((mag_spec[i - 1] > 0.0) && (mag_spec[i] > 0.0) && (mag_spec[i + 1] > 0.0)) {
//                compactness += Math.abs(20.0 * Math.log(mag_spec[i]) - 20.0 * (Math.log(mag_spec[i - 1]) + Math.log(mag_spec[i]) + Math .log(mag_spec[i + 1])) / 3.0);
//            }
//        }
//        double[] result = new double[1];
//        result[0] = compactness;
//        return result;
//    }
//
//}
