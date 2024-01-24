package com.darkpaster;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    public static final byte WINDOW_SIZE = 4;

    private static final ArrayList<String> vocabulary = new ArrayList<>();
    private static final ArrayList<List<String>> vectors_data = new ArrayList<>();

    //private static final String[] RUSSIAN = read(new File("russian.txt")).toLowerCase().split("\n");
//    private static final String[] FOR_TESTS = read(new File("for_tests.txt"))
//            .replaceAll("[^ЁёА-я \n\\-]", "").replaceAll("\n", " ")
//            .replaceAll(" - ", " ").toLowerCase().replaceAll("\\s+", " ").split(" ");
    private static final String[] SLANG = read(new File("russian_slang.txt"))
            .toLowerCase().replaceAll("[^ЁёА-я\n]", "").split("[\n]");
    private static final String[] STOP_WORDS = read(new File("stop_words.txt"))
            .replaceAll("[^ЁёА-я\n\\-]", "").split("[\n]");

    private static final ArrayList<ArrayList<String>> INPUT = new ArrayList<>();

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
        int i = 0;
        for (String pr : read(new File("input.txt"))
                .replaceAll("[^ЁёА-я \n\\-.]", "").replaceAll("\n", " ")
                .replaceAll(" - ", " ").replaceAll("[.]{2,14}", "").toLowerCase().split("[.]")) {
            ArrayList<String> n = new ArrayList<>();
            Collections.addAll(n, pr.split(" "));
            i += n.size();
            INPUT.add(n);
        }
        System.out.println(i);
        INPUT.removeIf(asd -> asd.size() < 2);;
        for (ArrayList<String> words : INPUT) {
            while (words.contains("")) words.remove("");
            removeStopWords(words);
        }
        //stemming(INPUT);
        for (List<String> proffer : INPUT) {
            for (String s : proffer) {
                if (!vocabulary.contains(s)) {
                    vocabulary.add(s);
                }
            }
        }
        INPUT.removeIf(asd -> asd.size() < 2);
        System.out.println(vocabulary);
        System.out.println(INPUT);
        System.out.println(vocabulary.size());
        System.out.println(INPUT.size());
        Word2vec neuralNetwork = new Word2vec(0.01, NeuralNetwork.AF.SOFTMAX, NeuralNetwork.WI.XAVIER,
                vocabulary.size(), 10, vocabulary.size());
        int epochs = 1000;
        long start = System.nanoTime();
        neuralNetwork.cbow(epochs, INPUT, vocabulary);
        System.out.println((System.nanoTime() - start) / 1000000000d / (double) epochs);
        //16.469277044
        //16.275196715
    }

    private static int[][] bagOfWords(ArrayList<String> unique_words, ArrayList<ArrayList<String>> data) {
        int[][] vectors = new int[unique_words.size()][unique_words.size()];
        int[] most = {0, 0, 0};
        for (List<String> proffer : data) {
            for (int i = 0; i < proffer.size(); i++) {
                for (int j = i - WINDOW_SIZE; j < i + WINDOW_SIZE; j++) {
                    try {
                        if (j == 0) continue;
                        vectors[unique_words.indexOf(proffer.get(i))][unique_words.indexOf(proffer.get(i + j))]++;
                        if (most[2] < vectors[unique_words.indexOf(proffer.get(i))][unique_words.indexOf(proffer.get(i + j))]) {
                            most[0] = unique_words.indexOf(proffer.get(i));
                            most[1] = unique_words.indexOf(proffer.get(i + j));
                            most[2] = vectors[unique_words.indexOf(proffer.get(i))][unique_words.indexOf(proffer.get(i + j))];
                        }
                    } catch (Exception ignored) {

                    }
                }
            }
        }
        System.out.println(Arrays.toString(most));
        System.out.println(unique_words.get(most[0]) + " " + unique_words.get(most[1]));
        return vectors;
    }

    private static void stemming2(ArrayList<String> words) {
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            StringBuilder b = new StringBuilder();
            b.append("Before: ").append(word);
            String w2 = word;
            if (word.endsWith("ая") || word.endsWith("ие") || word.endsWith("ый") || word.endsWith("ые") || word.endsWith("ий")) {
                word = new StringBuilder().append(word).delete(word.length() - 2, word.length()).append("ое").toString();
            }
            if (!w2.equals(word)) {
                b.append(" After: ").append(word);
                System.out.println(b.toString());
            }
            words.set(i, word);
        }
    }

    private static void stemming(ArrayList<ArrayList<String>> words) {
        ArrayList<String> total = new ArrayList<>();
        for (ArrayList<String> proffer: words) {
            total.addAll(proffer);

        }
        for (int i = 0; i < total.size(); i++) {
            for (int j = 0; j < total.size(); j++) {
                if(total.get(i).equals(total.get(j)))continue;
                if (total.get(i).contains(total.get(j)
                        .substring(0, (int) Math.max(3, total.get(j).length() - Math.ceil(total.get(j).length() * 0.2))))) {
                    total.remove(total.get(j));
                }
            }
        }
        for (String word : total) {
            for (ArrayList<String> strings : words) {
                ArrayList<String> similarWords = new ArrayList<>();
                for (int j = 0; j < strings.size(); j++) {
                    if (word.length() < 3 || word.equals(strings.get(j))) continue;
                    if (strings.get(j).contains(word
                            .substring(0, (int) Math.max(3, word.length() - Math.ceil(word.length() * 0.2))))) {
                        similarWords.add(word);
                        similarWords.add(strings.get(j));
                        if(word.startsWith("медв")){
                            System.out.println(strings.get(j));
                            System.out.println(word);
                        }
                        strings.set(j, word);
                        if(word.startsWith("медв")){
                            System.out.println("after: "+strings.get(j));
                        }
//                        boolean z = false;
//                        if(similarWords.contains()){
//
//                        }else{
//                            words.get(i).set(j, similarWords.get(0));
//                        }
                        //System.out.println(similarWords.get(0) +" equals "+ words.get(i).get(j) + ": "+ similarWords.get(0).equals(words.get(i).get(j)));
                    }
                }
                if (similarWords.size() > 1) {
                    System.out.println("Similar words: " + similarWords);
                }
                similarWords.clear();
            }
        }
    }


    private static void removeStopWords(ArrayList<String> words) {
        for (String s : STOP_WORDS) {
            while (words.contains(s)) {
                words.remove(s);
                //System.out.println("Deleted stop-word: " + s);
            }
        }
    }

    private static void checkForSlangWords(String[] words) {
        StringBuilder test = new StringBuilder();
        int i = 0;
        for (String slang : SLANG) {
            //System.out.println(slang);
            //System.out.println("\n\n\n"+slang);
            for (String word : words) {
                //System.out.println(word);
                if (word.equals(slang)) {
                    i++;
                    test.append(slang).append(", ");
                }
            }
        }
        System.out.println(i);
        System.out.println(test.toString());
    }

    private static String read(File file) {
        StringBuilder content = new StringBuilder();
        try {
            FileReader reader = new FileReader(file);
            for (int i = 0; i < file.length(); i++) {
                char c;
                c = (char) reader.read();
                //if(file.getName().equals("input.txt")) System.out.println(c);;
                content.append(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private static void write(File file, String content) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
