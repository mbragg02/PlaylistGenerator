package playlistGenerator.models;

/**
 * Class to represent metadata for an Audio Track
 *
 * @author Michael Bragg
 */
public class TrackMeta {
    private String artist;
    private String album;
    private String genre;
    private String bpm;
    private String year;
    private String title;

    public TrackMeta( String artist, String album, String genre, String bpm, String year, String title) {
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.bpm = bpm;
        this.year = year;
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public String getBpm() {
        return bpm;
    }

    public String getYear() {
        return year;
    }

    public String getTitle() {
        return title;
    }
}
