package playlistGenerator.models;

/**
 * Class to represent an Audio Track
 *
 * @author Michael Bragg
 */
public class Track {

    private TrackMeta meta;
    private int trackID;


    public Track(int trackID, TrackMeta meta) {
        setId(trackID);
        setMeta(meta);

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
}
