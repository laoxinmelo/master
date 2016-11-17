package com.raul.bupt.word_vector.pre;

import com.ansj.vec.Word2VEC;
import com.ansj.vec.domain.WordEntry;

import java.io.*;
import java.util.*;

/**
 * Created by Administrator on 2016/11/17.
 */
public class SWFinder {

    private static final String dicType = "ntusd"; //情感词典类型

    private static final String positiveWordPath = "corpus/sentiment/"+dicType+"/positive.txt"; //正向情感词保存路径
    private static final String negativeWordPath = "corpus/sentiment/"+dicType+"/negative.txt"; //负向情感词保存路径

    private static final List<String> positiveWordList = getPositiveWordList();  //正向情感词词典
    private static final List<String> negativeWordList = getNegativeWordList();  //负向情感词词典

    //所需加载模型的保存路径
    private static final String modelPath = "library\\model\\noSwVector";

    //reviewId之间的分隔符
    private static final String freqSplit = ",";

    //word2vec模型工具
    private static final Word2VEC vec = new Word2VEC();


    /**
     * 获取正向情感词
     * @return
     * @throws IOException
     */
    private static List<String> getPositiveWordList(){
        List<String> positiveWordList = new ArrayList<String>();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(positiveWordPath)),"gbk");
            BufferedReader br = new BufferedReader(inputStreamReader);

            String temp = br.readLine();
            while (temp != null) {
                positiveWordList.add(temp.trim());
                temp = br.readLine();
            }
        }catch (IOException e) {
            ;
        }
        return positiveWordList;

    }

    /**
     * 获取负向情感词
     * @return
     * @throws IOException
     */
    private static List<String> getNegativeWordList(){
        List<String> negativeWordList = new ArrayList<String>();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(negativeWordPath)),"gbk");
            BufferedReader br = new BufferedReader(inputStreamReader);

            String temp = br.readLine();
            while (temp != null) {
                negativeWordList.add(temp.trim());
                temp = br.readLine();
            }
        }catch (IOException e) {
            ;
        }
        return negativeWordList;
    }

    /**
     * 获取大连理工情感词典
     * @return
     */
    private static Map<String,Double> getSentimentDict() throws Exception{
        String filePath = "corpus/sentiment/dalian.txt";
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(filePath)),"gbk");
        BufferedReader br = new BufferedReader(inputStreamReader);
        String temp = br.readLine();

        Map<String,Double> sentimentWordMap = new HashMap<String, Double>();
        while(temp != null) {
            String word =temp.substring(0,temp.indexOf("\t"));
            Double value = Double.valueOf(temp.substring(temp.lastIndexOf("\t")+1));

            sentimentWordMap.put(word,value);
            temp = br.readLine();
        }


        return sentimentWordMap;
    }


    /**
     * 找出隐性情感词
     */
    private static void hiddenSentimentWordFinder() {
        String vocabularyPath = "library/vocabulary/noSw.txt";
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(vocabularyPath)));
            BufferedReader br = new BufferedReader(inputStreamReader);
            String temp = br.readLine();

            BufferedWriter bw = new BufferedWriter(new FileWriter("result/"+dicType+"SentimentWord.txt"));
//            BufferedWriter bw = new BufferedWriter(new FileWriter("result/dalianSentimentWord.txt"));
//            Map<String,Double> sentimentWordMap = getSentimentDict();

            while(temp != null) {
                temp = temp.trim();
                String word = temp.substring(0,temp.lastIndexOf(freqSplit));



                if(!word.replaceAll("[^\u4e00-\u9fa5]","").equals("")) {
                    if ((!positiveWordList.contains(word)) && (!negativeWordList.contains(word))) {
//                     if(!sentimentWordMap.containsKey(word)) {
                        Set<WordEntry> simiWordSet = vec.distance(word);
                        float value = 0;

                        int simiCount = 0;
                        for (WordEntry wordEntry : simiWordSet) {
                            String simiWord = wordEntry.name;
                            float simiValue = wordEntry.score;
                            /**
                             * 使用大连理工情感词典时的情感计算方法
                             */
//                            if(sentimentWordMap.containsKey(simiWord)) {
//                                value += simiValue * sentimentWordMap.get(simiWord);
//                                System.out.println(word + " " + simiWord + "    " + simiValue);
//                                simiCount += 1;
//                            }

                            /**
                             * 使用NTUSD情感情感词典时的情感计算方法
                             */
                            if (positiveWordList.contains(simiWord)) {
                                value += simiValue;
                                simiCount += 1;
                            }
                            if (negativeWordList.contains(simiWord)) {
                                value -= simiValue;
                                simiCount += 1;
                            }
                        }

//                         if(simiCount != 0) {
//                             value = value/simiCount;
//                         }
                         if (value != 0) {
                            System.out.println(word + " " + value + "   " + value/simiCount);
                            bw.write(word + "\t" + value + "\t" + value/simiCount + "\r\n");
                         }

                        System.out.println("______________________________________");
                    }
                }
                temp = br.readLine();
            }

            br.close();
            bw.flush();  bw.close();
        }catch(Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 将语料中出现过的情感词加入到redis中
     * @param args
     */
    public static void main(String[] args) throws Exception{
        vec.loadJavaModel(modelPath);  //模型加载
        hiddenSentimentWordFinder();
//        getSentimentDict();
    }
}
