package com.raul.bupt.word_freq;

import com.raul.bupt.db.DBTool;
import com.raul.bupt.db.RedisTool;
import com.raul.bupt.db.impl.DBToolImpl;
import com.raul.bupt.db.impl.RedisToolImpl;
import com.raul.bupt.parser.dataobject.RelationDO;

import java.io.*;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by Administrator on 2016/11/15.
 */
public class NounCounter {

    //名词标识符
    private static final String nounIndex = "NN";
    //redis缓存操作工具
    private static final RedisTool redisTool = new RedisToolImpl();
    //reviewId之间的分隔符
    private static final String reviewSplit = ";";
    //文本内容之间的分隔符
    private static final String contentSplit = "\r\n";
    //词与词之间的分隔符
    private static final String wordSplit = "\\s";
    //词与词性之间的分隔符
    private static final String unitSplit = "#";

    /**
     * 对初始评论和追加评论中的所有名词进行统计
     * @return
     */
    private static Map WordFreqCount() {

        //每个词对应一个hashMap.该hashMap表示该词以各类词性在语料中的出现次数
        Map<String,HashMap<String,Integer>> wordMap = new HashMap<String, HashMap<String,Integer>>();

        Set<String> itemIdSet = redisTool.getKeys(0);
        for(String itemId : itemIdSet) {
            String reviewIdStr = redisTool.getValue(0,itemId);
            String[] reviewIdArray = reviewIdStr.split(reviewSplit); //获取所有reviewId

            for(String reviewId:reviewIdArray) {
                for(int i=1;i<=2;i++) {
                    String totalContent = redisTool.getValue(i,reviewId);

                    if(totalContent != null) {
                        String[] contentArray = totalContent.split(contentSplit);
                        String contentWithPos = contentArray[2];
                        String[] wordUnitArray = contentWithPos.split(wordSplit);
                        for(String wordUnit:wordUnitArray) {
                            String word = wordUnit.substring(0,wordUnit.lastIndexOf(unitSplit));
                            String POS = wordUnit.substring(wordUnit.lastIndexOf(unitSplit)+1);

                            HashMap<String,Integer> freqMap = null;
                            if(wordMap.containsKey(word)) {
                                freqMap = wordMap.get(word);
                                if(freqMap.containsKey(POS)) {
                                    freqMap.put(POS,freqMap.get(POS) + 1);
                                }else {
                                    freqMap.put(POS,1);
                                }
                            }else {
                                freqMap = new HashMap<String, Integer>();
                                freqMap.put(POS,1);
                            }
                            wordMap.put(word,freqMap);

                        }
                    }

                }
            }
        }
        return wordMap;
    }


    /**
     * 对电子烟评论的所有名词进行统计
     * @return
     */
    private static Map WordFreqCount4Ecigar() throws Exception {

        //每个词对应一个hashMap.该hashMap表示该词以各类词性在语料中的出现次数
        Map<String, HashMap<String, Integer>> wordMap = new HashMap<String, HashMap<String, Integer>>();

        InputStreamReader isr = new InputStreamReader(new FileInputStream(new File("corpus/taggerNote.txt")),"gbk");
        BufferedReader br = new BufferedReader(isr);

        String temp = br.readLine();
        while (temp != null) {
            temp = temp.substring(temp.lastIndexOf("\t")+1);
            String[] wordUnitArray = temp.trim().split(wordSplit);
            for (String wordUnit : wordUnitArray) {
                try {
                    String word = wordUnit.substring(0, wordUnit.lastIndexOf(unitSplit));
                    String POS = wordUnit.substring(wordUnit.lastIndexOf(unitSplit) + 1);

                    HashMap<String, Integer> freqMap = null;
                    if (wordMap.containsKey(word)) {
                        freqMap = wordMap.get(word);
                        if (freqMap.containsKey(POS)) {
                            freqMap.put(POS, freqMap.get(POS) + 1);
                        } else {
                            freqMap.put(POS, 1);
                        }
                    } else {
                        freqMap = new HashMap<String, Integer>();
                        freqMap.put(POS, 1);
                    }
                    wordMap.put(word, freqMap);
                }catch (StringIndexOutOfBoundsException e) {
                    System.out.println(wordUnit);
                }
            }

            temp = br.readLine();
        }

        return wordMap;
    }



    /**
     * 从所提取的语义关系中获取名词，并保存在Set当中
     * 逐一获取其在所有评论中的词频
     */
    private static void getAttributeFreq(Map<String,Map<String,Integer>> wordMap) {
        String filePath = "result/ecigar/feature/";
        List<String> wordList = new ArrayList<String>();

        try {
        for(File file : new File(filePath).listFiles()) {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()));
            Integer featureNum = objectInputStream.readInt();
            for(int i=0;i<featureNum;i++) {
                RelationDO relationDO = (RelationDO) objectInputStream.readObject();

                String depWord = relationDO.getDepWordDO().getWord();
                String depPos = relationDO.getDepWordDO().getPos();

                String govWord = relationDO.getGovWordDO().getWord();
                String govPos = relationDO.getGovWordDO().getPos();

                if(depPos.equals(nounIndex) && (!wordList.contains(depWord))) {
                    wordList.add(depWord);
                }
                if(govPos.equals(nounIndex) && (!wordList.contains(govWord))) {
                    wordList.add(govWord);
                }
            }
        }
        }catch(Exception e){
                e.printStackTrace();
        }

        //保存词频结果
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("result/candAttribute.txt"));
            for (String word : wordList) {
                Map<String,Integer> freqMap = wordMap.get(word);

                int totalCount = 0;
                int nnCount = 0;
                 try {
                     for (String POS : freqMap.keySet()) {
                         int tempCount = freqMap.get(POS);

                         totalCount += tempCount;
                         if (POS.equals(nounIndex)) {
                             nnCount += tempCount;
                         }
                     }
                 }catch(NullPointerException e) {
                     System.out.println(word);
                     e.printStackTrace();
                 }

                bw.write(word + "\t" + nnCount + "\t" + totalCount + "\r\n");
            }
            bw.flush();
            bw.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{

        getAttributeFreq(WordFreqCount4Ecigar());
    }
}
