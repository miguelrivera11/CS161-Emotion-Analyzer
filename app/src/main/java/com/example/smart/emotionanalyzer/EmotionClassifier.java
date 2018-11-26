package com.example.smart.emotionanalyzer;

import android.app.Activity;
import android.content.res.AssetManager;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class EmotionClassifier{

    private static final int NUM_CLASSES = 3;
    private static final int BATCH_SIZE = 24;
    private static final int MAX_SEQ_LENGTH = 35;
    private static final String INPUT_NAME = "input_data";
    private static final String OUTPUT_NAME = "add";

    public static final int ANGRY = 0;
    public static final int HAPPY = 1;
    public static final int SAD = 2;

    private TensorFlowInferenceInterface tf;
    private ArrayList<String> wordList = new ArrayList<>();

    public EmotionClassifier(Activity context) throws IOException {
        AssetManager assetManager = context.getAssets();
        tf = new TensorFlowInferenceInterface(assetManager, "file:///android_asset/frozen_model.pb");
        InputStream inputStream = assetManager.open("wordList.txt");
        Scanner in = new Scanner(inputStream);
        while (in.hasNextLine()){
            wordList.add(in.nextLine());
        }
    }

    public TensorFlowInferenceInterface getTf() {
        return tf;
    }

    public void setTf(TensorFlowInferenceInterface tf) {
        this.tf = tf;
    }

    private String cleanSentences(String text) {
        return text.replaceAll("[^A-Za-z0-9 ]", "");
    }

    private int[][] getMatrix(String text) {
        String clean = cleanSentences(text);
        String[] split = clean.split(" ");
        int[][] matrix = new int[BATCH_SIZE][MAX_SEQ_LENGTH];
        for (int i = 0; i < split.length; i++) {
            int index = wordList.indexOf(split[i]);
            if (index < 0) {
                matrix[0][i] = 399999;
            }
            else {
                matrix[0][i] = index;
            }
        }
        return matrix;
    }

    public int predict(String text) {
        int[][] matrix = getMatrix(text);
        long[] size = {MAX_SEQ_LENGTH};
        tf.feed("Placeholder_1", matrix[0], size);
        String[] outputNodes = {OUTPUT_NAME};
        float[] predictedSentiment = new float[3];
        tf.run(outputNodes);
        tf.fetch(OUTPUT_NAME, predictedSentiment);
        if(predictedSentiment[0] > predictedSentiment[1] && predictedSentiment[0] > predictedSentiment[2])
        {
            return ANGRY;
        }
        else if(predictedSentiment[1] > predictedSentiment[0] && predictedSentiment[1] > predictedSentiment[2])
        {
            return HAPPY;
        }
        else if(predictedSentiment[2] > predictedSentiment[1] && predictedSentiment[2] > predictedSentiment[0])
        {
            return SAD;
        }
        else {
            return -1;
        }
    }
}
