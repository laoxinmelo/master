package com.raul.bupt.calculate.simi.impl.simi;

import com.raul.bupt.calculate.simi.dataobject.RelationWord;
import com.raul.bupt.calculate.simi.dataobject.SameRelationWord;
import com.raul.bupt.calculate.simi.impl.AbstractSimiCalculator;

import java.util.List;

/**
 * Created by Administrator on 2017/3/23.
 */
public class HindlerSimiCalculator extends AbstractSimiCalculator {

    /**
     * 计算两个词之间的相似度
     *
     * @param relationWordList1
     * @param relationWordList2
     * @param sameRelationWordList
     * @return
     */
    @Override
    public double calculateSimilarity(List<RelationWord> relationWordList1, List<RelationWord> relationWordList2, List<SameRelationWord> sameRelationWordList)  {

        if(sameRelationWordList.size() == 0) {
            return 0;
        }

        double minValue = Double.MAX_VALUE;
        for(SameRelationWord sameRelationWord:sameRelationWordList) {
            double value1 = sameRelationWord.getValue1();
            double value2 = sameRelationWord.getValue2();

            if(value1<minValue) {
                minValue = value1;
            }
            if(value2<minValue) {
                minValue = value2;
            }
        }

        return minValue;
    }
}
