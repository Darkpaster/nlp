package com.darkpaster.neuralNetworks;

public abstract class NLPNeuralNetwork extends NeuralNetwork{
    protected final double[][] embeddings;
    public final byte WINDOW_SIZE;

    public NLPNeuralNetwork(double learningRate, byte window_size, AF activationFunction, WI weightsInitCase, int... neurNum) {
        super(learningRate, activationFunction, weightsInitCase, neurNum);
        WINDOW_SIZE = window_size;
        embeddings = new double[layers[layers.length-1].neurons.length][layers[1].weights.length];
    }
}
