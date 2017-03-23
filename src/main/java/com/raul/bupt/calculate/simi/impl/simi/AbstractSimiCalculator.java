package com.raul.bupt.calculate.simi.impl.simi;

import com.raul.bupt.calculate.simi.SimiCalculator;
import com.raul.bupt.calculate.simi.dataobject.RelationWord;
import com.raul.bupt.calculate.simi.dataobject.SameRelationWord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/23.
 */
public abstract class AbstractSimiCalculator implements SimiCalculator {

    /**
     * 计算两个词之间的相似度
     *
     * @param relationWordList1
     * @param relationWordList2
     * @param sameRelationWordList
     * @return
     */
    public double calculateSimilarity(List<RelationWord> relationWordList1,List<RelationWord> relationWordList2, List<RelationWord> sameRelationWordList) {
        return 0;
    }


    /**
     * 计算两个词对应RelationWord列表中是否包含相同的pair
     *
     * @param relationWordList1
     * @param relationWordList2
     * @return
     */
    public List<SameRelationWord> calculateSameRelationWordNum(List<RelationWord> relationWordList1, List<RelationWord> relationWordList2) {

        List<SameRelationWord> relationWordList = new ArrayList<SameRelationWord>();

        if(relationWordList1.size() == 0 || relationWordList2.size() == 0) {
            return null;
        }
        else {
            for (RelationWord relationWord : relationWordList1) {
                for (RelationWord candidateRelationWord : relationWordList2) {

                    if (relationWord.equals(candidateRelationWord)) {
                        relationWordList.add(new SameRelationWord(relationWord.getRelationName(), relationWord.getWord(), relationWord.getValue(),candidateRelationWord.getValue()));
                    }
                }
            }
        }
        return relationWordList;
    }

}
