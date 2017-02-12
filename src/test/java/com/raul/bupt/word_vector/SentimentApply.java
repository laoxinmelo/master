package com.raul.bupt.word_vector;

import com.raul.bupt.db.DBTool;
import com.raul.bupt.db.RedisTool;
import com.raul.bupt.db.impl.DBToolImpl;
import com.raul.bupt.db.impl.RedisToolImpl;
import com.raul.bupt.word_vector.pre.SWFinder;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Administrator on 2016/11/18.
 */
public class SentimentApply {


    //redis操作工具
    private static final RedisTool redisTool = new RedisToolImpl();
    //数据库操作工具
    private static final DBTool dbTool = new DBToolImpl();

    private static final List<String> positiveWordList = SWFinder.getPositiveWordList();  //正向情感词词典
    private static final List<String> negativeWordList = SWFinder.getNegativeWordList();  //负向情感词词典

    private static final int originReviewIndex = 1;
    private static final int appendReviewIndex = 2;
    private static final int sentimentWordIndex = 6;
    private static final int contentWithoutStopWordIndex = 1;

    private static final String contentSplit = "\r\n";
    private static final String wordSplit = " ";
    private static final String wordValueSplit = "\t";

    private static final String positiveIndex = "positive";
    private static final String negativeIndex = "negative";

    private static final String hiddenSentimentWordSavePath = "corpus/sentiment/hidden/HiddenSentimentEcigar.txt";

    /**
     * 将情感词（隐性+显性）按照情感倾向加入到redis库中
     */
    private static void sentimentWordToRedis() {

        Map<String,HashSet<String>> swMap = ergodicReviewForSW4Ecigar();
        Set<String> positiveSet = swMap.get(positiveIndex);
        Set<String> negativeSet = swMap.get(negativeIndex);

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(hiddenSentimentWordSavePath))));
            String temp = br.readLine();
            while(temp != null) {
                if(temp.contains(wordValueSplit)) {
                    String word = temp.substring(0, temp.lastIndexOf(wordValueSplit));
                    Double value = Double.valueOf(temp.substring(temp.lastIndexOf(wordValueSplit) + 1));
                    if(value>0) {
                        positiveSet.add(word);
                    }
                    if(value<0) {
                        negativeSet.add(word);
                    }
                }

                temp = br.readLine();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        for(String positiveWord:positiveSet) {
            redisTool.setValue(sentimentWordIndex,positiveWord,"1");
        }
        for(String negativeWord:negativeSet) {
            redisTool.setValue(sentimentWordIndex,negativeWord,"-1");
        }

    }


    /**
     * 遍历所有评论文本，找出文本中出现的情感词...针对电子烟评论
     * @return
     */
    private static Map<String,HashSet<String>> ergodicReviewForSW4Ecigar() {
        Map<String,HashSet<String>> swMap = new HashMap<String, HashSet<String>>();
        swMap.put(positiveIndex,new HashSet<String>());
        swMap.put(negativeIndex,new HashSet<String>());


        ResultSet resultSet = dbTool.query("select segmentNoSW from ecigar;");
        try{
            while(resultSet.next()) {
                String segmentNoSW = resultSet.getString(1);
                System.out.println(segmentNoSW);

                for(String word:segmentNoSW.split(wordSplit)) {
                    word = word.trim();
                    if(positiveWordList.contains(word)) {
                        HashSet<String> positiveSet = swMap.get(positiveIndex);
                        positiveSet.add(word);
                    }
                    if(negativeWordList.contains(word)) {
                        HashSet<String> negativeSet = swMap.get(negativeIndex);
                        negativeSet.add(word);
                    }
                }
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return swMap;
    }


    /**
     * 遍历所有评论文本，找出文本中出现的情感词
     * @return
     */
    private static Map<String,HashSet<String>> ergodicReviewForSW() {
        Map<String,HashSet<String>> swMap = new HashMap<String, HashSet<String>>();
        swMap.put(positiveIndex,new HashSet<String>());
        swMap.put(negativeIndex,new HashSet<String>());

        for(int i=originReviewIndex;i<=appendReviewIndex;i++) {

            Set<String> reviewIdSet = redisTool.getKeys(i);
            for(String reviewId:reviewIdSet) {
                String totalContent = redisTool.getValue(i,reviewId);
                String contentWithoutStopWord = totalContent.split(contentSplit)[contentWithoutStopWordIndex];
                for(String word:contentWithoutStopWord.split(wordSplit)) {
                    word = word.trim();
                    if(positiveWordList.contains(word)) {
                        HashSet<String> positiveSet = swMap.get(positiveIndex);
                        positiveSet.add(word);
                    }
                    if(negativeWordList.contains(word)) {
                        HashSet<String> negativeSet = swMap.get(negativeIndex);
                        negativeSet.add(word);
                    }
                }
            }
        }

        return swMap;

    }

    public static void main(String[] args) {
        sentimentWordToRedis();
    }


}
