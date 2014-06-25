package playlistGenerator.factories;

import org.jaudiotagger.tag.FieldKey;

import java.util.ArrayList;
import java.util.List;

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

    public List<FieldKey> getFieldKeys() {
        FieldKey title = FieldKey.TITLE;
        FieldKey artist = FieldKey.ARTIST;
        FieldKey album = FieldKey.ALBUM;
        FieldKey genre = FieldKey.GENRE;
        FieldKey year = FieldKey.YEAR;

        List<FieldKey> fieldKeys = new ArrayList<>();
        fieldKeys.add(title);
        fieldKeys.add(artist);
        fieldKeys.add(album);
        fieldKeys.add(genre);
        fieldKeys.add(year);

        return fieldKeys;

    }
}


