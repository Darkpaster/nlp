package com.darkpaster.neuralNetworks.architecture;

import com.darkpaster.neuralNetworks.NeuralNetwork;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ClassificationNN extends NeuralNetwork {

    private double[][] input;
    private boolean[] bears;

    public ClassificationNN(double learningRate, AF activationFunction, WI weightsInitCase, int... neurNum) {
        super(learningRate, activationFunction, weightsInitCase, neurNum);
    }

    public void imageClassification(int epoch) {
        epoch++;
        int samples = 276;
        BufferedImage[] images = new BufferedImage[samples];
        bears = new boolean[samples];
        File[] imagesFiles = new File("animals").listFiles();
        for (int i = 0; i < samples; i++) {
            try {
                assert imagesFiles != null;
                images[i] = ImageIO.read(imagesFiles[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bears[i] = imagesFiles[i].getName().startsWith("bear");
        }
        for (int j = 0; j < images.length; j++) {
            images[j] = resizeImage(images[j], (int) Math.sqrt(layers[0].size), (int) Math.sqrt(layers[0].size));
        }
        //System.out.println(images[2].getHeight());
        input = new double[samples][];
        for (int i = 0; i < samples; i++) {
            input[i] = new double[images[i].getHeight() * images[i].getWidth()];
            for (int y = 0; y < images[i].getHeight(); y++) {
                for (int x = 0; x < images[i].getWidth(); x++) {
                    input[i][y * images[i].getWidth() + x] = (images[i].getRGB(x, y) & 0xff) / (255.0 * 3);
                }
            }
        }
        for (int k = 1; k < epoch; k++) {
            double rights = 0, errors = 0;
            for (int i = 0; i < 100; i++) {
                int index = (int) (Math.random() * samples);
                double[] outputs = feedForward(input[index]);
                double target = bears[index] ? 1 : 0;
                errors += (target - outputs[0]) * (target - outputs[0]);
                if (target - outputs[0] < 0.5 && target - outputs[0] > -0.5) {
                    rights++;
                }
                backPropagation(new double[]{target});
                // System.out.println("Target: "+target+"|Real: "+neurons[neurons.length-1][0]);
            }
            System.out.println("Epoch: " + k + "  errors: " + errors + "  rights: " + rights);
        }
    }


    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }
}
