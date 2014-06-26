package playlistGenerator.factories;


import playlistGenerator.controllers.FeatureExtractionController;
import playlistGenerator.functionalInterfaces.Parser;
import playlistGenerator.models.Track;

import java.io.File;
import java.util.Map;

public class TrackFactory {

    private static TrackFactory instance;

    private TrackFactory() {
        // private constructor
    }

    public static TrackFactory getInstance() {
        if(instance == null) {
            instance = new TrackFactory();
        }
        return instance;
    }


   public Track buildTrack(Parser tagger, FeatureExtractionController featureExtraction, File file) throws Exception {

       Map<String, String> tags = tagger.parse(file);
       double[] featureVector = featureExtraction.extract(file);

       return new Track(file.getName(), file.getAbsolutePath(), tags, featureVector);
   }
}
