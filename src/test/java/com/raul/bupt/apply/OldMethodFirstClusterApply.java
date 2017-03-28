package com.raul.bupt.apply;

import com.raul.bupt.calculate.clusters.methods.theirs.ClusterToolOldMethod;
import com.raul.bupt.calculate.clusters.methods.theirs.impl.ClusterToolOldMethodImpl;
import com.raul.bupt.calculate.simi.RelatedWordCalculator;
import com.raul.bupt.calculate.simi.dataobject.RelationWord;
import com.raul.bupt.calculate.simi.impl.RelatedWordCalculatorImpl;

import java.io.*;
import java.util.*;

/**
 * Created by Administrator on 2017/3/27.
 */
public class OldMethodFirstClusterApply {

    //层次聚类工具
    private static final ClusterToolOldMethod clusterTool = new ClusterToolOldMethodImpl(0);

    private static final RelatedWordCalculator relatedWordCalculator = new RelatedWordCalculatorImpl();

    //初始特征的保存路径
    private static final String attributePath = "result/startAttribute.txt";

    //阈值
    private static final float threshold = Float.valueOf("0.4");

    /**
     * 获取所有初始特征
     * @return
     */
    private static void getFeatureMap() throws Exception{
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(attributePath)));
        BufferedReader br = new BufferedReader(inputStreamReader);
        String temp = br.readLine();
//        Map<String,List<RelationWord>[]> featureMap = new HashMap<String,List<RelationWord>[]>();

        temp = br.readLine();
        while(temp != null) {

            String word = temp.trim();
            List<RelationWord> relationWordList = relatedWordCalculator.calculateRelatedWordList(word);

//            List<List<RelationWord>> tempRelationWordList = new ArrayList<List<RelationWord>>();
//            tempRelationWordList.add(relationWordList);
//            List<RelationWord>[] relationWordListArray = (List<RelationWord>[]) tempRelationWordList.toArray();
//            featureMap.put(word, relationWordListArray);

            temp = br.readLine();
        }
        br.close();
    }


    public static void main(String[] args) throws Exception{

        System.out.println("Start Time:" + new Date());
        getFeatureMap();
        System.out.println("End Time:" + new Date());

//        clusterTool.hierarchicalCluster(featureMap,threshold);
    }

}
