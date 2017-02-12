package com.raul.bupt.word_vector;

import com.raul.bupt.db.RedisTool;
import com.raul.bupt.db.impl.RedisToolImpl;
import com.raul.bupt.parser.dataobject.RelationDO;
import com.raul.bupt.parser.dataobject.WordDO;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.util.*;

/**
 * Created by Administrator on 2016/11/18.
 */
public class AspectBasedAnalyzer {

    //redis缓存工具
    private static final RedisTool redisTool = new RedisToolImpl();
    //特征分隔符
    private static final String featureSplit = "_";
    //保存结果的分隔符
    private static final String saveSplit = "\t";
    //名词的词性
    private static final String nounPOS = "NN";
    //情感词典在redis中的索引
    private static final Integer swIndex = 4;
    //特征集合在redis中的索引
    private static final Integer featureIndex = 5;
    //商品语义关系存放位置
    private static final String itemFeatureSavePath = "result/ecigar/feature/";

    //情感词集
    private static final Map<String,Integer> swMap = getSentimentWordMap();
    //特征列表
    private static final List<String> featureList = getFeatureList();


    /**
     * 从redis中读取特征列表
     * @return
     */
    private static List<String> getFeatureList() {

        List<String> tempFeatureList = new ArrayList<String>();
        Set keySet = redisTool.getKeys(featureIndex);
        for(Object key:keySet) {
            String tempFeature = redisTool.getValue(featureIndex,(String) key);
            System.out.println(tempFeature);
            tempFeatureList.add(tempFeature);
        }
        return tempFeatureList;
    }

    /**
     * 从redis中读取情感词典
     * @return
     */
    private static Map<String,Integer> getSentimentWordMap() {

        Map<String,Integer> sentimentWordMap = new HashMap<String, Integer>();
        Set keySet = redisTool.getKeys(swIndex);
        for(Object key:keySet) {

            String word = String.valueOf(key);
            Integer value = Integer.valueOf(redisTool.getValue(swIndex,word));
            sentimentWordMap.put(word,value);
        }
        return sentimentWordMap;
    }

    /**
     * 判断某个词属于哪一项特征集
     * 如果属于则返回该特征集的id；如果不属于则返回-1
     * @param word
     * @return
     */
    private static int belongToFeature(String word) {
        int featureIndex = -1;
        for(int i=0;i<featureList.size();i++) {
            String featureStr = featureList.get(i);
            if(featureStr.contains(featureSplit + word + featureSplit)) {
                featureIndex = i;
                break;
            }
        }
        return featureIndex;
    }


    /**
     * 判断某个词是否为情感词（显性+隐性）
     * 如果是，则返回其极性（1.-1）
     * 如果不是，返回0
     * @param sentimentWord
     * @return
     */
    private static int belongToSW(String sentimentWord) {
        int polarity = 0;
        if(redisTool.exist(swIndex,sentimentWord)) {
            polarity = Integer.valueOf(redisTool.getValue(swIndex,sentimentWord));
        }
        return polarity;
    }

    /**
     * 进行细粒度情感计算
     */
    private static void AspectBasedFeatureSentimentCalculate() {
        int featureSize = featureList.size();  //特征维度

        try {

            BufferedWriter bw = new BufferedWriter(new FileWriter("result/ecigar/aspect-based.txt"));

            File[] files = new File(itemFeatureSavePath).listFiles();
            for(File file:files) {

                int[] featureSentimentValueArray = new int[featureSize*2];
                for(int i=0;i<featureSize;i++) {
                    featureSentimentValueArray[i] = 0;
                }

                String itemId = file.getName();
                ObjectInputStream oos = new ObjectInputStream(new FileInputStream(itemFeatureSavePath + itemId));
                int featureNum = oos.readInt();
                int count = 0;
                while(count < featureNum) {
                    RelationDO relationDO = (RelationDO) oos.readObject();
                    WordDO govWordDO = relationDO.getGovWordDO();
                    WordDO depWordDO = relationDO.getDepWordDO();
                    boolean withNeg = relationDO.isWithNeg();

                    if(govWordDO.getPos().equals(nounPOS)) {
                        String candFeature = govWordDO.getWord().trim();
                        int featureIndex = belongToFeature(candFeature);
                        if((featureIndex!=-1) && (!depWordDO.getPos().equals(nounPOS)))  {
                                String candSentimentWord = depWordDO.getWord().trim();
                                int polarity = belongToSW(candSentimentWord);
                                if(withNeg) {polarity = polarity * (-1);}

                                if(polarity > 0) {
                                    featureSentimentValueArray[featureIndex] += polarity;
                                    System.out.println(candFeature + "  " + candSentimentWord + "   " + polarity + "    " + featureIndex);
                                } else if(polarity < 0) {
                                    featureSentimentValueArray[featureIndex+featureSize] += polarity;
                                    System.out.println(candFeature + "  " + candSentimentWord + "   " + polarity + "    " + (featureIndex+1));
                                }
                            }
                    }


                    if(depWordDO.getPos().equals(nounPOS)) {
                        String candFeature = depWordDO.getWord().trim();
                        int featureIndex = belongToFeature(candFeature);
                        if((featureIndex!=-1) && (!govWordDO.getPos().equals(nounPOS)))  {
                            String candSentimentWord = govWordDO.getWord().trim();
                            int polarity = belongToSW(candSentimentWord);
                            if(withNeg) {polarity = polarity * (-1);}

                            if(polarity > 0) {
                                featureSentimentValueArray[featureIndex] += polarity;
                                System.out.println(candFeature + "  " + candSentimentWord + "   " + polarity + "    " + featureIndex);
                            } else if(polarity < 0) {
                                featureSentimentValueArray[featureIndex+featureSize] += polarity;
                                System.out.println(candFeature + "  " + candSentimentWord + "   " + polarity + "    " + (featureIndex+1));
                            }
                        }
                    }

                    count += 1;
                }

                try {

                    String input = itemId;
                    for (int featureSentimentValue : featureSentimentValueArray) {
                        input += saveSplit + featureSentimentValue;
                    }
                    System.out.println(input);
                    bw.write(input + "\r\n");
                    System.out.println("_________________________________");
                }catch (IOException e) {
                }
                oos.close();
            }

            bw.flush();  bw.close();
        }catch (IOException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        AspectBasedFeatureSentimentCalculate();
    }
}
