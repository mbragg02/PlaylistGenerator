package playlistGenerator.controllers;

import playlistGenerator.features.Feature;
import playlistGenerator.tools.AudioFunctions;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ExtractionController {

    private int windowSize;
    private int windowOverlapOffset;
    private List<Feature> featuresToExtract;
    private double samplingRate;

    public ExtractionController(int windowSize, double windowOverlap, List<Feature> featuresToExtract) {
        this.featuresToExtract = featuresToExtract;
        this.windowSize        = windowSize;
        this.windowOverlapOffset = (int) (windowOverlap * (double) windowSize);
    }

    public double[] extract(File file) throws Exception {

        // Extract audio samples from file
        double[] samples = extractSamples(file);

        // Calculate the window start positions
        int[] windowStartPositions = calculateWindowStartPositions(samples);

        double[][][] windowFeatureValues = getFeatures(samples, windowStartPositions);

        return getAverageVector(windowFeatureValues);

    }

    private double[] extractSamples(File file) throws Exception {

        AudioInputStream stream = AudioFunctions.getAudioFileToAudioInputStream(file, AudioSystem::getAudioInputStream);

        samplingRate = stream.getFormat().getSampleRate();

        return AudioFunctions.getSamplesInMono(AudioFunctions.extractSampleValues(stream));
    }

    public double[][][] getFeatures(double[] samples, int[] windowStartPositions) throws Exception {

        double[][][] results = new double[windowStartPositions.length][featuresToExtract.size()][];

        for (int win = 0; win < windowStartPositions.length; win++) {

            double[] window = new double[windowSize];

            // Set window sample positions
            int startSample = windowStartPositions[win];
            int endSample = startSample + windowSize - 1;

            // Get the samples for the current window
            if (endSample < samples.length) {
                System.arraycopy(samples, startSample, window, 0, endSample + 1 - startSample);
            }
            else {
                // Case when end of window is larger than the number of samples. i.e reached then end of the file
                // Pad the end of the window with zeros.

                for (int sample = startSample; sample <= endSample; sample++) {
                    if (sample < samples.length)
                        window[sample - startSample] = samples[sample];
                    else
                        window[sample - startSample] = 0.0;
                }
            }

            List<double[]> values = featuresToExtract.stream()
                    .map(f -> f.extractFeature(window, samplingRate))
                    .collect(Collectors.toList());


            for (int i = 0; i < values.size(); i++) {
                // Loops over list of features extracted values. Adds them to result array.
                results[win][i] = values.get(i);

            }
        }

        return results;
    }

    private double[] getAverageVector(double[][][] windowFeatureValues) {
        // windowFeatureValues [window][feature][values]

        Map<String, Double> values = new HashMap<>();
        double[] result = new double[0];

        String featureName;

        for (int feat = 0; feat < featuresToExtract.size(); feat++) {

            double averages;
            double stdvs = 0;

            int numberOfWindows = windowFeatureValues.length - 1;
            featureName = featuresToExtract.get(feat).getFeatureName().toLowerCase();

            result = new double[windowFeatureValues[numberOfWindows][feat].length];


            // change val to 1 to avoid low frequency bias
            for (int val = 1; val < windowFeatureValues[numberOfWindows][feat].length; val++) {

                // Find the values to find the average and standard deviations of
                double[] valuesToProcess = new double[windowFeatureValues.length];

                int current = 0;
                for (double[][] windowFeatureValue : windowFeatureValues) {
                    if (windowFeatureValue[feat] != null) {
                        valuesToProcess[current] = windowFeatureValue[feat][val];
                        current++;
                    }
                }
                // Calculate the averages (and standard deviations)
               // averages = Statistics.getAverage(valuesToProcess);
                averages = Arrays.stream(valuesToProcess).average().getAsDouble();

                 //stdvs = Statistics.getStandardDeviation(valuesToProcess);

                // Store the results
                values.put(featureName + val + "_avg", averages);
                result[val] = averages;
                // values.put(featureName + val + "_stdvs", stdvs);
            }

        }
        //return values;
        return result;

    }



//    private Map<String, Double> normalizeFeatureVector(Map<String, Double> overallFeatures) {
//        Map<String, Double> result = new HashMap<>();
//
//        double vectorMagnitude = getVectorMagnitude(overallFeatures.values());
//
//        overallFeatures.entrySet()
//                .stream()
//                .forEach(v -> {
//                    double value = v.getValue() / vectorMagnitude;
//                    result.put(v.getKey(), value);
//                });
//
//        //overallFeatures.entrySet().stream().map( x -> x.getValue() / vectorMagnitude);
//
//        return result;
//    }





    private int[] calculateWindowStartPositions(double[] samples) {

        LinkedList<Integer> windowStartPositionsList = new LinkedList<>();

        int currentStartPosition = 0;
        while (currentStartPosition < samples.length) {
            windowStartPositionsList.add(currentStartPosition);
            currentStartPosition += windowSize - windowOverlapOffset;
        }

        Integer[] windowStartIndices = windowStartPositionsList.toArray(new Integer[1]);
        int[] windowStartPositions = new int[windowStartIndices.length];

        for (int i = 0; i < windowStartPositions.length; i++)
            windowStartPositions[i] = windowStartIndices[i];

        return windowStartPositions;
    }


}
