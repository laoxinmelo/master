package com.raul.bupt.apply;

import com.raul.bupt.calculate.clusters.methods.ours.ClusterToolNewMethod;
import com.raul.bupt.calculate.clusters.methods.ours.impl.ClusterToolNewMethodImpl;
import com.raul.bupt.calculate.clusters.methods.theirs.ClusterToolOldMethod;
import com.raul.bupt.calculate.clusters.methods.theirs.impl.ClusterToolOldMethodImpl;
import com.raul.bupt.calculate.simi.RelatedWordCalculator;
import com.raul.bupt.calculate.simi.dataobject.RelationWord;
import com.raul.bupt.calculate.simi.impl.RelatedWordCalculatorImpl;
import com.raul.bupt.word2vec.Word2VEC;

import javax.management.relation.Relation;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/27.
 */
public class OldMethodFirstClusterApply {

    //层次聚类工具
    private static final ClusterToolOldMethod clusterTool = new ClusterToolOldMethodImpl(1);

    private static final RelatedWordCalculator relatedWordCalculator = new RelatedWordCalculatorImpl();

    //初始特征的保存路径
    private static final String attributePath = "result/startAttribute.txt";

    //阈值
    private static final float threshold = Float.valueOf("0.4");

    /**
     * 获取所有初始特征
     * @return
     */
    private static Map<String,List<RelationWord>[]> getFeatureMap() throws Exception{
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(attributePath)));
        BufferedReader br = new BufferedReader(inputStreamReader);
        String temp = br.readLine();
        Map<String,List<RelationWord>[]> featureMap = new HashMap<String,List<RelationWord>[]>();

        temp = br.readLine();
        while(temp != null) {
            String word = temp.trim();
            List<RelationWord> relationWordList = relatedWordCalculator.calculateRelatedWordList(word);

            //保存对应的relationWord
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("result/relationWord/" + word));
                for(RelationWord relationWord:relationWordList) {
                    objectOutputStream.writeObject(relationWord);
                }
                objectOutputStream.flush();  objectOutputStream.close();
            }catch (Exception e) {
                e.printStackTrace();
            }

            List<List<RelationWord>> tempRelationWordList = new ArrayList<List<RelationWord>>();
            tempRelationWordList.add(relationWordList);
            List<RelationWord>[] relationWordListArray = (List<RelationWord>[]) tempRelationWordList.toArray();
            featureMap.put(word,relationWordListArray);

            temp = br.readLine();
        }
        br.close();

        return featureMap;
    }


    public static void main(String[] args) throws Exception{

        Map<String,List<RelationWord>[]> featureMap = getFeatureMap();

//        clusterTool.hierarchicalCluster(featureMap,threshold);
    }

}
