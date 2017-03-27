package com.raul.bupt.apply;

import com.raul.bupt.calculate.clusters.methods.ours.ClusterToolNewMethod;
import com.raul.bupt.calculate.clusters.methods.ours.impl.ClusterToolNewMethodImpl;
import com.raul.bupt.word2vec.Word2VEC;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 对初始特征词进行层次聚类
 * Created by Administrator on 2016/11/16.
 */

public class NewMethodFirstClusterApply {

    //层次聚类工具
    private static final ClusterToolNewMethod clusterTool = new ClusterToolNewMethodImpl();

    //word2vec模型
    private static final Word2VEC vec = new Word2VEC();

    //所需加载模型的保存路径
    private static final String modelPath = "library/model/testReviewVector";

    //初始特征的保存路径
    private static final String attributePath = "result/startAttribute.txt";

    //阈值
    private static final float threshold = Float.valueOf("0.4");

    /**
     * 加载模型
     */
    private static void loadModel() throws IOException {
        vec.loadJavaModel(modelPath);
    }

    /**
     * 获取所有初始特征
     * @return
     */
    private static Map<String,float[][]> getFeatureMap() throws Exception{
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(attributePath)));
        BufferedReader br = new BufferedReader(inputStreamReader);
        String temp = br.readLine();
        Map<String,float[][]> featureMap = new HashMap<String,float[][]>();

        while(temp != null) {
            String word = temp.trim();
            float[] tempVector = vec.getWordVector(word);
            if(tempVector != null) {
                float[][] vector = {tempVector};
                featureMap.put(word,vector);
            }

            temp = br.readLine();
        }
        br.close();

        return featureMap;
    }


    public static void main(String[] args) throws Exception{
        loadModel();
        Map<String,float[][]> featureMap = getFeatureMap();

        clusterTool.hierarchicalCluster(featureMap,threshold);
    }
}
