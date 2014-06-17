package playlistGenerator.models;

/**
 * Class to represent an Audio Track
 *
 * @author Michael Bragg
 */
public class Track {

    private TrackMeta trackMeta;
    private int trackID;
    private String fileName;
    private String filePath;

    public Track(int trackID, String fileName, String filePath) {
        setTrackID(trackID);
        setFileName(fileName);
        setFilePath(filePath);
    }

    private void setTrackID(int trackID) {
        this.trackID = trackID;
    }

    private void setFileName(String filename) {
        this.fileName = filename;
    }

    public void setMeta(TrackMeta trackMeta) {
        this.trackMeta = trackMeta;
    }

    private void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public int getTrackID() {
        return trackID;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public TrackMeta getTrackMeta() {
        return trackMeta;
    }
}
