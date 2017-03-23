package com.raul.bupt.calculate.simi.impl.simi;

import com.raul.bupt.calculate.simi.SimiCalculator;
import com.raul.bupt.calculate.simi.dataobject.RelationWord;

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
     * @return
     */
    public double calculateSimilarity(List<RelationWord> relationWordList1,List<RelationWord> relationWordList2) {
        return 0;
    }


    /**
     * 计算两个词对应RelationWord列表中是否包含相同的pair
     *
     * @param relationWordList1
     * @param relationWordList2
     * @return
     */
    public List<RelationWord> calculateSameRelationWordNum(List<RelationWord> relationWordList1, List<RelationWord> relationWordList2) {


        if(relationWordList1.size() == 0 || relationWordList2.size() == 0) {
            return null;
        }


        List<RelationWord> relationWordList = new ArrayList<RelationWord>();
        for(RelationWord relationWord:relationWordList1) {
            for(RelationWord candidateRelationWord:relationWordList2) {

                if(relationWord.equals(candidateRelationWord)) {
                    relationWordList.add(new RelationWord(relationWord.getRelationName(),relationWord.getWord(),0));
                }
            }
        }
        return relationWordList;
    }

}
