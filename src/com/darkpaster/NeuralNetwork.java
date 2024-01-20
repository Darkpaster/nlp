package com.darkpaster;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class NeuralNetwork {
    enum AF {
        SIGMOID, BINARY, SOFTMAX, ReLU
    }

    protected byte AFU = 0;

    protected final double LR;

    protected Layer[] layers;


    NeuralNetwork(double learningRate, AF activationFunction, int... neurNum) {
        this.LR = learningRate;
        setAF(activationFunction);
        init(neurNum);
    }

    private void init(int[] neurNum) {
        layers = new Layer[neurNum.length];
        for (int i = 0; i < neurNum.length; i++) {
            int nextSize = 0;
            if (i < neurNum.length - 1) nextSize = neurNum[i + 1];
            layers[i] = new Layer(neurNum[i], nextSize);
            for (int j = 0; j < neurNum[i]; j++) {
                layers[i].biases[j] = Math.random() * 2.0 - 1.0;
                for (int k = 0; k < nextSize; k++) {
                    layers[i].weights[j][k] = Math.random() * 2.0 - 1.0;
                }
            }
        }
    }


//
//    void learnRandom(int epoch) {
//        epoch++;
//        int randomTarget = (int) (Math.random() * neurons[neurons.length - 1].length);
//        int randomInput = (int) (Math.random() * neurons[0].length);
//        for (int k = 1; k < epoch; k++) {
//            double rights = 0, errors = 0;
//            for (int i = 0; i < 100; i++) {
//                Arrays.fill(neurons[0], bRndom());
//                feedForward();
//                double[] targets = new double[neurons[neurons.length - 1].length];
//                targets[randomTarget] = neurons[0][randomInput] == 1 ? 1 : 0;
//                backPropagation(targets);
//                for (int j = 0; j < targets.length; j++) {
//                    errors += (targets[j] - neurons[neurons.length - 1][j]) * (targets[j] - neurons[neurons.length - 1][j]);
//                }
//                if (targets[randomTarget] - neurons[neurons.length - 1][randomTarget] < 0.5 && targets[randomTarget] - neurons[neurons.length - 1][randomTarget] > -0.5) {
//                    rights++;
//                }
//            }
//            //neurons[neurons.length-1][0] = 0;
//            //System.out.println("Weights: "+weights[0]+", "+weights[1]);
//            //System.out.println("Target: "+target+"|Real: "+neurons[2][0]);
//            System.out.println("Epoch: " + k + "  errors: " + errors + "  rights: " + rights);
//        }
//    }

    public double[][] input;
    public boolean[] bears;

    public void learn(int epoch) {
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

//    private void feedForward() {
//        //System.out.println("before: "+neurons[2][0]);
//        for (int i = 1; i < neurons.length; i++) {
//            double[] prev = neurons[i - 1];
//            for (int j = 0; j < neurons[i].length; j++) {
//                neurons[i][j] = 0;
//                for (int p = 0; p < prev.length; p++) {
//                    neurons[i][j] += prev[p] * weights[i - 1][p][j];
//                }
//                if (AFU == 3) {
//                    if (i == neurons.length - 1) {
//                        neurons[i][j] = AF(neurons[i][j]);
//                    }
//                } else {
//                    neurons[i][j] = AF(neurons[i][j]);
//                }
//            }
//        }
//        //System.out.println("after: "+neurons[2][0]);
//    }

//    private void backPropagation(double[] targets) {
//        double[][] err = neurons;
//        for (int i = 0; i < err[err.length - 1].length; i++) {
//            err[err.length - 1][i] = targets[i] - neurons[neurons.length - 1][i];
//        }
//        for (int i = neurons.length - 1; i > 1; i--) {
//            for (int j = 0; j < neurons[i].length; j++) {
//                for (int p = 0; p < neurons[i - 1].length; p++) {
//                    err[i - 1][p] += weights[i - 1][p][j] * err[i][j];
//                }
//            }
//        }
//        for (int i = neurons.length - 1; i > 1; i--) {
//            for (int j = 0; j < neurons[i].length; j++) {
//                for (int p = 0; p < neurons[i - 1].length; p++) {
//                    weights[i - 1][p][j] += err[i - 1][p] * LR * neurons[i - 1][p];
//                }
//            }
//        }
//    }

    protected void backPropagation(double[] targets) {
        double[] errors = new double[layers[layers.length - 1].size];
        //int tar = 0;
        for (int i = 0; i < layers[layers.length - 1].size; i++) {
            errors[i] = targets[i] - layers[layers.length - 1].neurons[i];
//            if(targets[i] != 0){
//                System.out.println(i +" = "+ targets[i] + ", " + layers[layers.length - 1].neurons[i]);
//                tar = i;
//            }
        }
        for (int k = layers.length - 2; k >= 0; k--) {
            Layer l = layers[k];
            Layer l1 = layers[k + 1];
            double[] errorsNext = new double[l.size];
            double[] gradients = new double[l1.size];
            for (int i = 0; i < l1.size; i++) {
                if(AFU != 3){
                    gradients[i] = errors[i] * derivative(layers[k + 1].neurons[i], layers[k + 1].neurons);
                }else{
                    if(k == layers.length - 2){

                        gradients[i] = errors[i] * derivative(layers[k + 1].neurons[i], layers[k + 1].neurons);
//                    if(i == tar){
//                        System.out.println("neuron: "+i+" ("+layers[k + 1].neurons[i]+"), gradient (error * derivative * LR): " + gradients[i]*LR);
//                    }
                    }else{
                        gradients[i] = errors[i] * layers[k + 1].neurons[i];
                    }
                }
                gradients[i] *= LR;
            }
            double[][] deltas = new double[l1.size][l.size];
            int check = 0;
            for (int i = 0; i < l1.size; i++) {
                for (int j = 0; j < l.size; j++) {
                    deltas[i][j] = gradients[i] * l.neurons[j];
//                    if(k == layers.length - 2 && i == tar){
//                        System.out.println("delta " + j+" (gradient * each neuron of the next layer): "+deltas[i][check]);
//                    }
                }

            }
            for (int i = 0; i < l.size; i++) {
                errorsNext[i] = 0;
                for (int j = 0; j < l1.size; j++) {
                    errorsNext[i] += l.weights[i][j] * errors[j];
                }
            }
            errors = new double[l.size];
            System.arraycopy(errorsNext, 0, errors, 0, l.size);
            double[][] weightsNew = new double[l.weights.length][l.weights[0].length]; //why new?
            for (int i = 0; i < l1.size; i++) {
                for (int j = 0; j < l.size; j++) {
                    weightsNew[j][i] = l.weights[j][i] + deltas[i][j];
//                    if(k == layers.length - 2){
//                        System.out.println("weight "+j+" (weight / each deltas of the next layer) = "+weightsNew[j][i]);
//                    }
                }
            }
            l.weights = weightsNew;
            for (int i = 0; i < l1.size; i++) {
//                if(i == tar && k == layers.length - 2){
//                    System.out.println(l1.biases[i]+" biase (biase + gradient) = " + l1.biases[i] + gradients[i]);
//                }
                l1.biases[i] += gradients[i];
            }
        }
    }

    public double[] feedForward(double[] inputs) {
        System.arraycopy(inputs, 0, layers[0].neurons, 0, inputs.length);
        for (int i = 1; i < layers.length; i++) {
            Layer l = layers[i - 1];
            Layer l1 = layers[i];
            for (int j = 0; j < l1.size; j++) {
                l1.neurons[j] = 0;
                for (int k = 0; k < l.size; k++) {
                    l1.neurons[j] += l.neurons[k] * l.weights[k][j];
                }
                l1.neurons[j] += l1.biases[j];
                if (AFU == 3) {
                    if (i == layers.length - 1) {
                        //System.out.println("step 1: "+l1.neurons[j]);
                        l1.neurons[j] = softmax(l1.neurons[j], l1.neurons);
                        //System.out.println("step 3: "+ l1.neurons[j]);
                    }
                } else {
                    l1.neurons[j] = AF(l1.neurons[j], l1.neurons);
                }
            }
        }
        return layers[layers.length - 1].neurons;
    }

    private void setAF(AF af) {
        switch (af) {
            case BINARY:
                AFU = 1;
                break;
            case SIGMOID:
                AFU = 2;
                break;
            case SOFTMAX:
                AFU = 3;
                break;
            case ReLU:
                AFU = 4;
        }
    }

    protected double AF(double neuron, double[] neurons) {
        switch (AFU) {
            case 1:
                return binary(neuron);
            case 2:
                return sigmoid(neuron);
            case 3:
                return softmax(neuron, neurons);
            case 4:
                return reLU(neuron);
        }
        return neuron;
    }

    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    private double softmax(double target, double[] vectors) {
        double sum = 0;
        for (double vector : vectors) {
            sum += Math.exp(vector);
        }
        //System.out.println("Step 2: "+Math.exp(target) +" / "+sum);
        return Math.exp(target) / sum;
    }

    private double binary(double x) {
        return x >= 1 ? 1 : 0;
    }

    private double reLU(double x) {
        return Math.max(0, x);
    }

    private double bRndom() {
        return Math.random() > 0.5 ? 1 : 0;
    }

    protected double derivative(double x, double[] n) {
        switch (AFU) {
            case 1:
                return x;//binary
            case 2:
                return x * (1 - x);//sigmoid
            case 3:
                return softmax(x, n) * (1 - softmax(x, n)); //softmax
            case 4:
                return x <= 0 ? 0 : 1;
        }
        return x;
    }
}
