package com.raul.bupt.calculate.clusters.methods.theirs;

import com.raul.bupt.calculate.simi.dataobject.RelationWord;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/24.
 */
public interface ClusterTool {

    /**
     * 计算两个向量之间的余弦距离
     * @param relationWordList1
     * @param relationWordList2
     * @return
     */
    public float getSimi(List<RelationWord> relationWordList1, List<RelationWord> relationWordList2);


    /**
     * 进行层次聚类
     * @param map
     * @param threshold
     */
    public void hierarchicalCluster(Map<String,List<RelationWord>> map, float threshold);

}
