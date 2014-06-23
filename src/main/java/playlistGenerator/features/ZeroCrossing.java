package playlistGenerator.features;

/**
 * Created by Michael Bragg on 28/02/2014.
 *
 */
public class ZeroCrossing extends Feature{

    @Override
    public double[] extractFeature(double[] samples, double sampling_rate) {
        long count = 0;
        for (int samp = 0; samp < samples.length - 1; samp++)
        {
            if (samples[samp] > 0.0 && samples[samp + 1] < 0.0)
                count++;
            else if (samples[samp] < 0.0 && samples[samp + 1] > 0.0)
                count++;
            else if (samples[samp] == 0.0 && samples[samp + 1] != 0.0)
                count++;
        }
        double[] result = new double[1];
        result[0] = (double) count;
        return result;
    }

}
