package playlistGenerator;

import playlistGenerator.features.Feature;
import playlistGenerator.tools.AudioUtilities;
import playlistGenerator.tools.Statistics;

import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class FileAnalyser {

    private int windowSize;
    private int windowOverlapOffset;
    private List<Feature> featuresToExtract;
    private double samplingRate;

    public FileAnalyser(int windowSize, double windowOverlap, List<Feature> featuresToExtract) {
        this.featuresToExtract = featuresToExtract;
        this.windowSize        = windowSize;
        this.windowOverlapOffset = (int) (windowOverlap * (double) windowSize);
    }

    public void extract() throws Exception {
        File file = new File("/Users/mbragg/IdeaProjects/PlaylistGenerator/music.m4a");

        // Extract audio samples from file
        double[] samples = extractSamples(file);

        // Calculate the window start positions
        int[] windowStartPositions = calculateWindowStartPositions(samples);

        double[][][] windowFeatureValues = getFeatures(samples, windowStartPositions);

        Map<String, Double> averageVector =  getAverageVector(windowFeatureValues);

        Map<String, Double> normalizedVector = normalizeVector(averageVector);

        System.out.println(normalizedVector);

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

    private Map<String, Double> getAverageVector(double[][][] windowFeatureValues) {
        // windowFeatureValues [window][feature][values]

        Map<String, Double> values = new HashMap<>();

        String featureName;

        for (int feat = 0; feat < featuresToExtract.size(); feat++) {

            double averages;
            double stdvs = 0;

            int numberOfWindows = windowFeatureValues.length - 1;
            featureName = featuresToExtract.get(feat).getFeatureName().toLowerCase();

            for (int val = 0; val < windowFeatureValues[numberOfWindows][feat].length; val++) {

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
                averages = Statistics.getAverage(valuesToProcess);
                 stdvs = Statistics.getStandardDeviation(valuesToProcess);


                // Store the results
                values.put(featureName + val + "_avg", averages);
                // values.put(featureName + val + "_stdvs", stdvs);
            }

        }
        return values;

    }


    private Map<String, Double> normalizeVector(Map<String, Double> overallFeatures) {
        Map<String, Double> result = new HashMap<>();

        double vectorMagnitude = getVectorMagnitude(overallFeatures.values());

        overallFeatures.entrySet()
                .stream()
                .forEach(v -> {
                    double value = v.getValue() / vectorMagnitude;
                    result.put(v.getKey(), value);
                });

        //overallFeatures.entrySet().stream().map( x -> x.getValue() / vectorMagnitude);

        return result;
    }

    private double getVectorMagnitude(Collection<Double> vectorValues) {
        return vectorValues
                .stream()
                .map(v -> Math.pow(v, 2))
                .reduce(Double::sum)
                .map(Math::sqrt)
                .get();
    }



    private double[] extractSamples(File file) throws Exception {
        // files.stream().map(AudioUtilities::convertM4atoAudioInputStream).forEach();

        AudioInputStream stream = AudioUtilities.convertM4atoAudioInputStream(file);

        samplingRate = stream.getFormat().getSampleRate();

       return AudioUtilities.getSamplesInMono(AudioUtilities.extractSampleValues(stream));
    }

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
