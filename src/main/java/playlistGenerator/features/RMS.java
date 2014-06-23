package playlistGenerator.features;

/**
 * Created by Michael Bragg on 28/02/2014.
 *
 */
public class RMS extends Feature {

    @Override
    public double[] extractFeature(double[] samples, double sampling_rate) {
        double sum = 0.0;
        for (double sample1 : samples) {
            sum += Math.pow(sample1, 2);
        }

        double rms = Math.sqrt(sum / samples.length);
        double[] result = new double[1];
        result[0] = rms;
        return result;
    }



}
