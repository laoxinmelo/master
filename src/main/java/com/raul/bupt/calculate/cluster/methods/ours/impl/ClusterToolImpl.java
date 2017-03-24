package com.raul.bupt.calculate.cluster.methods.ours.impl;

import com.raul.bupt.calculate.cluster.methods.ours.ClusterTool;
import com.raul.bupt.calculate.cluster.methods.ours.dataobject.ClusterIndex;

import java.util.*;

/**
 * Created by Administrator on 2016/11/16.
 */
public class ClusterToolImpl implements ClusterTool {

    //用来对两个词之间进行分隔
    private static final String indexTag = "_";

    /**
     * 计算两个向量之间的余弦距离
     * @param f1
     * @param f2
     * @return
     */
    public float getSimi(float[] f1, float[] f2) {
        float dist = 0;
        float temp1 = 0;
        float temp2 = 0;
        for (int i = 0; i < f1.length; i++)
        {
            dist += f1[i] * f2[i];
            temp1 += f1[i] * f1[i];
            temp2 += f2[i] * f2[i];
        }
        dist = dist/(float)(Math.sqrt(Double.valueOf(temp1))*Math.sqrt(Double.valueOf(temp2)));

        return dist;
    }


    /**
     * 进行一次层次聚类的计算
     * @param vectorMap
     */
    public ClusterIndex clusterCalculate(Map<String,float[][]> vectorMap) {
        if(vectorMap == null) {
            throw new NullPointerException("npe");
        }

        Set<String> wordSet = vectorMap.keySet();

//        String maxIndex = "";
        float maxValue = 0;
        String word1 = "";
        String word2 = "";

        int count = 1;
        for(String word:wordSet) {

            int tempCount = 0;
            for(String otherWord:wordSet) {

                tempCount += 1;
                if(tempCount <= count) {
                    continue;
                }

                float[][] f1 = vectorMap.get(word);
                float[][] f2 = vectorMap.get(otherWord);
                float averageSimi = 0;
                for(int i=0;i<f1.length;i++) {
                    for(int j=0;j<f2.length;j++) {
                        averageSimi += getSimi(f1[i],f2[j]);
                    }
                }
                averageSimi = averageSimi/(f1.length * f2.length);
                if(averageSimi > maxValue) {
                    word1 = word;
                    word2 = otherWord;
                    maxValue = averageSimi;
                }
            }
            count += 1;
        }

        ClusterIndex clusterIndex = new ClusterIndex();
        clusterIndex.setElement1(word1);
        clusterIndex.setElement2(word2);
        clusterIndex.setMaxSimi(maxValue);

        return clusterIndex;
    }


    /**
     * 进行层次聚类
     * @param vectorMap
     */
    public void hierarchicalCluster(Map<String,float[][]> vectorMap,float threshold) {

        ClusterIndex clusterIndex = clusterCalculate(vectorMap);
        while(clusterIndex.getMaxSimi() >= threshold) {

            /**
             * 将融合为一个簇的另外两个簇删掉
             */
            String word1 = clusterIndex.getElement1();
            String word2 = clusterIndex.getElement2();

            float[][] f1 = vectorMap.get(word1);
            float[][] f2 = vectorMap.get(word2);

            float[][] f = new float[f1.length + f2.length][f1[0].length];
            for (int i = 0; i < f1.length; i++) {
                f[i] = f1[i];
            }
            for (int i = 0; i < f2.length; i++) {
                f[f1.length + i] = f2[i];
            }

            vectorMap.remove(word1);
            vectorMap.remove(word2);
            vectorMap.put(word1 + indexTag + word2, f);

            if (vectorMap.size() > 1) {
                clusterIndex = clusterCalculate(vectorMap);
            }else {
                break;
            }
        }

        for(String word:vectorMap.keySet()) {
            System.out.println(word);
        }
    }

//    public static void main(String[] args) {
//
//        ClusterTool clusterTool = new ClusterToolImpl();
//
//        float[][] x1 = {{1,2,3}};
//        float[][] x2 = {{1,2,3}};
//
//        Map<String,float[][]> vectorMap = new HashMap<String, float[][]>();
//        vectorMap.put("x1",x1);
//        vectorMap.put("x2",x2);
//
//        clusterTool.hierarchicalCluster(vectorMap,0);
//    }
}
