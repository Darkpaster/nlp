package com.darkpaster.neuralNetworks.architecture;

import com.darkpaster.IO;
import com.darkpaster.Main;
import com.darkpaster.NLP.Preprocessing;
import com.darkpaster.neuralNetworks.NLPNeuralNetwork;
import com.darkpaster.neuralNetworks.NeuralNetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Word2vec extends NLPNeuralNetwork {

    public Word2vec(double learningRate, byte window_size, AF activationFunction, WI weightsInitCase, int... neurNum) {
        super(learningRate, window_size, activationFunction, weightsInitCase, neurNum);
    }

    public void skipGram(int epoch, ArrayList<ArrayList<String>> tokens, ArrayList<String> vocabulary) {
        epoch++;
        for (int i = 1; i < epoch; i++) {
            double errors = 0;
            double errorsStandard = 0;
            double errorsSoftmax = 0;
            int iterator = 0;
            for (ArrayList<String> sentence: tokens) {
                for (int j = 0; j < sentence.size(); j++) {
                    double[] in = new double[layers[0].neurons.length];
                    in[vocabulary.indexOf(sentence.get(j))] = 1;
                    feedForward(in);
                    ArrayList<String> wordTargets = new ArrayList<>();
                    for (int k = -WINDOW_SIZE; k < WINDOW_SIZE; k++) {
                        if (k == 0) continue;
                        try {
                            wordTargets.add(sentence.get(j + k));
                        } catch (Exception ignored) {
                        }
                    }
                    double[] out = new double[layers[layers.length - 1].neurons.length];
                    for (String word : wordTargets) {
                        out[vocabulary.indexOf(word)] = 1;
                    }
                    errors += getAbsoluteError(layers[layers.length-1].neurons, out);
                    errorsStandard += getStandardDeviation(layers[layers.length-1].neurons);
                    errorsSoftmax += categoricalCrossEntropyLoss(layers[layers.length-1].neurons, out);
                    iterator++;
                    backPropagation(out);
                }
            }
            System.out.println("standard deviation: "+errorsStandard / iterator
                    +", softmax error: " + errorsSoftmax / iterator + ", absolute error: " + errors / iterator);
        }
        setEmbeddings(vocabulary);
    }

    public void cbow(int epoch, List<List<String>> tokens, List<String> vocabulary) {
        epoch++;
        for (int i = 1; i < epoch; i++) {
            double errors = 0;
            double errorsStandard = 0;
            double errorsSoftmax = 0;
            int iterator = 0;
            for (List<String> sentence: tokens) {
                for (int j = 0; j < sentence.size(); j++) {
                    ArrayList<String> wordsInput = new ArrayList<>();
                    for (int k = -WINDOW_SIZE; k < WINDOW_SIZE; k++) {
                        if (k == 0) continue;
                        try {
                            wordsInput.add(sentence.get(j + k));
                        } catch (Exception ignored) {
                        }
                    }
                    double[] in = new double[layers[0].neurons.length];
                    for (String word: wordsInput) {
                        in[vocabulary.indexOf(word)] = 1;
                    }
                    feedForward(in);
                    double[] out = new double[layers[layers.length-1].neurons.length];
                    out[vocabulary.indexOf(sentence.get(j))] = 1;
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
        //setEmbeddings(vocabulary);
        //System.out.println("\n\n\n\n");
        setEmbeddings2();
    }

    private void setEmbeddings(List<String> vocabulary){
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

    private void setEmbeddings2(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < layers[0].weights.length; i++) {
            for (int j = 0; j < layers[0].weights[i].length; j++) {
                embeddings[i][j] = layers[0].weights[i][j];
                stringBuilder.append(embeddings[i][j]).append(" ");
                if(embeddings[i][j] > 1){
                    //System.out.println("govno");
                }
            }
            stringBuilder.append("\n");
        }
        IO.write(new File("dataset/embeddings.txt"), String.valueOf(stringBuilder));
//        for (int i = 0; i < embeddings.length; i++) {
//            System.out.print(vocabulary.get(i) + ": ");
//            for (int j = 0; j < embeddings[i].length; j++) {
//                System.out.format("%f, ", embeddings[i][j]);
//            }
//            System.out.print("\n");
//        }
//
//        double[][] matrice = new double[vocabulary.size()][vocabulary.size()];
//        for (int i = 0; i < embeddings.length; i++) {
//            for (int k = 0; k < embeddings.length; k++) {
//                for (int l = 0; l < embeddings[k].length; l++) {
//                    matrice[i][k] += Math.abs(embeddings[i][l] - embeddings[k][l]);
//                }
//            }
//        }
//        for (int i = 0; i < matrice.length; i++) {
//            System.out.print(vocabulary.get(i) + ": ");
//            for (int j = 0; j < matrice[i].length; j++) {
//                System.out.print(vocabulary.get(j) + " " + matrice[i][j] +", ");
//            }
//            System.out.print("\n");
//        }
    }
}
