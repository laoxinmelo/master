package com.raul.bupt.calculate.clusters.methods.theirs.impl;

import com.raul.bupt.calculate.clusters.methods.AbstractClusterTool;
import com.raul.bupt.calculate.clusters.methods.dataobject.ClusterIndex;
import com.raul.bupt.calculate.clusters.methods.theirs.ClusterToolOldMethod;
import com.raul.bupt.calculate.simi.SimiCalculator;
import com.raul.bupt.calculate.simi.dataobject.RelationWord;
import com.raul.bupt.calculate.simi.dataobject.SameRelationWord;
import com.raul.bupt.calculate.simi.impl.simi.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/3/24.
 */
public class ClusterToolOldMethodImpl extends AbstractClusterTool implements ClusterToolOldMethod {

    private int simiCalculatorIndex; //所选用的相似度计算方法索引



    private static final List<SimiCalculator> simiCalculatorList = new ArrayList<SimiCalculator>();
    static {
        simiCalculatorList.add(new CosineSimiCalculator());
        simiCalculatorList.add(new DiceSimiCalculator());
        simiCalculatorList.add(new DekangSimiCalculator());
        simiCalculatorList.add(new HindlerSimiCalculator());
        simiCalculatorList.add(new JacardSimiCalculator());
    }


    public ClusterToolOldMethodImpl(int simiCalculatorIndex) {
        this.simiCalculatorIndex = simiCalculatorIndex;
    }



    /**
     * 运用各类传统方法，计算两个向量之间的余弦距离
     * @param relationWordList1
     * @param relationWordList2
     * @return
     */
    public double getSimi(List<RelationWord> relationWordList1, List<RelationWord> relationWordList2) {

        List<SameRelationWord> sameRelationWordList = simiCalculatorList.get(simiCalculatorIndex).calculateSameRelationWordNum(relationWordList1, relationWordList2);
        return simiCalculatorList.get(simiCalculatorIndex).calculateSimilarity(relationWordList1, relationWordList2, sameRelationWordList);
    }


    /**
     * 进行一次层次聚类的计算
     * @param wordMap
     */
    private ClusterIndex clusterCalculate(Map<String,List<ArrayList<RelationWord>>> wordMap) {
        if (wordMap == null) {
            throw new NullPointerException("npe");
        }

        Set<String> wordSet = wordMap.keySet();

        double maxValue = Double.MIN_VALUE;
        String word1 = "";
        String word2 = "";

        for (String word : wordSet) {

            for (String otherWord : wordSet) {

                if(word.equals(otherWord)) {
                    continue;
                }


                double simiValue = 0.0;

//                if(word.contains("笔记本") && otherWord.contains("本本")) {
                List<ArrayList<RelationWord>> relationWordListArray1 = wordMap.get(word);
                List<ArrayList<RelationWord>> relationWordListArray2 = wordMap.get(otherWord);

                for(List<RelationWord> relationWordList1:relationWordListArray1) {
                    for(List<RelationWord> relationWordList2:relationWordListArray2) {
                        simiValue += getSimi(relationWordList1,relationWordList2);
                    }
                }

                simiValue = simiValue/(relationWordListArray1.size() * relationWordListArray2.size());
//                System.out.println(simiValue);
//                }

                if(simiValue > maxValue) {
                    word1 = word;
                    word2 = otherWord;
                    maxValue = simiValue;
                }

            }
        }

        ClusterIndex clusterIndex = new ClusterIndex();
        clusterIndex.setElement1(word1);
        clusterIndex.setElement2(word2);
        clusterIndex.setMaxSimi(maxValue);

        return clusterIndex;
    }


    /**
     * 进行层次聚类
     * @param wordMap
     * @param threshold
     */
    public void hierarchicalCluster(Map<String,List<ArrayList<RelationWord>>> wordMap, float threshold) {

        clusterEvaluate(wordMap);

        ClusterIndex clusterIndex = clusterCalculate(wordMap);
        while(clusterIndex.getMaxSimi() >= threshold) {


            String word1 = clusterIndex.getElement1();
            String word2 = clusterIndex.getElement2();

            List<ArrayList<RelationWord>> relationWordListArray1 = wordMap.get(word1);
            List<ArrayList<RelationWord>> relationWordListArray2 = wordMap.get(word2);

            List<ArrayList<RelationWord>> relationWordListArray = new ArrayList<ArrayList<RelationWord>>();
            relationWordListArray.addAll(relationWordListArray1);
            relationWordListArray.addAll(relationWordListArray2);

            /**
             * 将融合为一个簇的另外两个簇删掉
             */
            wordMap.remove(word1);
            wordMap.remove(word2);
            wordMap.put(word1 + indexTag + word2, relationWordListArray);
//            System.out.println(word1 + indexTag + word2);

            clusterEvaluate(wordMap);

            if (wordMap.size() > 1) {
                clusterIndex = clusterCalculate(wordMap);
            }else {
                break;
            }
        }

//        for(String word:wordMap.keySet()) {
//            if(word.split(indexTag).length > 1) {
//                System.out.println(word.replaceAll(indexTag,"\t"));
//            }
//        }

    }
}
