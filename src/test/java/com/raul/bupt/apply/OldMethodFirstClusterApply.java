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



//    private static final RelatedWordCalculator relatedWordCalculator = new RelatedWordCalculatorImpl();

    //初始特征的保存路径
    private static final String attributePath = "result/startAttribute.txt";

    //初始特征对应RelationWord的保存路径
    private static final String relationWordSavePath = "result/relationWord/";

    //阈值
    private static final float threshold = Float.MIN_VALUE;

    /**
     * 计算所有候选特征词的RelationWord并保存
     * 获取所有初始特征
     * @return
     */
//    private static void calculateRelationWord() throws Exception{
//        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(attributePath)));
//        BufferedReader br = new BufferedReader(inputStreamReader);
//        String temp = br.readLine();
//        temp = br.readLine();
//
//        while(temp != null) {
//            String word = temp.trim();
//            relatedWordCalculator.calculateRelatedWordList(word);
//
//            temp = br.readLine();
//        }
//        br.close();
//    }


    /**
     *
     * 获取所有候选特征词所对应的RelationWordList
     * @return
     */
    private static Map<String,List<ArrayList<RelationWord>>> getFeatureMap() throws Exception{
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(attributePath)));
        BufferedReader br = new BufferedReader(inputStreamReader);
        String temp = br.readLine();
        Map<String,List<ArrayList<RelationWord>>> featureMap = new HashMap<String,List<ArrayList<RelationWord>>>();

        temp = br.readLine();
        while(temp != null) {

            String word = temp.trim();
            ArrayList<RelationWord> relationWordList = new ArrayList<RelationWord>();

            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(relationWordSavePath + word));

            while(true) {
                try {
                    RelationWord relationWord = (RelationWord) objectInputStream.readObject();
                    relationWordList.add(relationWord);
                }catch (EOFException e) {
                    break;
                }

            }

            List<ArrayList<RelationWord>> relationWordArray = new ArrayList<ArrayList<RelationWord>>();
            relationWordArray.add(relationWordList);

            featureMap.put(word, relationWordArray);

            temp = br.readLine();
        }
        br.close();

        return featureMap;
    }


    /**
     * 进行聚类操作
     *
     * @param featureMap
     * @param clusterTool
     */
    private static void clusterOperation(Map<String,List<ArrayList<RelationWord>>> featureMap,ClusterToolOldMethod clusterTool) {

        clusterTool.hierarchicalCluster(featureMap,threshold);

    }



    public static void main(String[] args) throws Exception{

//        System.out.println("Start Time:" + new Date());
//        System.out.println("End Time:" + new Date());


//        calculateRelationWord();
        for(int i=2;i<3;i++) {
            System.out.println("Start Time:" + new Date());
            Map<String,List<ArrayList<RelationWord>>> featureMap = getFeatureMap();
            clusterOperation(featureMap, new ClusterToolOldMethodImpl(i));
            System.out.println("End Time:" + new Date());
            System.out.println("____________________________________");
        }


    }

}
