package playlistGenerator.tools;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.MetaBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.UserDataBox;
import com.coremedia.iso.boxes.apple.AppleItemListBox;
import playlistGenerator.factories.TrackMetaFactory;
import playlistGenerator.models.TrackMeta;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to parse M4a audio files.
 *
 * @author Michael Bragg
 */
public class MP4Parser {

    private final static int ATOM_BYTE_OFFSET = 16;

    // iTunes track metadata 4 char codes
    private final static String ALBUM_CODE = "\u00A9" + "alb";
    private final static String ARTIST_CODE_1 = "\u00A9" + "art";
    private final static String ARTIST_CODE_2 = "\u00A9" + "ART";
    private final static String YEAR_CODE = "\u00A9" + "day";
    private final static String TITLE_CODE = "\u00A9" + "nam";
    private final static String GENRE_CODE_APPLE = "\u00A9" + "gen";
    private final static String TRACK_NUMBER_CODE = "trkn";
    private final static String BPM_CODE = "tmpo";
    private final static String GENRE_CODE_STANDARD = "gnre";

    private final static String[] metaCodes = {ALBUM_CODE, ARTIST_CODE_1, ARTIST_CODE_2, YEAR_CODE, TITLE_CODE, GENRE_CODE_APPLE, GENRE_CODE_STANDARD, BPM_CODE, TRACK_NUMBER_CODE};

    private ByteArrayOutputStream out;
    private WritableByteChannel channel;
    private String type;
    private Map<String, String> metaValues;


    public MP4Parser() throws IOException {
        out = new ByteArrayOutputStream();
        channel = Channels.newChannel(out);
        metaValues = new HashMap<>();
    }

    public TrackMeta getMeta(String filePath) throws IOException {

        IsoFile isoFile = new IsoFile(filePath);

        MovieBox movieBox = isoFile.getBoxes(MovieBox.class).get(0);
        UserDataBox userDataBox = movieBox.getBoxes(UserDataBox.class).get(0);
        MetaBox metaBox = userDataBox.getBoxes(MetaBox.class).get(0);
        AppleItemListBox appleItemListBox = metaBox.getBoxes(AppleItemListBox.class).get(0);

        for (Box x : appleItemListBox.getBoxes()) {
            Arrays.stream(metaCodes)
                    .filter(y -> x.getType().equals(y)).forEach(y -> {
                this.type = x.getType();
                try {
                    x.getBox(channel);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                parseMeta();
            });
        }
        isoFile.close();
        return buildTrackMeta();
    }

    private void parseMeta() {
        byte[] outArray = out.toByteArray();
        out.reset();
        out.write(outArray, ATOM_BYTE_OFFSET, outArray.length - ATOM_BYTE_OFFSET);
        String value = out.toString().trim();
        if (!value.isEmpty())
            Arrays.stream(metaCodes).filter(type::equals).forEach(code -> metaValues.put(code, value));
        out.reset();
    }

    private TrackMeta buildTrackMeta() {
        String album = metaValues.get(ALBUM_CODE);
        String bpm = metaValues.get(BPM_CODE);
        String year = metaValues.get(YEAR_CODE);
        String title = metaValues.get(TITLE_CODE);
        String artist = metaValues.containsKey(ARTIST_CODE_1) ? metaValues.get(ARTIST_CODE_1) : metaValues.get(ARTIST_CODE_2);
        String genre = metaValues.containsKey(GENRE_CODE_APPLE) ? metaValues.get(GENRE_CODE_APPLE) : metaValues.get(GENRE_CODE_STANDARD);

        return TrackMetaFactory.getInstance().getTrackMeta(artist, album, genre, bpm, year, title);
    }
}
