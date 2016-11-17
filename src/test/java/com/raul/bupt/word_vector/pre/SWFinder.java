package com.raul.bupt.word_vector.pre;

import com.ansj.vec.Word2VEC;
import com.ansj.vec.domain.WordEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/17.
 */
public class SWFinder {

    private static final String positiveWordPath = "corpus/sentiment/ntusd/positive.txt"; //正向情感词保存路径
    private static final String negativeWordPath = "corpus/sentiment/ntusd/negative.txt"; //负向情感词保存路径

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
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(positiveWordPath)));
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
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(negativeWordPath)));
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
     * 找出隐性情感词
     */
    private static void hiddenSentimentWordFinder() {
        String vocabularyPath = "library/vocabulary/noSw.txt";
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(vocabularyPath)));
            BufferedReader br = new BufferedReader(inputStreamReader);
            String temp = br.readLine();


            BufferedWriter bw = new BufferedWriter(new FileWriter("result/ntusdSentimentWord.txt"));
            while(temp != null) {
                temp = temp.trim();
                String word = temp.substring(0,temp.lastIndexOf(freqSplit));


                if(!word.replaceAll("[^\u4e00-\u9fa5]","").equals("")) {
                    if ((!positiveWordList.contains(word)) && (!negativeWordList.contains(word))) {
                        Set<WordEntry> simiWordSet = vec.distance(word);
                        float value = 0;

                        for (WordEntry wordEntry : simiWordSet) {
                            String simiWord = wordEntry.name;
                            float simiValue = wordEntry.score;

                            if (positiveWordList.contains(simiWord)) {
                                value += simiValue;
                            }
                            if (negativeWordList.contains(simiWord)) {
                                value -= simiValue;
                            }
                        }

                        if (value != 0) {
                            System.out.println(word + " " + value);
                            bw.write(word + "\t" + value + "\r\n");
                        }
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
    }

}
