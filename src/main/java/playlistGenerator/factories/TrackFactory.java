package playlistGenerator.factories;


import playlistGenerator.controllers.ExtractionController;
import playlistGenerator.functionalInterfaces.Parser;
import playlistGenerator.models.Track;
import playlistGenerator.models.TrackMeta;

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


   public Track buildTrack(Parser<TrackMeta> metaParser, ExtractionController analyser, int trackId, File file) throws Exception {
       TrackMeta trackMeta = metaParser.parse(file);
       Map<String, Double> featureVector = analyser.extract(file);
       return new Track(trackId, trackMeta, featureVector);
   }
}
