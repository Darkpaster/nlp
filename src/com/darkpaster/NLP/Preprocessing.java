package com.darkpaster.NLP;

import com.darkpaster.IO;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

    public Preprocessing(File dataset, TOKEN_TYPE rule, boolean removeChars) {
        source = IO.read(dataset).toLowerCase();
        this.rule = rule;
        STOP_WORDS = IO.read(new File(STOP_WORDS_PATH))
                .replaceAll("[^ЁёА-я\n\\-]", "").split("[\n]");
        SLANG = IO.read(new File(SLANG_WORDS_PATH))
                .toLowerCase().replaceAll("[^ЁёА-я\n]", "").split("[\n]");
        if (removeChars && rule != TOKEN_TYPE.EVERY_MARK) {
            removeChars();
        }
    }

    public Preprocessing(String dataset, TOKEN_TYPE rule, boolean removeChars) {
        source = dataset.toLowerCase();
        this.rule = rule;
        STOP_WORDS = IO.read(new File(STOP_WORDS_PATH))
                .replaceAll("[^ЁёА-я\n\\-]", "").split("[\n]");
        SLANG = IO.read(new File(SLANG_WORDS_PATH))
                .toLowerCase().replaceAll("[^ЁёА-я\n]", "").split("[\n]");
        if (removeChars && rule != TOKEN_TYPE.EVERY_MARK) {
            removeChars();
        }
    }

    private void removeChars() {
        source = source
                .replaceAll("[^Ёёа-я \n\\-.?!]", "").replaceAll("\n", " ")
                .replaceAll(" - ", " ").replaceAll("- ", "").replaceAll("[.]{2,14}", ".");
    }

    public Preprocessing tokenize() { //
        //int i = 0;
        for (String sentence : source.split("[.?!]")) {
            List<String> n = new ArrayList<>();
            if (rule == TOKEN_TYPE.EVERY_WORD) {
                Collections.addAll(n, sentence.split(" "));
            } else if (rule == TOKEN_TYPE.EVERY_CHAR) {
                for (char c : sentence.trim().toCharArray()) {
                    n.add(String.valueOf(c));
                }
            } else if (rule == TOKEN_TYPE.EVERY_MARK) {
                n.addAll(Arrays.asList(sentence.trim()
                        .split("(?=[:.;,)(!?0-9])|\\s")));
            } else {

            }
            //i += n.size();
            if (n.size() > 1) {
                while (n.contains("") || n.contains("null")) {
                    n.remove("");
                    n.remove("null");
                }
                TOKENS.add(n);
            }
        }

        //System.out.println(i);
        TOKENS.removeIf(asd -> asd.size() < 2);

        return this;
    }

    public Preprocessing removeStopWords() {
        for (String word : STOP_WORDS) {
            source = source.replaceAll(" " + word + " ", " ").replaceAll(" " + word + "\\.", ".");
        }
        return this;
    }

    public Preprocessing stemming() {
        String[] st = source.split(" ");
        String[] result = new String[st.length];
        for (String word : st) {
            for (int i = 0; i < st.length; i++) {
                String replacedWord = word.replaceAll("[^ёЁА-я]", "");
                if (st[i].replaceAll("[^ёЁА-я]", "").length() < 3 || replacedWord.length() < 3) continue;
                if (st[i].contains(replacedWord
                        .substring(0, (int) Math.max(3, replacedWord.length() - Math.ceil(replacedWord.length() * 0.2))))) {
                    result[i] = word;
                } else {
                    if (result[i] == null) {
                        result[i] = st[i];
                    }
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        for (String value : result) {
            builder.append(value).append(" ");
        }
        source = builder.toString().trim();
        return this;
    }

    public Preprocessing removeSlangWords() {
        for (String slang : SLANG) {
            source = source.replaceAll(" " + slang + " ", " ").replaceAll(" " + slang + "\\.", ".");
        }
        return this;
    }

    public List<String> removeSentenceSplitting() {
        List<String> tokens = new ArrayList<>();
        for (List<String> sentence : TOKENS) {
            tokens.addAll(sentence);
        }
        return tokens;
    }

    public Preprocessing complete() {
        if (TOKENS.size() == 0) throw new NullPointerException();
        for (List<String> sentence : TOKENS) {
            for (String token : sentence) {
                if (!VOCABULARY.contains(token)) {
                    VOCABULARY.add(token);
                }
            }
        }
        return this;
    }

    public List<List<String>> getTOKENS() {
        return TOKENS;
    }

    public List<String> getVOCABULARY() {
        return VOCABULARY;
    }

    public List<String> usingSplitMethod(String text, int n) {
        String[] results = text.split("(?<=\\G.{" + n + "})");

        return Arrays.asList(results);
    }

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
