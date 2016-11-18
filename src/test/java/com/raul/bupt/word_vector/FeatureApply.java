package com.raul.bupt.word_vector;

import com.ansj.vec.Word2VEC;
import com.cluster.hierarchical.ClusterTool;
import com.cluster.hierarchical.impl.ClusterToolImpl;
import com.raul.bupt.db.RedisTool;
import com.raul.bupt.db.impl.RedisToolImpl;
import com.raul.bupt.parser.dataobject.RelationDO;
import com.raul.bupt.parser.dataobject.WordDO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 对于所提取的候选特征词，完成筛选过后，对初次聚类分析所得的簇进行扩充
 * Created by Administrator on 2016/11/17.
 */

public class FeatureApply {

    //初始特征聚类结果
    private static final String originFeatureSavePath = "result/originFeature.txt";
    //word2vec模型存放位置
    private static final String vecModelPath = "library\\model\\noSwVector";
    //特征存放位置
    private static final String itemFeatureSavePath = "result/feature/";
    //所得特征提取结果的保存位置
    private static final String featureTableSavePath = "result/featureTable.txt";
    //候选特征词保存位置
    private static final String candFeatureSavePath = "result/candAttribute.txt";

    //redis库操作工具
    private static final RedisTool redisTool = new RedisToolImpl();

    //名词词性标注
    private static final String nounTag = "NN";
    //特征之间的分隔符
    private static final String featureSplit = "_";
    //所提取得到特征之间的分隔符
    private static final String tableSplit = "\t";
    //相似度阈值
    private static final double simiThreshold = 0.3;
    //所提取特征在redis中的存放位置
    private static final int featureRedisIndex = 5;

    //保存各个簇的特征词
    private static List<String> clusterList = getOriginFeatureCluster();
    //所有候选词
    private static List<String> candFeatureList = getCandidateFeature();

    //word2vec模型加载器
    private static final Word2VEC vec = new Word2VEC();
    //聚类分析工具
    private static final ClusterTool clusterTool = new ClusterToolImpl();


    /**
     * 获取所有候选特征词
     * @return
     */
    private static List getCandidateFeature() {
        List<String> candFeatureList = new ArrayList<String>();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(candFeatureSavePath)),"utf-8");
            BufferedReader br = new BufferedReader(inputStreamReader);
            String temp = br.readLine();

            while(temp != null) {
                candFeatureList.add(temp.trim());
                temp = br.readLine();
            }

            br.close();
        }catch(Exception e) {
            e.printStackTrace();
        }

        return candFeatureList;
    }


    /**
     * 在对词频数最高的100个词进行层次聚类后，
     * 读取该聚类结果
     * @return
     */
    private static List<String> getOriginFeatureCluster() {
        List<String> clusterList = new ArrayList<String>();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(originFeatureSavePath)),"utf-8");
            BufferedReader br = new BufferedReader(inputStreamReader);
            String temp = br.readLine();

            while(temp != null) {
                if(temp.contains(featureSplit)) {
                    clusterList.add(temp.trim());
                }
                temp = br.readLine();
            }

            br.close();
        }catch(Exception e) {
            e.printStackTrace();
        }

        return clusterList;
    }

    /**
     * 对候选特征进行计算
     * @return
     */
    private static void candFeatureCalculate() {

        try {
            File[] files = new File(itemFeatureSavePath).listFiles();
            for(File file:files) {
                ObjectInputStream oos = new ObjectInputStream(new FileInputStream(itemFeatureSavePath + file.getName()));
                int featureNum = oos.readInt();
                int count = 0;
                while(count < featureNum) {
                    RelationDO relationDO = (RelationDO) oos.readObject();
                    WordDO govWordDO = relationDO.getGovWordDO();
                    WordDO depWordDO = relationDO.getDepWordDO();

                    if(govWordDO.getPos().equals(nounTag)) {
                        String govWord = govWordDO.getWord();
                        if(candFeatureList.contains(govWord)) {
                            addToCluster(govWord);
                        }
                    }
                    if(depWordDO.getPos().equals(nounTag)) {
                        String depWord = depWordDO.getWord();
                        if(candFeatureList.contains(depWord)) {
                            addToCluster(depWord);
                        }
                    }

                    count += 1;
                }
                oos.close();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断某个名词是否可以作为特征词
     * 如果可以，则将其加入到对应的簇当中
     * @param word
     * @return
     */
    private static void addToCluster(String word) {

        int maxIndex = 0;
        float maxValue = 0;

        for(int i=0;i<clusterList.size();i++) {

            String featureStr = clusterList.get(i);
            //1、判断该词是否属于某个簇
            if(featureStr.contains(featureSplit + word + featureSplit) ||
                    featureStr.startsWith(word + featureSplit) ||
                    featureStr.endsWith(featureSplit + word)) {
                return ;
            }

            //2、计算该词与这个簇中所有特征的平均相似度
            float tempValue = 0;
            String[] tempFeatureArray = featureStr.split(featureSplit);
            for(String featureWord:tempFeatureArray) {
                try {
                    tempValue += clusterTool.getSimi(vec.getWordVector(word.trim()), vec.getWordVector(featureWord.trim()));
                }catch (NullPointerException e) {
                }
            }
            tempValue = tempValue/tempFeatureArray.length;
            if(tempValue > maxValue) {
                maxIndex = i;
                maxValue = tempValue;
            }
        }

        if(maxValue > simiThreshold) {
            System.out.println(word + " " + clusterList.get(maxIndex) + "   " + maxValue);
            clusterList.set(maxIndex, clusterList.get(maxIndex) + featureSplit + word);
            System.out.println("___________________________");
        }
    }


    /**
     * 将特征保存到redis当中
     */
    private static void featureToRedis() {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(featureTableSavePath))));
            String temp = br.readLine();
            int featureSize = temp.split(tableSplit).length/2;

            String[] featureArray = new String[featureSize];
            for(int i=0;i<featureSize;i++) {
                featureArray[i] = featureSplit;
            }

            while(true) {
                temp = br.readLine();
                if(temp == null) {
                    break;
                }

                String[] tempArray = temp.split(tableSplit);
                for(int i=0;i<tempArray.length;i=i+2) {
                    if(!tempArray[i].equals("")) {
                        featureArray[i/2] += tempArray[i] + featureSplit;
                    }
                }

            }

            for(int i=0;i<featureSize;i++) {
                redisTool.setValue(5,String.valueOf(i+1),featureArray[i]);
            }

            br.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws IOException{

        featureToRedis();
    }


}
