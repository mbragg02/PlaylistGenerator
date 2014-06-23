package playlistGenerator.models;

import java.util.Map;

/**
 * Class to represent an Audio Track
 *
 * @author Michael Bragg
 */
public class Track {

    private TrackMeta meta;
    private int trackID;
    private Map<String, Double> featureVector;


    public Track(int trackID, TrackMeta meta, Map<String, Double> featureVector) {
        setId(trackID);
        setMeta(meta);
        setFeatureVector(featureVector);
    }

    private void setFeatureVector(Map<String, Double> featureVector) {
        this.featureVector = featureVector;
    }

    private void setId(int trackID) {
        this.trackID = trackID;
    }

    private void setMeta(TrackMeta trackMeta) {
        this.meta = trackMeta;
    }

    public int getId() {
        return trackID;
    }

    public TrackMeta getMeta() {
        return meta;
    }

    public Map<String, Double> getFeatureVector() {
        return featureVector;
    }
}
