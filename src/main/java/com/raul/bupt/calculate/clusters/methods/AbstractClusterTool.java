package com.raul.bupt.calculate.clusters.methods;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/4/5.
 */
public abstract class AbstractClusterTool {

    protected static final String indexTag = "_";

    protected static final String goldClusteringSavePath = "result/goldCluster.txt";

    protected static final List<String[]> goldClusterList = getGoldClustering();

    /**
     * 获取标准聚类数据集
     *
     * @return
     */
    private static List<String[]> getGoldClustering() {
        List<String[]> goldClusterList = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(goldClusteringSavePath))));
            goldClusterList = new ArrayList<String[]>();

            String temp = bufferedReader.readLine();
            while(temp != null) {
                String[] wordArray = temp.split(indexTag);
                if(wordArray.length>1) {
                    goldClusterList.add(wordArray);
                }
                temp = bufferedReader.readLine();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            return goldClusterList;
        }
    }


    /**
     * 对聚类效果进行评估
     *
     * @param vectorMap
     */
    protected void clusterEvaluate(Map vectorMap) {

        Set<String> wordSet = vectorMap.keySet();
        int candidateCluster = vectorMap.keySet().size();

        int goldClusterWordNum = 0;
        for(String[] goldWordArray:goldClusterList) {
            goldClusterWordNum += goldWordArray.length;
        }


        double MicroF = 0;
        double MacroF = 0;

        for(String[] goldWordArray:goldClusterList) {
            int goldWordNum = goldWordArray.length;
            double F = 0;

            for(String wordArrayStr:wordSet) {

                String[] candidateWordArray = wordArrayStr.split(indexTag);
                int candidateWordNum = candidateWordArray.length;
                int candidateSameWordNum = 0;

                for(String goldWord:goldWordArray) {
                    for(String word:candidateWordArray) {
                        if(goldWord.equals(word)) {
                            candidateSameWordNum += 1;
                        }
                    }
                }

                double precision = Double.valueOf(candidateSameWordNum)/Double.valueOf(candidateWordNum);
                double recall = Double.valueOf(candidateSameWordNum)/Double.valueOf(goldWordNum);
                double tempF = precision*recall/(precision + recall);

                if(tempF>F) {
                    F = tempF;
                }

            }

            MicroF += Double.valueOf(goldWordNum)/Double.valueOf(goldClusterWordNum) * F;
            MacroF += F;

        }

        MacroF = MacroF/goldClusterList.size();
        System.out.println(candidateCluster + "\t" + MicroF + "\t" + MacroF);
    }

}
