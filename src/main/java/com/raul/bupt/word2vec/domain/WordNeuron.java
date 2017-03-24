package com.raul.bupt.word2vec.domain;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @reader Rauldini
 *
 */
public class WordNeuron extends Neuron {
    public String name;
    public double[] syn0 = null; //input->hidden，权重
    public List<Neuron> neurons = null;//路径神经元
    public int[] codeArr = null;

    /**
     * @return
     */
    public List<Neuron> makeNeurons()
    {
        if (neurons != null)
        {
            return neurons;
        }
        /**
         * 以下代码都是neurons为null时所执行的语句
         */
        Neuron neuron = this;
        neurons = new LinkedList<Neuron>();
        while ((neuron = neuron.parent) != null)
        {
            neurons.add(neuron);
        }  //构建该词所对应的路径
        /**
         * Collection:是一个集合接口，对其的实现包括Arraylist等
         * Collections:包含有各种有关集合操作的静态多态方法，是一个工具类
         */
        Collections.reverse(neurons);//对排序进行反转
        codeArr = new int[neurons.size()];

        //将之前的WordNeuron的信息添加到codeArr当中
        for (int i = 1; i < neurons.size(); i++)
        {
            codeArr[i - 1] = neurons.get(i).code;
        }
        //将WordNeuron自身添加到codeArr最后
        codeArr[codeArr.length - 1] = this.code;

        return neurons;
    }

    public WordNeuron(String name, int freq, int layerSize)
    {
        this.name = name;
        this.freq = freq;
        this.syn0 = new double[layerSize];
        //Random类，生成随机数 ，初始化权重
        Random random = new Random();
        for (int i = 0; i < syn0.length; i++)
        {
            syn0[i] = (random.nextDouble() - 0.5) / layerSize;//对input->hidden的权值矩阵进行赋值
        }
    }

}