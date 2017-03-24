package com.raul.bupt.calculate.clusters.methods.theirs.impl;

import com.raul.bupt.calculate.clusters.methods.dataobject.ClusterIndex;
import com.raul.bupt.calculate.clusters.methods.theirs.ClusterToolOldMethod;
import com.raul.bupt.calculate.simi.RelatedWordCalculator;
import com.raul.bupt.calculate.simi.SimiCalculator;
import com.raul.bupt.calculate.simi.dataobject.RelationWord;
import com.raul.bupt.calculate.simi.dataobject.SameRelationWord;
import com.raul.bupt.calculate.simi.impl.RelatedWordCalculatorImpl;
import com.raul.bupt.calculate.simi.impl.simi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/3/24.
 */
public class ClusterToolOldMethodImpl implements ClusterToolOldMethod {

    private static final RelatedWordCalculator relatedWordCalculator = new RelatedWordCalculatorImpl();

    private static final int simiCalculatorIndex = 1; //所选用的相似度计算方法索引

    private static final String indexTag = "_";

    private static final List<SimiCalculator> simiCalculatorList = new ArrayList<SimiCalculator>();
    static {
        simiCalculatorList.add(new CosineSimiCalculator());
        simiCalculatorList.add(new DiceSimiCalculator());
        simiCalculatorList.add(new DekangSimiCalculator());
        simiCalculatorList.add(new HindlerSimiCalculator());
        simiCalculatorList.add(new JacardSimiCalculator());
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
    private ClusterIndex clusterCalculate(Map<String,List<RelationWord>[]> wordMap) {
        if (wordMap == null) {
            throw new NullPointerException("npe");
        }

        Set<String> wordSet = wordMap.keySet();

        double maxValue = Double.MIN_VALUE;
        String word1 = "";
        String word2 = "";

        int count = 1;
        for (String word : wordSet) {

            int tempCount = 0;
            for (String otherWord : wordSet) {

                tempCount += 1;
                if (tempCount <= count) {
                    continue;
                }

                double simiValue = 0.0;

                List<RelationWord>[] relationWordListArray1 = wordMap.get(word);
                List<RelationWord>[] relationWordListArray2 = wordMap.get(otherWord);

                for(List<RelationWord> relationWordList1:relationWordListArray1) {
                    for(List<RelationWord> relationWordList2:relationWordListArray2) {
                        simiValue += getSimi(relationWordList1,relationWordList2);
                    }
                }

                simiValue = simiValue/(relationWordListArray1.length+relationWordListArray2.length);
                if(simiValue > maxValue) {
                    word1 = word;
                    word2 = otherWord;
                    maxValue = simiValue;
                }

                count += 1;
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
    public void hierarchicalCluster(Map<String,List<RelationWord>[]> wordMap, float threshold) {

        ClusterIndex clusterIndex = clusterCalculate(wordMap);
        while(clusterIndex.getMaxSimi() >= threshold) {

            /**
             * 将融合为一个簇的另外两个簇删掉
             */
            String word1 = clusterIndex.getElement1();
            String word2 = clusterIndex.getElement2();

            List<RelationWord>[] relationWordListArray1 = wordMap.get(word1);
            List<RelationWord>[] relationWordListArray2 = wordMap.get(word2);

            List<List<RelationWord>> tempRelationWordList = new ArrayList<List<RelationWord>>();
            for(List<RelationWord> relationWordList:relationWordListArray1) {
                tempRelationWordList.add(relationWordList);
            }
            for(List<RelationWord> relationWordList:relationWordListArray2) {
                tempRelationWordList.add(relationWordList);
            }
            List<RelationWord>[] relationWordListArray = (List<RelationWord>[]) tempRelationWordList.toArray();

            wordMap.remove(word1);
            wordMap.remove(word2);
            wordMap.put(word1 + indexTag + word2, relationWordListArray);

            if (wordMap.size() > 1) {
                clusterIndex = clusterCalculate(wordMap);
            }else {
                break;
            }
        }

        for(String word:wordMap.keySet()) {
            System.out.println(word);
        }

    }
}
