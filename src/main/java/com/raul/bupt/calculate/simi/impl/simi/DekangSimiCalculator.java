package com.raul.bupt.calculate.simi.impl.simi;

import com.raul.bupt.calculate.simi.dataobject.RelationWord;
import com.raul.bupt.calculate.simi.dataobject.SameRelationWord;
import com.raul.bupt.calculate.simi.impl.AbstractSimiCalculator;

import java.util.List;

/**
 * Created by Administrator on 2017/3/23.
 */
public class DekangSimiCalculator extends AbstractSimiCalculator {

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

        double numerator = 0.0;
        double denominator = 0.0;

        for(SameRelationWord sameRelationWord:sameRelationWordList) {
            numerator += sameRelationWord.getValue1() + sameRelationWord.getValue2();
        }

        for(RelationWord relationWord:relationWordList1) {
            denominator += relationWord.getValue();
        }
        for(RelationWord relationWord:relationWordList2) {
            denominator += relationWord.getValue();
        }

        return numerator/denominator;
    }

}
