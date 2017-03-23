package com.raul.bupt.calculate.simi;

import com.raul.bupt.calculate.simi.dataobject.RelationWord;

import java.util.List;

/**
 *
 * 计算与某一个词相关的所有RelationWord集合
 * Created by Administrator on 2017/3/22.
 */
public interface RelatedWordCalculator {


    /**
     * 计算与之相关的所有RelationWord集合
     *
     * @param word
     * @return
     */
    List<RelationWord> calculateRelatedWordList(String word);
}
