package com.darkpaster;

import com.darkpaster.neuralNetworks.architecture.Word2vec;
import com.darkpaster.neuralNetworks.NeuralNetwork;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
//    private static final String[] FOR_TESTS = read(new File("for_tests.txt"))
//            .replaceAll("[^ЁёА-я \n\\-]", "").replaceAll("\n", " ")
//            .replaceAll(" - ", " ").toLowerCase().replaceAll("\\s+", " ").split(" ");

    public static void main(String[] args) {
//        NeuralNetwork word2vecNN2 = new NeuralNetwork(0.01, NeuralNetwork.AF.SIGMOID,
//                2500, 1024, 256, 1);
//        double[] test = {1, 1, 1};
//        double[] oldTest = {1, 1, 1};
//        for (int i = 0; i < test.length; i++) {
//            test[i] = word2vecNN2.softmax(oldTest[i], oldTest);
//        }
//        System.out.println(Arrays.toString(test));
//        word2vecNN2.learn(10);
       nlp();
    }

    private static void nlp() {
//        checkForSlangWords(read(new File("input.txt"))
//                .replaceAll("[^ЁёА-я \n\\-]", "").replaceAll("\n", " ")
//                .replaceAll(" - ", " ").toLowerCase().split(" "));
//        System.exit(1);


        //stemming(INPUT);

        INPUT.removeIf(asd -> asd.size() < 2);
        System.out.println(vocabulary);
        //System.out.println(INPUT);
        System.out.println(vocabulary.size());
        System.out.println(INPUT.size());
        Word2vec neuralNetwork = new Word2vec(0.0001, NeuralNetwork.AF.SOFTMAX, NeuralNetwork.WI.POSITIVE_DEFAULT,
                vocabulary.size(), vocabulary.size() / 33, vocabulary.size());
        int epochs = 1000;
        long start = System.nanoTime();
        neuralNetwork.cbow(epochs, INPUT, vocabulary); //попробовать без производной
        System.out.println((System.nanoTime() - start) / 1000000000d / (double) epochs);
        ArrayList<String> input = new ArrayList<>();
        input.add("братья");
        //input.add("роджер");
        double[] firstLayer = new double[vocabulary.size()];
        firstLayer[vocabulary.indexOf(input.get(0))] = 1;
        //firstLayer[vocabulary.indexOf(input.get(1))] = 1;
        double[] answer = neuralNetwork.feedForward(firstLayer);
        double maxNum = 0;
        int index = 0;
        for (int j = 0; j < answer.length; j++) {
            if(answer[j] > maxNum) {
                maxNum = answer[j];
                index = j;
            }
        }
        StringBuilder result = new StringBuilder();
        System.out.print("Input: Братья");
        System.out.print("\n");
        result.append(vocabulary.get(index));
        input.add(vocabulary.get(index));
        firstLayer[vocabulary.indexOf(input.get(1))] = 1;
        answer =  neuralNetwork.feedForward(firstLayer);
        maxNum = 0;
        index = 0;
        for (int j = 0; j < answer.length; j++) {
            if(answer[j] > maxNum) {
                maxNum = answer[j];
                index = j;
            }
        }
        result.append(" ").append(vocabulary.get(index));

        System.out.print("Output: Братья "+ result.toString() + ".");

        //~14
        //16.275196715
        //293 and NaN default
        //from 2.19 to 2.66 defaultPositive, with biases: 1.65
        //3.23 xavier
        //3.17 xavier2
        //3.10 xavier3
        //3.12 xavier4
        //3.15 xavier5
    }

}
