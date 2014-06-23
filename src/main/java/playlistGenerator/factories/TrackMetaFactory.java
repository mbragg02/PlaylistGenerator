package playlistGenerator.factories;

import playlistGenerator.models.TrackMeta;

/**
 * Factory to create TrackMeta objects
 *
 * @author Michael Bragg
 */
public class TrackMetaFactory {

    private static TrackMetaFactory instance;

    private TrackMetaFactory() {
        // private constructor
    }

    public static TrackMetaFactory getInstance() {
        if(instance == null) {
            instance = new TrackMetaFactory();
        }
        return instance;
    }

    public TrackMeta getTrackMeta(String filename, String filePath, String artist, String album,
                                  String genre, String bpm, String year, String title) {
        return new TrackMeta(filename, filePath, artist, album, genre, bpm, year, title);
    }
}
