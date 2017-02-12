package com.raul.bupt.word_vector;

import com.ansj.vec.Learn;

import java.io.File;
import java.io.IOException;

/**
 * 进行Word2vec模型训练
 * Created by Administrator on 2016/11/16.
 */
public class ModelApply {

    //输入文件读取路径
    private static final String inputPath = "./library/input/";
    //模型保存路径
    private static final String modelPath = "./library/model/";

    //模型学习工具
    private static final Learn learner = new Learn();

    /**
     * 模型训练
     * @param inputName
     * @param modelSaveName
     */
    private static void modelTrain(String inputName,String modelSaveName) {
        try {
            learner.learnFile(new File(inputPath + inputName));
            learner.saveModel(new File(modelPath + modelSaveName));
        }catch (IOException e) {
            System.err.println("Fail to Learn Model...");
        }
    }


    public static void main(String[] args) {
        modelTrain("ecigar.txt","ecigarVector");
    }
}
