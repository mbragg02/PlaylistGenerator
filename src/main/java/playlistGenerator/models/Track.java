package playlistGenerator.models;

import java.util.Arrays;
import java.util.Map;

/**
 * Class to represent an Audio Track
 *
 * @author Michael Bragg
 */
public class Track {

    private int trackID;
    private double[] featureVector;
    private double featureVectorLength;
    private String filename;
    private String filepath;
    private Map<String, String> tags;

    public Track(int trackID, String filename, String filepath, Map<String, String> tags, double[] featureVector) {
        setId(trackID);
        setFeatureVector(featureVector);
        setTrackTags(tags);
        setFilename(filename);
        setFilepath(filepath);
    }

    public int getId() {
        return trackID;
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

    public String getFilepath() {
        return filepath;
    }

    private void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    private void setTrackTags(Map<String, String> tags) {
        this.tags = tags;
    }

    private void setId(int trackID) {
        this.trackID = trackID;
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
