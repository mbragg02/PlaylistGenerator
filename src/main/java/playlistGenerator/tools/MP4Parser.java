//package playlistGenerator.tools;
//
//import com.coremedia.iso.IsoFile;
//import com.coremedia.iso.boxes.Box;
//import com.coremedia.iso.boxes.MetaBox;
//import com.coremedia.iso.boxes.MovieBox;
//import com.coremedia.iso.boxes.UserDataBox;
//import com.coremedia.iso.boxes.apple.AppleItemListBox;
//import playlistGenerator.functionalInterfaces.Parser;
//import playlistGenerator.factories.TrackMetaFactory;
//import playlistGenerator.models.TrackMeta;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.nio.channels.Channels;
//import java.nio.channels.WritableByteChannel;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Class to parse M4a audio files.
// *
// * @author Michael Bragg
// */
//public class MP4Parser implements Parser<TrackMeta> {
//
//
//
//    private final static int ATOM_BYTE_OFFSET = 16;
//
//    // iTunes track metadata 4 char codes
//    private final static String ALBUM_CODE = "\u00A9" + "alb";
//    private final static String ARTIST_CODE_1 = "\u00A9" + "art";
//    private final static String ARTIST_CODE_2 = "\u00A9" + "ART";
//    private final static String YEAR_CODE = "\u00A9" + "day";
//    private final static String TITLE_CODE = "\u00A9" + "nam";
//    private final static String GENRE_CODE_APPLE = "\u00A9" + "gen";
//    private final static String TRACK_NUMBER_CODE = "trkn";
//    private final static String BPM_CODE = "tmpo";
//    private final static String GENRE_CODE_STANDARD = "gnre";
//
//    private final static String[] metaCodes = {ALBUM_CODE, ARTIST_CODE_1, ARTIST_CODE_2, YEAR_CODE, TITLE_CODE, GENRE_CODE_APPLE, GENRE_CODE_STANDARD, BPM_CODE, TRACK_NUMBER_CODE};
//
//    private ByteArrayOutputStream out;
//    private WritableByteChannel channel;
//    private String type;
//    private Map<String, String> metaValues;
//
//
//    public MP4Parser() throws IOException {
//        out = new ByteArrayOutputStream();
//        channel = Channels.newChannel(out);
//        metaValues = new HashMap<>();
//
//        for(String code : metaCodes) {
//            metaValues.put(code, "");
//        }
//    }
//
//    public TrackMeta parse(File file) {
//
//        IsoFile isoFile = null;
//        try {
//            isoFile = new IsoFile(file.getAbsolutePath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        MovieBox movieBox = isoFile.getBoxes(MovieBox.class).get(0);
//        UserDataBox userDataBox = movieBox.getBoxes(UserDataBox.class).get(0);
//        MetaBox metaBox = userDataBox.getBoxes(MetaBox.class).get(0);
//        AppleItemListBox appleItemListBox = metaBox.getBoxes(AppleItemListBox.class).get(0);
//
//        for (Box x : appleItemListBox.getBoxes()) {
//            //System.out.println(x.getType());
//
//            Arrays.stream(metaCodes)
//                    .filter(y -> y.equals(x.getType())).forEach(y -> {
//                this.type = x.getType();
//                try {
//                    x.getBox(channel);
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                parseMeta();
//            });
//        }
//        try {
//            isoFile.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return buildTrackMeta(file);
//    }
//
//    private void parseMeta() {
//        byte[] outArray = out.toByteArray();
//        out.reset();
//
//
//        //System.out.println(type + ": " + Arrays.toString(outArray));
//
//
//        out.write(outArray, ATOM_BYTE_OFFSET, outArray.length - ATOM_BYTE_OFFSET);
//        String value = out.toString().trim();
//        //System.out.println(value);
//        //if (!value.isEmpty())
//        Arrays.stream(metaCodes).filter(type::equals).forEach(code -> metaValues.put(code, value));
//        out.reset();
//    }
//
//    private TrackMeta buildTrackMeta(File file) {
//        String album = metaValues.get(ALBUM_CODE);
//        String bpm = metaValues.get(BPM_CODE);
//        String year = metaValues.get(YEAR_CODE);
//        String title = metaValues.get(TITLE_CODE);
//
//        String artist = "";
//        if(!metaValues.get(ARTIST_CODE_1).isEmpty()) {
//            artist = metaValues.get(ARTIST_CODE_1);
//        }
//        if(!metaValues.get(ARTIST_CODE_2).isEmpty()) {
//            artist = metaValues.get(ARTIST_CODE_2);
//        }
//        //artist = metaValues.containsKey(ARTIST_CODE_1) ? metaValues.get(ARTIST_CODE_1) : metaValues.get(ARTIST_CODE_2);
//
//        String genre = "";
//        if(!metaValues.get(GENRE_CODE_APPLE).isEmpty()) {
//            genre = metaValues.get(GENRE_CODE_APPLE);
//        }
//        if(!metaValues.get(GENRE_CODE_STANDARD).isEmpty()) {
//            genre = metaValues.get(GENRE_CODE_STANDARD);
//        }
//
//        //String genre = metaValues.containsKey(GENRE_CODE_APPLE) ? metaValues.get(GENRE_CODE_APPLE) : metaValues.get(GENRE_CODE_STANDARD);
//
//        return TrackMetaFactory.getInstance().getTrackMeta(file.getName(), file.getAbsolutePath(), artist, album, genre, bpm, year, title);
//    }
//}
