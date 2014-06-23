package playlistGenerator;

import playlistGenerator.features.Feature;
import playlistGenerator.tools.AudioUtilities;
import playlistGenerator.tools.Statistics;

import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.util.*;

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




        double[][][] window_feature_values = getFeatures(samples, windowStartPositions);

        Map<String, Double> mfccAverageVectors =  getMFCCAverageVector(window_feature_values);


        System.out.println(mfccAverageVectors);



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

                for (int samp = startSample; samp <= endSample; samp++) {
                    if (samp < samples.length)
                        window[samp - startSample] = samples[samp];
                    else
                        window[samp - startSample] = 0.0;
                }
            }


            List<double[]> values = new ArrayList<>();

            featuresToExtract.stream().map(f -> f.extractFeature(window, samplingRate)).forEach(values::add);

            for (int i = 0; i < values.size(); ++i) {
                results[win][i] = values.get(i);
            }
        }

        return results;
    }

    private Map<String, Double> getMFCCAverageVector(double[][][] window_feature_values) {
        // window_feature_values [window int ][feature int ][values]
        Map<String, Double> values = new HashMap<>();

        String feautureName;

        for (int feat = 0; feat < featuresToExtract.size(); feat++) {

            double averages;
            //double stdvs = 0;

            int numberOfWindows = window_feature_values.length - 1;
            feautureName = featuresToExtract.get(feat).getFeatureName().toLowerCase();

            for (int val = 0; val < window_feature_values[numberOfWindows][feat].length; val++) {

                // Find the values to find the average and standard deviations of
                double[] values_to_process = new double[window_feature_values.length];

                int current = 0;
                for (double[][] window_feature_value : window_feature_values) {
                    if (window_feature_value[feat] != null) {
                        values_to_process[current] = window_feature_value[feat][val];
                        current++;
                    }
                }
                // Calculate the averages and standard deviations
                averages = Statistics.getAverage(values_to_process);
                // stdvs = Statistics.getStandardDeviation(values_to_process);


                // Store the results
                values.put(feautureName + val + "_avg", averages);
                // values.put(feautureName + val + "_stdvs", stdvs);
            }

        }
        return values;

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

        Integer[] windowStartIndicesI = windowStartPositionsList.toArray(new Integer[1]);
        int[] windowStartPositions = new int[windowStartIndicesI.length];

        for (int i = 0; i < windowStartPositions.length; i++)
            windowStartPositions[i] = windowStartIndicesI[i];

        return windowStartPositions;
    }



}
