package playlistGenerator.features;

/**
 * Created by Michael Bragg on 28/02/2014.
 *
 */
public abstract class Feature {

    public String getFeatureName() {
        return this.getClass().getSimpleName();
    }

    public abstract double[] extractFeature(double[] samples, double sampling_rate);
}
