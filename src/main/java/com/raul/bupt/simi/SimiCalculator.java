package com.raul.bupt.simi;

/**
 *
 * 计算词与词之间的相似度
 * Created by Administrator on 2017/3/22.
 */
public interface SimiCalculator {

    /**
     * 计算两个词之间的相似度
     *
     * @param word1
     * @param word2
     * @return
     */
    double calculateSimilarity(String word1,String word2) ;
}
