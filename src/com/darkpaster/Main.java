package com.darkpaster;

import com.darkpaster.NLP.Preprocessing;
import com.darkpaster.neuralNetworks.architecture.ClassificationNN;
import com.darkpaster.neuralNetworks.architecture.Word2vec;
import com.darkpaster.neuralNetworks.NeuralNetwork;
import com.darkpaster.neuralNetworks.architecture.myOwnModel;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.DoubleStream;

public class Main {

    private static final String INPUT = "Ты беги, я его задержу...";
    private static final String INPUT2 = "Братья со всех ног помчались в сторону амбара, а минотавр неспешно и ехидно улыбаясь - следовал за ними.\n" +
            "Закрыв за собой массивные ворота здания - братья упали наземь и начали ломать голову, как быть в такой стрессовой ситуации.\n" +
            "Вооружившись вилами - братья покинули амбар и приготовились к предстоящему первому и, возможно, последнему бою в их жизни.\n" +
            "Минотавру успешно удалось воплотить в жизнь задуманное, и братья оба как один отлетели в сторону, мешками повалившись наземь.";
    public static void main(String[] args) {
        nlg();
    }
    private static void nlg(){
        Preprocessing preprocessing = new Preprocessing(INPUT2, Preprocessing.TOKEN_TYPE.EVERY_MARK, false);
        List<String> tokens = preprocessing.tokenize().complete().removeSentenceSplitting();
        List<String> vocabulary = preprocessing.getVOCABULARY();
        myOwnModel myOwnModel = new myOwnModel(0.1, (byte) 5, NeuralNetwork.AF.SIGMOID, NeuralNetwork.WI.DEFAULT,
                vocabulary.size(), vocabulary.size(), vocabulary.size());
        myOwnModel.learn(1000, tokens, vocabulary);
        checkGeneration(vocabulary, myOwnModel, 5, "братья", false);
    }

    private static void nlu(){
        Preprocessing preprocessing = new Preprocessing(INPUT, Preprocessing.TOKEN_TYPE.EVERY_WORD, true);
        List<List<String>> tokens = preprocessing.removeStopWords().stemming().tokenize().complete().getTOKENS();
        List<String> vocabulary = preprocessing.getVOCABULARY();
        System.out.println(vocabulary.size());
        System.out.println("tokens: "+tokens);
        System.out.println("vocab: "+vocabulary);
        Word2vec neuralNetwork = new Word2vec(0.01, (byte) 4, NeuralNetwork.AF.SOFTMAX, NeuralNetwork.WI.XAVIER3,
                vocabulary.size(), vocabulary.size() / 33, vocabulary.size());
        int epochs = 300;
        long start = System.nanoTime();
        neuralNetwork.cbow(epochs, tokens, vocabulary); //попробовать без производной
        System.out.println((System.nanoTime() - start) / 1000000000d / (double) epochs);

        checkGeneration(vocabulary, neuralNetwork, 3, "братья", true);
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

    private static void checkGeneration(List<String> vocabulary, NeuralNetwork neuralNetwork,
                                        int ind, String startWord, boolean values){
        for (int d = 0; d < 1; d++) {
            ArrayList<String> input = new ArrayList<>();
            StringBuilder result = new StringBuilder();
            input.add(startWord);
            result.append(startWord);
            //input.add("роджер");
            double[] firstLayer = new double[vocabulary.size()];
            firstLayer[vocabulary.indexOf(input.get(0))] = 1;
            //firstLayer[vocabulary.indexOf(input.get(1))] = 1;
            double[] answer;
            double maxNum = 0;
            int index = 0;
//            for (int j = 0; j < answer.length; j++) {
//                if (answer[j] > maxNum && !result.toString().contains(vocabulary.get(j))) {
//                    maxNum = answer[j];
//                    index = j;
//                }
//            }
            System.out.print("Input: " + startWord);
            System.out.print("\n");
            String space = " ";
//            if (vocabulary.get(index).length() < 2) {
//                space = "";
//            }
//            if (values) {
//                result.append(space).append(vocabulary.get(index)).append("(").append(maxNum).append(")");
//            } else {
//                result.append(space).append(vocabulary.get(index));
//            }

            //       for (int i = 0; i < ind; i++) {


            answer = neuralNetwork.feedForward(firstLayer);
            maxNum = 0;
//            for (int j = 0; j < answer.length; j++) {
//                if(answer[j] > maxNum && !result.toString().contains(vocabulary.get(j))) {
//                    maxNum = answer[j];
//                    index = j;
//                }
//            }
            double[] sortedAnswer = DoubleStream.of(answer).sorted().toArray();
            for (int j = sortedAnswer.length - 1; j > sortedAnswer.length - 1 - ind; j--) {
                index = 0;
                for (int i = 0; i < answer.length; i++) {
                    if (answer[i] == sortedAnswer[j]) {
                        index = i;
                    }
                }
                //firstLayer[vocabulary.indexOf(vocabulary.get(index))] = 1;
                if (vocabulary.get(index).length() < 2 && (vocabulary.get(index).equals(",") || vocabulary.get(index).equals(":"))) {
                    space = "";
                } else {
                    space = " ";
                }
                if (values) {
                    result.append(space).append(vocabulary.get(index)).append("(").append(maxNum).append(")");
                } else {
                    result.append(space).append(vocabulary.get(index));
                }
            }
            //       }

            System.out.print("Output: " + result.toString());
        }
    }

    private static void clNN(){
        ClassificationNN word2vecNN2 = new ClassificationNN(0.01, NeuralNetwork.AF.SIGMOID,
                NeuralNetwork.WI.DEFAULT, 2500, 1024, 256, 1);
        word2vecNN2.imageClassification(10);
    }

}
