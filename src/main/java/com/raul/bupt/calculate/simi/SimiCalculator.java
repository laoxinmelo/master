package com.raul.bupt.calculate.simi;

import com.raul.bupt.calculate.simi.dataobject.RelationWord;

import javax.management.relation.Relation;
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
     * @return
     */
    double calculateSimilarity(List<RelationWord> relationWordList1,List<RelationWord> relationWordList2) ;


    /**
     * 计算两个词对应RelationWord列表中是否包含相同的pair
     *
     * @param relationWordList1
     * @param relationWordList2
     * @return
     */
    List<RelationWord> calculateSameRelationWordNum(List<RelationWord> relationWordList1, List<RelationWord> relationWordList2);
}
