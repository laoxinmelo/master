package com.raul.bupt.calculate.simi;

import com.raul.bupt.calculate.simi.dataobject.RelationWord;
import com.raul.bupt.calculate.simi.dataobject.SameRelationWord;

import java.util.List;

/**
 *
 * 计算词与词之间的相似度
 * Created by Administrator on 2017/3/22.
 */
public interface SimiCalculator {

    /**
     * 计算两个词之间的相似度
     *
     * @param relationWordList1
     * @param relationWordList2
     * @param sameRelationWordList
     * @return
     */
    double calculateSimilarity(List<RelationWord> relationWordList1,List<RelationWord> relationWordList2, List<SameRelationWord> sameRelationWordList) ;


    /**
     * 计算两个词对应RelationWord列表中是否包含相同的pair
     *
     * @param relationWordList1
     * @param relationWordList2
     * @return
     */
    List<SameRelationWord> calculateSameRelationWordNum(List<RelationWord> relationWordList1, List<RelationWord> relationWordList2);
}
