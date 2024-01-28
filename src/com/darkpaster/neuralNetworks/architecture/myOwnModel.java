package com.darkpaster.neuralNetworks.architecture;

import com.darkpaster.neuralNetworks.NLPNeuralNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class myOwnModel extends NLPNeuralNetwork {
    public myOwnModel(double learningRate, byte window_size, AF activationFunction, WI weightsInitCase, int... neurNum) {
        super(learningRate, window_size, activationFunction, weightsInitCase, neurNum);
    }

    public void learn(int epoch, List<String> tokens, List<String> vocabulary) {
        epoch++;
        for (int i = 1; i < epoch; i++) {
            double errors = 0;
            double errorsStandard = 0;
            double errorsSoftmax = 0;
            int iterator = 0;
            for (int j = 0; j < tokens.size(); j++) {
                double[] out = new double[layers[layers.length - 1].neurons.length];
                out[vocabulary.indexOf(tokens.get(j))] = 1;
                //System.out.println(tokens.get(j));
                double[] result = feedForward(out);
                ArrayList<String> wordsOut = new ArrayList<>();
                for (int k = 1; k < WINDOW_SIZE + 1; k++) {
                    try {
                        wordsOut.add(tokens.get(j + k));
                    } catch (Exception ignored) {
                    }
                }
                double[] outNums = new double[layers[0].neurons.length];
                for (String word: wordsOut) {
                    outNums[vocabulary.indexOf(word)] = (double) 1 / (wordsOut.indexOf(word) + 1);
                }
                //System.out.println("target out: " + Arrays.toString(in));
                //System.out.println("real out: " + Arrays.toString(result));
                errors += getAbsoluteError(layers[layers.length - 1].neurons, outNums);
                errorsStandard += getStandardDeviation(layers[layers.length - 1].neurons);
                errorsSoftmax += categoricalCrossEntropyLoss(layers[layers.length - 1].neurons, outNums);
                iterator++;
                backPropagation(outNums);
            }
            System.out.println("standard deviation: " + errorsStandard / iterator
                    + ", softmax error: " + errorsSoftmax / iterator + ", absolute error: " + errors / iterator);
        }
    }
}
