package com.raul.bupt.calculate.clusters.methods.ours;

import java.util.Map;

/**
 * Created by Administrator on 2016/11/16.
 */
public interface ClusterToolNewMethod {

    /**
     * 计算两个向量之间的余弦距离
     * @param vector1
     * @param vector2
     * @return
     */
    double getSimi(float[] vector1, float[] vector2);


    /**
     * 进行层次聚类
     * @param map
     * @param threshold
     */
    void hierarchicalCluster(Map<String,float[][]> map,float threshold);
}
