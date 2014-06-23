package playlistGenerator;

import playlistGenerator.features.Feature;
import playlistGenerator.features.MFCC;
import playlistGenerator.models.Track;
import playlistGenerator.models.TrackMeta;
import playlistGenerator.tools.MP4Parser;
import playlistGenerator.tools.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final int    WINDOWSIZE    = 512;
    private static final double WINDOWOVERLAP = 0.0;

    public final static String musicPath = "/Users/mbragg/Music/iTunes/iTunes Media/Music";
    public static final String M4A = ".m4a";
    private List<File> files;
    private List<Track> tracks;
    private int trackIDs;

    public static void main(String[] args) throws Exception {

        Main main = new Main();

        main.launcher();
    }


    private void launcher() throws Exception {

        files = new ArrayList<>();
        tracks = new ArrayList<>();
        trackIDs = 0;



//        fileScanner();
//        trackBuilder();

        List<Feature> features = new ArrayList<>();
//        features.add(new RMS());
//        features.add(new ZeroCrossing());
        features.add(new MFCC());
        FileAnalyser analyser = new FileAnalyser(WINDOWSIZE, WINDOWOVERLAP, features);
        analyser.extract();

    }




    private void trackBuilder() throws IOException {
        Parser<TrackMeta> meta = new MP4Parser();

        tracks = files.stream()
                .map(meta::parse)
                .map(m -> new Track(trackIDs++, m))
                .collect(Collectors.toList());
    }

    private void fileScanner() {

        try {
            Files.walk(new File(musicPath).toPath())
                    .filter(p -> p.getFileName().toString().endsWith(M4A))
                    .filter(p -> !p.getFileName().toString().startsWith("."))
                    .forEach(p -> files.add(p.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
