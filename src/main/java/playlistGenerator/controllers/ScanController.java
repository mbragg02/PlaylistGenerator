package playlistGenerator.controllers;

import org.jaudiotagger.tag.FieldKey;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.helpers.collection.IteratorUtil;
import playlistGenerator.factories.FeatureFactory;
import playlistGenerator.factories.TrackFactory;
import playlistGenerator.factories.TrackMetaFactory;
import playlistGenerator.features.Feature;
import playlistGenerator.functionalInterfaces.Parser;
import playlistGenerator.models.Track;
import playlistGenerator.models.graphDb;
import playlistGenerator.tools.JAudioTagger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScanController {

    private static final int WINDOW_SIZE = 512;
    private static final double WINDOW_OVERLAP = 0.0;

    public final static String musicPath = "/Users/mbragg/Music/iTunes/iTunes Media/Music";
    public static final String M4A = ".m4a";
    private List<File> files;
    private List<Feature> features;
    private List<FieldKey> tagKeys;
    private int trackIDs;
    private TrackFactory trackFactory;
    private graphDb db;
    private final ExtractionController extractionController;
    private final Parser jAudioTagger;

    public ScanController() throws IOException {
        files  = new ArrayList<>();
        db = new graphDb();
        trackIDs = 0;
        features = FeatureFactory.getInstance().getFeatureList();
        trackFactory = TrackFactory.getInstance();
        tagKeys = TrackMetaFactory.getInstance().getFieldKeys();


        extractionController = new ExtractionController(WINDOW_SIZE, WINDOW_OVERLAP, features);
        jAudioTagger = new JAudioTagger(tagKeys);
    }

    public void launch() throws Exception {
        scan();
        buildTrack();

//        query();
        db.shutDown();
    }

    private void query() {

        String filename = "01 Saltwater.m4a";

        System.out.println("Query: " + filename);

        System.out.println("=========================");
        ExecutionResult result = db.getNearest(filename, 5);

        Iterator<String> t = result.columnAs("Neighbor");


        for (String s : IteratorUtil.asIterable(t))
        {
            System.out.println(s);

        }
    }



    private void buildTrack() throws Exception {

        for(File file : files) {
            if(!db.containsFileName(file.getName())) {

                // debug
                System.out.println(file.getName());

                Track track = trackFactory.buildTrack(jAudioTagger, extractionController, ++trackIDs, file);
                db.addNode(track);
            }
        }
    }

    private void scan() {
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
