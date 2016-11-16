package com.cluster.hierarchical;

import java.util.Map;

/**
 * Created by Administrator on 2016/11/16.
 */
public interface ClusterTool {

    /**
     * 计算两个向量之间的余弦距离
     * @param vector1
     * @param vector2
     * @return
     */
    public float getSimi(float[] vector1, float[] vector2);


    /**
     * 进行层次聚类
     * @param map
     * @param threshold
     */
    public void hierarchicalCluster(Map<String,float[][]> map,float threshold);
}
