package playlistGenerator.controllers;

import playlistGenerator.factories.FeatureFactory;
import playlistGenerator.factories.TrackFactory;
import playlistGenerator.features.Feature;
import playlistGenerator.functionalInterfaces.Parser;
import playlistGenerator.models.Track;
import playlistGenerator.models.TrackMeta;
import playlistGenerator.tools.MP4Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ScanController {

    private static final int WINDOW_SIZE = 512;
    private static final double WINDOW_OVERLAP = 0.0;

    public final static String musicPath = "/Users/mbragg/Music/iTunes/iTunes Media/Music";
    public static final String M4A = ".m4a";
    private List<File> files;
    private List<Track> tracks;
    private List<Feature> features;
    private int trackIDs;
    private TrackFactory trackFactory;

    public ScanController() throws IOException {
        files  = new ArrayList<>();
        tracks = new ArrayList<>();
        trackIDs = 0;
        features = FeatureFactory.getInstance().getFeatureList();
        trackFactory = TrackFactory.getInstance();
    }

    public void launch() throws Exception {
        scanDirectory();
        buildTrackFromFile();
    }

    private void buildTrackFromFile() throws Exception {

        Parser<TrackMeta> metaParser  = new MP4Parser();
        ExtractionController analyser = new ExtractionController(WINDOW_SIZE, WINDOW_OVERLAP, features);

        for(File file : files) {
            Track track = trackFactory.buildTrack(metaParser, analyser, ++trackIDs, file);
            tracks.add(track);
            System.out.println(track.getId() + ": " + track.getMeta().getFilename() + ": " + track.getFeatureVector().values());
        }
    }

    private void scanDirectory() {
//        File file = new File("/Users/mbragg/IdeaProjects/PlaylistGenerator/music.m4a");
//        files.add(file);

        try {
            Files.walk(new File(musicPath).toPath())
                    .filter(p -> p.getFileName().toString().endsWith(M4A))
                    .filter(p -> !p.getFileName().toString().startsWith("."))
                    .forEach(f -> files.add(f.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
