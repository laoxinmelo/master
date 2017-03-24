package com.raul.bupt.calculate.clusters.methods.dataobject;

/**
 * Created by Administrator on 2016/11/16.
 */
public class ClusterIndex {

    private String element1;  //实现聚类的第1个簇
    private String element2;  //实现聚类的第2个簇
    private float maxSimi; //两个簇之间的相似度

    public float getMaxSimi() {
        return maxSimi;
    }

    public void setMaxSimi(float maxSimi) {
        this.maxSimi = maxSimi;
    }

    public String getElement1() {
        return element1;
    }

    public void setElement1(String element1) {
        this.element1 = element1;
    }

    public String getElement2() {
        return element2;
    }

    public void setElement2(String element2) {
        this.element2 = element2;
    }
}
