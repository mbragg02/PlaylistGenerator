package playlistGenerator.models;

import java.util.Arrays;
import java.util.Map;

/**
 * Class to represent an Audio Track
 *
 * @author Michael Bragg
 */
public class Track {

    private String filename;
    private String filePath;
    private Map<String, String> tags;

    private double[] featureVector;
    private double featureVectorLength;

    public Track(String filename, String filePath, Map<String, String> tags, double[] featureVector) {
        setFeatureVector(featureVector);
        setTrackTags(tags);
        setFilename(filename);
        setFilePath(filePath);
    }


    public Map<String, String> getTags() {
        return tags;
    }

    public double getFeatureVectorLength() {
        return featureVectorLength;
    }

    public double[] getFeatureVector() {
        return featureVector;
    }

    public void setFeatureVector(double[] featureVector) {
        this.featureVector = featureVector;
        setFeatureVectorLength(getVectorMagnitude(featureVector));
    }

    public String getFilename() {
        return filename;
    }

    private void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilePath() {
        return filePath;
    }

    private void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private void setTrackTags(Map<String, String> tags) {
        this.tags = tags;
    }

    private void setFeatureVectorLength(double length) {
        this.featureVectorLength = length;
    }

    private double getVectorMagnitude(double[] vectorValues) {
        double res = Arrays.stream(vectorValues)
                .map(v -> Math.pow(v, 2))
                .reduce(Double::sum)
                .getAsDouble();

        return Math.sqrt(res);
    }



    private double[] normalzeFeatureArray(double[] featureArray) {
        double unNormalizedLength = getVectorMagnitude(featureArray);
        return Arrays.stream(featureArray).map(d -> d / unNormalizedLength).toArray();
    }

}
