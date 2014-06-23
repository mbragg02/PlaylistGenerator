package playlistGenerator.factories;


import playlistGenerator.features.Feature;
import playlistGenerator.features.MFCC;

import java.util.ArrayList;
import java.util.List;

public class FeatureFactory {

    private static FeatureFactory instance;

    private FeatureFactory() {
        // Private factory
    }

    public static FeatureFactory getInstance() {
        if(instance == null) {
            instance = new FeatureFactory();
        }
        return instance;
    }

    public List<Feature> getFeatureList() {

        List<Feature> features = new ArrayList<>();
        features.add(getMFCC());
        return features;
    }

    private Feature getMFCC() {
        return new MFCC();
    }
}
