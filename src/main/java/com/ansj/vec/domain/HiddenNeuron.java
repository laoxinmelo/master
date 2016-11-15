package com.ansj.vec.domain;

public class HiddenNeuron extends Neuron{

    public double[] syn1 ; //hidden->out，权重

    public HiddenNeuron(int layerSize){
        syn1 = new double[layerSize] ;
    }

}
