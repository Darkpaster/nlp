package com.darkpaster.NLP;

import com.darkpaster.IO;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Preprocessing {
    public enum TOKEN_TYPE {
        EVERY_CHAR, EVERY_TWO_CHARS, EVERY_WORD, EVERY_MARK
    }
    private final String STOP_WORDS_PATH = "dataset/stop_words.txt";
    private final String PROFANITY_WORDS_PATH = "dataset/profanity.txt";
    private final String SLANG_WORDS_PATH = "dataset/russian_slang.txt";
    private final String[] STOP_WORDS;
    private final String[] SLANG;
    private String source;
    private final List<List<String>> TOKENS = new ArrayList<>();
    private final List<String> VOCABULARY = new ArrayList<>();
    private final TOKEN_TYPE rule;
    private final boolean sentence;

    public Preprocessing(File dataset, TOKEN_TYPE rule, boolean sentence){
        source = IO.read(dataset);
        this.rule = rule;
        STOP_WORDS = IO.read(new File(STOP_WORDS_PATH))
                .replaceAll("[^ЁёА-я\n\\-]", "").split("[\n]");
        SLANG = IO.read(new File(SLANG_WORDS_PATH))
                .toLowerCase().replaceAll("[^ЁёА-я\n]", "").split("[\n]");
        this.sentence = sentence;
    }
    public Preprocessing(String dataset, TOKEN_TYPE rule, boolean sentence){
        source = dataset;
        this.rule = rule;
        STOP_WORDS = IO.read(new File(STOP_WORDS_PATH))
                .replaceAll("[^ЁёА-я\n\\-]", "").split("[\n]");
        SLANG = IO.read(new File(SLANG_WORDS_PATH))
                .toLowerCase().replaceAll("[^ЁёА-я\n]", "").split("[\n]");
        this.sentence = sentence;
    }

    public Preprocessing tokenize(){ //
        //int i = 0;
        if(TOKEN_TYPE == TOKEN_TYPE.){}
        for (String sentence: IO.read(new File("input.txt"))
                .replaceAll("[^ЁёА-я \n\\-.]", "").replaceAll("\n", " ")
                .replaceAll(" - ", " ").replaceAll("[.]{2,14}", "").toLowerCase().split("[.]")) {
            ArrayList<String> n = new ArrayList<>();
            Collections.addAll(n, sentence.split(" "));
            //i += n.size();
            TOKENS.add(n);
        }
        //System.out.println(i);
        TOKENS.removeIf(asd -> asd.size() < 2);

        return this;
    }

    public Preprocessing removeStopWords(ArrayList<String> words) {
        for (String word: STOP_WORDS) {
            source = source.replaceAll(word, "");
        }
        return this;
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
        for (ArrayList<String> sentence: words) {
            total.addAll(sentence);

        }
        for (String word: total) {
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

    public Preprocessing removeSlangWords() {
        for (String slang: SLANG) {
            source = source.replaceAll(slang, "");
        }
        return this;
    }

    public Preprocessing complete(){
        if(TOKENS.size() == 0)throw new NullPointerException();
        for(List<String> sentence: TOKENS){
            for(String token: sentence){
                if(!VOCABULARY.contains(token)){
                    VOCABULARY.add(token);
                }
            }
        }
        return this;
    }
    public List<String> getTOKENS(){
        return sentence ? TOKENS : ; //двухмерный или одномерный
    }
    public ArrayList<String> getVOCABULARY(){return VOCABULARY;}

//    private int[][] bagOfWords(ArrayList<String> unique_words, ArrayList<ArrayList<String>> data) {
//        int[][] vectors = new int[unique_words.size()][unique_words.size()];
//        int[] most = {0, 0, 0};
//        for (List<String> proffer : data) {
//            for (int i = 0; i < proffer.size(); i++) {
//                for (int j = i - WINDOW_SIZE; j < i + WINDOW_SIZE; j++) {
//                    try {
//                        if (j == 0) continue;
//                        vectors[unique_words.indexOf(proffer.get(i))][unique_words.indexOf(proffer.get(i + j))]++;
//                        if (most[2] < vectors[unique_words.indexOf(proffer.get(i))][unique_words.indexOf(proffer.get(i + j))]) {
//                            most[0] = unique_words.indexOf(proffer.get(i));
//                            most[1] = unique_words.indexOf(proffer.get(i + j));
//                            most[2] = vectors[unique_words.indexOf(proffer.get(i))][unique_words.indexOf(proffer.get(i + j))];
//                        }
//                    } catch (Exception ignored) {
//
//                    }
//                }
//            }
//        }
//        System.out.println(Arrays.toString(most));
//        System.out.println(unique_words.get(most[0]) + " " + unique_words.get(most[1]));
//        return vectors;
//    }
}
