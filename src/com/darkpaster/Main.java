package com.darkpaster;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final byte WINDOW_SIZE = 4;

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

    private static final ArrayList<List<String>> INPUT = new ArrayList<>();

    public static void main(String[] args) {
        for(String pr: read(new File("input.txt"))
                .replaceAll("[^ЁёА-я \n\\-.]", "").replaceAll("\n", " ")
                .replaceAll(" - ", " ").replaceAll("[.]{2,14}", "").toLowerCase().split("[.]")){
            INPUT.add(Arrays.asList(pr.split(" ")));
        }
        for(List<String> words: INPUT){
            System.out.println(words);
        }
        for(List<String> pref: INPUT) {
            for (String s : pref) {
                if (!vocabulary.contains(s)) {
                    vocabulary.add(s);
                }
            }
        }
        System.out.println(Arrays.deepToString(bagOfWords(vocabulary, INPUT)));
        System.exit(1);
//        vectors_data.addAll(Arrays.asList(read(new File("data_for_vectors.txt"))
//                .replaceAll("[^ЁёА-я \n\\-]", "").replaceAll("\n", " ")
//                .replaceAll(" - ", " ").toLowerCase().replaceAll("\\s+", " ").split(" ")));

//        String[] words_input = INPUT
//
//
//
//        stemming(words_input);

//
//        ArrayList<String> unique = new ArrayList<>();
//        stemming(vectors_data);
//        for (String word : vectors_data) {
//            if (!unique.contains(word)) unique.add(word);
//        }
//        removeStopWords(unique);
//        removeStopWords(vectors_data);
//        word2vec(unique);
//
//
//        for (String word : vocabulary) {
//            System.out.print(word + " ");
//        }
//        System.out.println(vocabulary.size());

    }

    private static int[][] bagOfWords(ArrayList<String> unique_words, ArrayList<List<String>> data) {
        int[][] vectors = new int[unique_words.size()][unique_words.size()];
        int[] most = {0, 0, 0};
        for(List<String> proffer: data){
            for (int i = 0; i < proffer.size(); i++) {
                for (int j = i-WINDOW_SIZE; j < i+WINDOW_SIZE; j++) {
                    try {
                    if (j == 0) continue;
                    vectors[unique_words.indexOf(proffer.get(i))][unique_words.indexOf(proffer.get(i + j))]++;
                    if (most[2] < vectors[unique_words.indexOf(proffer.get(i))][unique_words.indexOf(proffer.get(i + j))]) {
                        most[0] = unique_words.indexOf(proffer.get(i));
                        most[1] = unique_words.indexOf(proffer.get(i + j));
                        most[2] = vectors[unique_words.indexOf(proffer.get(i))][unique_words.indexOf(proffer.get(i + j))];
                    }
                    }catch (Exception ignored){

                    }
                }
            }
        }
        System.out.println(Arrays.toString(most));
        System.out.println(unique_words.get(most[0]) + " " + unique_words.get(most[1]));
        return vectors;
    }

    private static void stemming(ArrayList<String> words){
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            StringBuilder b = new StringBuilder();
            b.append("Before: ").append(word);
            String w2 = word;
            if(word.endsWith("ая") || word.endsWith("ие") || word.endsWith("ый") || word.endsWith("ые") || word.endsWith("ий")){
                word = new StringBuilder().append(word).delete(word.length() - 2, word.length()).append("ое").toString();
            }
            if(!w2.equals(word)){
                b.append(" After: ").append(word);
                System.out.println(b.toString());
            }
            words.set(i, word);
        }
    }

    private static void stemming(String[] words){
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            //if(word.endsWith("ые"))word = word.replaceAll("ые", "ый");
            if(word.endsWith("ая") || word.endsWith("ие") || word.endsWith("ый") || word.endsWith("ые") || word.endsWith("ий")){
                word = new StringBuilder().append(word).delete(word.length() - 2, word.length()).append("ое").toString();
            }
            words[i] = word;
        }
    }

    private static void removeSimilarWords(ArrayList<String> words) {
        for (int k = 0; k < words.size(); k++) {
            if (words.get(k).length() < 3) continue;
            ArrayList<String> similarWords = new ArrayList<>();
            for (String word : words) {
                if (word.contains(words.get(k).substring(0, Math.max(3, words.get(k).length() - words.get(k).length() / 5)))) {
                    similarWords.add(word);
                }

            }
            System.out.println("Similar words: "+similarWords);
            while (similarWords.size() > 1) {
                similarWords.remove((int) (similarWords.size() * Math.random()));
                words.remove(similarWords.get((int) (similarWords.size() * Math.random())));
            }
            similarWords.clear();
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

    private static void checkForSlangWords(String[] words){
        StringBuilder test = new StringBuilder();
        int i = 0;
        for(String slang: SLANG){
            //System.out.println(slang);
            //System.out.println("\n\n\n"+slang);
            for(String word: words){
                //System.out.println(word);
                if(word.equals(slang)){
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
        }catch (IOException e) {
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
