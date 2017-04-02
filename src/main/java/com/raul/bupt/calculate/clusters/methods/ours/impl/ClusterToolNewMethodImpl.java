package com.raul.bupt.calculate.clusters.methods.ours.impl;

import com.raul.bupt.calculate.clusters.methods.ours.ClusterToolNewMethod;
import com.raul.bupt.calculate.clusters.methods.dataobject.ClusterIndex;

import java.util.*;

/**
 * Created by Administrator on 2016/11/16.
 */
public class ClusterToolNewMethodImpl implements ClusterToolNewMethod {

    //用来对两个词之间进行分隔
    private static final String indexTag = "_";

    /**
     * 计算两个向量之间的余弦距离
     * @param f1
     * @param f2
     * @return
     */
    public double getSimi(float[] f1, float[] f2) {
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
     * 对聚类效果进行评估
     *
     * @param vectorMap
     * @param allWordVectorCentroid
     * @return
     */
    public double clusterEvaluate(Map<String,float[][]> vectorMap, float[] allWordVectorCentroid) {

        Set<String> wordSet = vectorMap.keySet();

        double numerator = 0.0;
        double denominator = 0.0;

        for(String wordArrayStr:wordSet) {
            float[][] vectorArray = vectorMap.get(wordArrayStr);
            String[] wordArray = wordArrayStr.split(indexTag);
            int wordNum = wordArray.length;

            //计算簇内部各个词之间的相似度
            double inClusterSimilarity = 0.0;
            for(int i=0;i<wordNum;i++) {
                for(int j=0;j<wordNum;j++) {
                    inClusterSimilarity += getSimi(vectorArray[i],vectorArray[j]);
                }
            }
            inClusterSimilarity = inClusterSimilarity/wordNum;
            numerator += inClusterSimilarity;

            //求簇的质心，并计算簇与整体之间的相似度
            float[] wordVectorCentroid = new float[vectorMap.get(wordArrayStr)[0].length];
            for(int i=0;i<wordVectorCentroid.length;i++) {
                for(int j=0;j<wordNum;j++) {
                    wordVectorCentroid[i] += vectorArray[j][i];
                }
                wordVectorCentroid[i] = wordVectorCentroid[i]/wordNum;
            }

            double betweenClusterSimilarity = getSimi(wordVectorCentroid,allWordVectorCentroid) * wordNum;
            betweenClusterSimilarity = betweenClusterSimilarity/Math.sqrt(inClusterSimilarity*wordNum);
            denominator += betweenClusterSimilarity;

        }
        return numerator/denominator;
    }

    /**
     * 将所有词构造成一个簇并求其质心
     *
     * @param originVectorMap
     * @return
     */
    public float[] getAllWordVectorCentroid(Map<String,float[][]> originVectorMap) {

        Set<String> wordSet = originVectorMap.keySet();

        List<float[]> vectorList = new ArrayList<float[]>();
        for(String word:wordSet) {
            float[][] wordVector = originVectorMap.get(word);
            vectorList.add(wordVector[0]);
        }

        float[] vectorArray = new float[vectorList.get(0).length];
        for(int i=0;i<vectorArray.length;i++) {
            for(int j=0;j<vectorList.size();j++) {
                vectorArray[i] += vectorList.get(j)[i];
            }
            vectorArray[i] = vectorArray[i]/vectorList.size();
        }

        return vectorArray;
    }



    /**
     * 进行一次层次聚类的计算
     *
     * @param vectorMap
     */
    private ClusterIndex clusterCalculate(Map<String,float[][]> vectorMap) {
        if(vectorMap == null) {
            throw new NullPointerException("npe");
        }

        Set<String> wordSet = vectorMap.keySet();

        double maxValue = 0;
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

        float[] allWordVectorCentroid = getAllWordVectorCentroid(vectorMap);

        double maxClusterEvaluate = 0.0;

        double evaluateResult = clusterEvaluate(vectorMap,allWordVectorCentroid);
        if(evaluateResult > maxClusterEvaluate) {
            maxClusterEvaluate = evaluateResult;
        }

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

            evaluateResult = clusterEvaluate(vectorMap,allWordVectorCentroid);
            if(evaluateResult > maxClusterEvaluate) {
                maxClusterEvaluate = evaluateResult;

                for(String word:vectorMap.keySet()) {
                    System.out.println(word.replaceAll(indexTag,"\t"));
                }
                System.out.println("________________________________________");
            }


            if (vectorMap.size() > 1) {
                clusterIndex = clusterCalculate(vectorMap);
            }else {
                break;
            }
        }

    }


}
