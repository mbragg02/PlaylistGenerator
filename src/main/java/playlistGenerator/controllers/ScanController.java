package playlistGenerator.controllers;

import org.jaudiotagger.tag.FieldKey;
import playlistGenerator.factories.FeatureFactory;
import playlistGenerator.factories.TrackFactory;
import playlistGenerator.factories.TrackMetaFactory;
import playlistGenerator.features.Feature;
import playlistGenerator.functionalInterfaces.Parser;
import playlistGenerator.models.graphDb;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ScanController {

    public final static String musicPath = "/Users/mbragg/Music/iTunes/iTunes Media/Music";
    public static final String M4A = ".m4a";
    private List<File> files;
    private List<Feature> features;
    private List<FieldKey> tagKeys;
    private TrackFactory trackFactory;
    private graphDb db;
    private final ExtractionController extractionController;
    private final Parser tagsController;
    private final PlaylistController playlistController;

    public ScanController() throws IOException {
        files  = new ArrayList<>();
        db = new graphDb();

        features = FeatureFactory.getInstance().getFeatureList();
        trackFactory = TrackFactory.getInstance();
        tagKeys = TrackMetaFactory.getInstance().getFieldKeys();

        extractionController = new ExtractionController(features);
        tagsController       = new TagsController(tagKeys);
        playlistController   = new PlaylistController();



    }

    public void query() throws IOException {

        String filename = "01 It's On!.m4a";

        System.out.println("Query: " + filename);

        System.out.println("===================================");
        List<String> playlist = db.getNearest(filename, 10);

        playlistController.create(playlist);
        db.shutDown();
    }



    private void buildTracks() throws Exception {
        System.out.println("Total files in library: " + files.size());
        int counter = 0;
        for(File file : files) {
            if(!db.containsFileName(file.getName())) {
                System.out.println("[" + counter + " of " + files.size() + "] " + file.getName());
                db.addNode(trackFactory.buildTrack(tagsController, extractionController, file));
            }
            counter++;
        }
        db.shutDown();
    }

    public void scan() throws Exception {
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
        buildTracks();
    }


}
