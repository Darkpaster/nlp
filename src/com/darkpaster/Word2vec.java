package com.darkpaster;

import java.util.ArrayList;
import java.util.Arrays;

public class Word2vec extends NeuralNetwork{
    private final double[][] embeddings;

    Word2vec(double learningRate, AF activationFunction, int... neurNum) {
        super(learningRate, activationFunction, neurNum);
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

                    //System.out.println("Weights: "+Arrays.deepToString(weights[1]));
                    //System.out.println(proffer.get(j));
                    //System.out.println(vocabulary.get(j) + " 1 layer: "+ Arrays.deepToString(weights[0]));
                    //System.out.println(vocabulary.get(j) + " 2 layer: "+ Arrays.deepToString(weights[1]));
                }
                System.out.println("errors: " + errors + "  rights: " + rights);
            }
        }
        setEmbeddings(vocabulary);
    }

    public void cbow(int epoch, ArrayList<ArrayList<String>> words, ArrayList<String> vocabulary) {
        epoch++;
        for (int i = 1; i < epoch; i++) {
            for (ArrayList<String> proffer : words) {
                double rights = 0, errors = 0;
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
                    //System.out.println("Target: " + Arrays.toString(out) + "|Real: " + Arrays.toString(layers[2].neurons));
                    for (int k = 0; k < out.length; k++) {
                        errors += (out[k] - layers[layers.length - 1].neurons[k]) * (out[k] - layers[layers.length - 1].neurons[k]);
                    }
                    backPropagation(out);
                    //System.out.println("Weights: "+Arrays.deepToString(weights[1]));
                    //System.out.println(proffer.get(j));
                    //System.out.println(vocabulary.get(j) + " 1 layer: "+ Arrays.deepToString(weights[0]));
                    //System.out.println(vocabulary.get(j) + " 2 layer: "+ Arrays.deepToString(weights[1]));
                    System.out.println("errors: " + errors + "  rights: " + rights);
                }
            }
        }
        setEmbeddings(vocabulary);
    }

    private void setEmbeddings(ArrayList<String> vocabulary){
        for (int i500 = 0; i500 < layers[1].weights[0].length; i500++) {
            for (int j15 = 0; j15 < layers[1].weights[j15][i500]; j15++) {
                embeddings[i500][j15] = layers[1].weights[j15][i500];
                //System.arraycopy(layers[1].weights[o][i], 0, embeddings[i], 0, layers[1].weights[i].length);
            }
        }
        for (int i = 0; i < embeddings.length; i++) {
            System.out.println(vocabulary.get(i) + ": " + Arrays.toString(embeddings[i]));
        }
        double[][] matrice = new double[embeddings.length][vocabulary.size()];
        for (int i = 0; i < embeddings.length; i++) {
            for (int k = 0; k < embeddings.length; k++) {
                for (int l = 0; l < embeddings[k].length; l++) {
                    matrice[i][k] += Math.pow(embeddings[i][l] - embeddings[k][l], 2);
                }
            }
        }
        for (int i = 0; i < matrice.length; i++) {
            System.out.print(vocabulary.get(i) + ": ");
            for (int j = 0; j < matrice[i].length; j++) {
                System.out.print(vocabulary.get(j) + " " + matrice[i][j] +", ");
            }
            System.out.println("\n");
        }
    }
}
