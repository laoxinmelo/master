package com.raul.bupt.calculate.clusters.methods.theirs.impl;

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

/**
 * Created by Administrator on 2017/3/24.
 */
public class ClusterToolOldMethodImpl implements ClusterToolOldMethod {


    private static final RelatedWordCalculator relatedWordCalculator = new RelatedWordCalculatorImpl();

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
    public List<Double> getSimi(List<RelationWord> relationWordList1, List<RelationWord> relationWordList2) {

        List<Double> simiResultList = new ArrayList<Double>();
        for(SimiCalculator simiCalculator:simiCalculatorList) {

            List<SameRelationWord> sameRelationWordList = simiCalculator.calculateSameRelationWordNum(relationWordList1, relationWordList2);
            simiResultList.add(simiCalculator.calculateSimilarity(relationWordList1, relationWordList2, sameRelationWordList));
        }

        return simiResultList;
    }


    /**
     * 进行层次聚类
     * @param map
     * @param threshold
     */
    public void hierarchicalCluster(Map<String,List<RelationWord>> map, float threshold) {
        return ;
    }
}
