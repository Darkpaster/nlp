package com.darkpaster;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class IO {
    public static String read(File file) {
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

    public static void write(File file, String content) {
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

    private static double[][] getEmbeddings(String path, String splitRegex1, String splitRegex2){
        String[] a = read(new File(path)).split(splitRegex1);
        String[][] emb = new String[a.length][];
        double[][] embD = new double[a.length][];
        for (int i = 0; i < a.length; i++) {
            emb[i] = a[i].split(splitRegex2);
            for (int j = 0; j < emb[i].length; j++) {
                embD[i][j] = Double.parseDouble(emb[i][j]);
            }
        }
        return embD;
    }
}
