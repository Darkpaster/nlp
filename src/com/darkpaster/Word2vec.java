package com.darkpaster;

import java.util.ArrayList;
import java.util.Arrays;

public class Word2vec extends NeuralNetwork{
    private final double[][] embeddings;

    Word2vec(double learningRate, AF activationFunction, WI weightsInitCase, int... neurNum) {
        super(learningRate, activationFunction, weightsInitCase, neurNum);
        embeddings = new double[layers[layers.length-1].neurons.length][layers[1].weights.length];
    }


    public void skipGram(int epoch, ArrayList<ArrayList<String>> words, ArrayList<String> vocabulary) {
        epoch++;
        for (int i = 1; i < epoch; i++) {
            double rights = 0, errors = 0;
            for (ArrayList<String> proffer : words) {
                for (int j = 0; j < proffer.size(); j++) {
                    double[] in = new double[layers[0].neurons.length];
                    Arrays.fill(in, 0);
                    in[vocabulary.indexOf(proffer.get(j))] = 1;
                    feedForward(in);
                    ArrayList<String> wordTargets = new ArrayList<>();
                    for (int k = -Main.WINDOW_SIZE; k < Main.WINDOW_SIZE; k++) {
                        if (k == 0) continue;
                        try {
                            wordTargets.add(proffer.get(j + k));
                        } catch (Exception ignored) {
                        }
                    }
                    double[] numTargets = new double[layers[layers.length - 1].neurons.length];
                    Arrays.fill(numTargets, 0);
                    for (String word : wordTargets) {
                        numTargets[vocabulary.indexOf(word)] = 1;
                    }
                    for (int k = 0; k < numTargets.length; k++) {
                        errors += (numTargets[k] - layers[layers.length - 1].neurons[k]) * (numTargets[k] - layers[layers.length - 1].neurons[k]);
                    }
                    //System.out.println("Target: " + Arrays.toString(numTargets) + "|Real: " + Arrays.toString(layers[2].neurons));
                    backPropagation(numTargets);
                }
                System.out.println("errors: " + errors + ", deviation: " + getStandardDeviation(layers[layers.length-1].neurons));
            }
        }
        setEmbeddings(vocabulary);
    }

    public void cbow(int epoch, ArrayList<ArrayList<String>> words, ArrayList<String> vocabulary) {
        epoch++;
        for (int i = 1; i < epoch; i++) {
            double errors = 0;
            double errorsStandard = 0;
            double errorsSoftmax = 0;
            int iterator = 0;
            for (ArrayList<String> proffer : words) {
                for (int j = 0; j < proffer.size(); j++) {
                    ArrayList<String> wordsInput = new ArrayList<>();
                    for (int k = -Main.WINDOW_SIZE; k < Main.WINDOW_SIZE; k++) {
                        if (k == 0) continue;
                        try {
                            wordsInput.add(proffer.get(j + k));
                        } catch (Exception ignored) {
                        }
                    }
                    double[] in = new double[layers[0].neurons.length];
                    Arrays.fill(in, 0);
                    for (String word : wordsInput) {
                        in[vocabulary.indexOf(word)] = 1;
                    }
                    feedForward(in);
                    double[] out = new double[layers[layers.length-1].neurons.length];
                    Arrays.fill(out, 0);
                    out[vocabulary.indexOf(proffer.get(j))] = 1;

                    errors += getAbsoluteError(layers[layers.length-1].neurons, out);
                    errorsStandard += getStandardDeviation(layers[layers.length-1].neurons);
                    errorsSoftmax += categoricalCrossEntropyLoss(layers[layers.length-1].neurons, out);
                    iterator++;
                    //System.out.println("Target: " + Arrays.toString(out) + "|Real: " + Arrays.toString(layers[2].neurons));
                    //for (int k = 0; k < out.length; k++) {
                        //errors += Math.abs(out[k] - layers[layers.length - 1].neurons[k]);
//                        if((out[k] - layers[layers.length - 1].neurons[k]) * (out[k] - layers[layers.length - 1].neurons[k]) == 1){
//                            System.out.println("Mistake on: " + vocabulary.get(k));
//                        }
//                        if(layers[layers.length - 1].neurons[k] > 0){
//                            System.out.println(vocabulary.get(k) + ": "+layers[layers.length - 1].neurons[k]);
//                        }
                   // }
//                    int ren = (int) (Math.random() * vocabulary.size());
//                    System.out.println("out " + ren + ": " + layers[layers.length - 1].neurons[ren]);
                    //System.out.println("Real: " + layers[2].neurons[vocabulary.indexOf(proffer.get(j))]);
                    //System.out.println("24: " + layers[2].neurons[24]);
                    backPropagation(out);
                    //System.out.println("word index: " + vocabulary.indexOf(proffer.get(j)));
                    //System.out.println("Weights: "+Arrays.deepToString(weights[1]));
                    //System.out.println(proffer.get(j));
                    //System.out.println(vocabulary.get(j) + " 1 layer: "+ Arrays.deepToString(weights[0]));
                    //System.out.println(vocabulary.get(j) + " 2 layer: "+ Arrays.deepToString(weights[1]));
                }
            }
//            double sum = 0;
//            for(double d: layers[2].neurons){
//                sum += d;
//            }
            System.out.println("standard deviation: "+errorsStandard / iterator
                    +", softmax error: " + errorsSoftmax / iterator + ", absolute error: " + errors / iterator);
            //System.out.println("sum: " + sum);
        }
        setEmbeddings(vocabulary);
    }

    private void setEmbeddings(ArrayList<String> vocabulary){
        for (int i = 0; i < layers[1].weights[0].length; i++) {
            for (int j = 0; j < layers[1].weights.length; j++) {
                embeddings[i][j] = layers[1].weights[j][i];
            }
        }
        for (int i = 0; i < embeddings.length; i++) {
            System.out.print(vocabulary.get(i) + ": ");
            for (int j = 0; j < embeddings[i].length; j++) {
                System.out.format("%f, ", embeddings[i][j]);
            }
            System.out.print("\n");
        }

        double[][] matrice = new double[vocabulary.size()][vocabulary.size()];
        for (int i = 0; i < embeddings.length; i++) {
            for (int k = 0; k < embeddings.length; k++) {
                for (int l = 0; l < embeddings[k].length; l++) {
                    matrice[i][k] += Math.abs(embeddings[i][l] - embeddings[k][l]);
                }
            }
        }
        for (int i = 0; i < matrice.length; i++) {
            System.out.print(vocabulary.get(i) + ": ");
            for (int j = 0; j < matrice[i].length; j++) {
                System.out.print(vocabulary.get(j) + " " + matrice[i][j] +", ");
            }
            System.out.print("\n");
        }
    }
}
