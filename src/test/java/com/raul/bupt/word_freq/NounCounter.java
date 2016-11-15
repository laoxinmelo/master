package com.raul.bupt.word_freq;

import com.raul.bupt.db.RedisTool;
import com.raul.bupt.db.impl.RedisToolImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
     * 并且保存在redis库中（4）
     * @return
     */
    public void WordFreqCount() {

        Map<String,Integer> wordMap = new HashMap<String, Integer>();

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
                            if(POS.equals(nounIndex)) {
                                if(wordMap.containsKey(word)) {
                                    wordMap.put(word,wordMap.get(word)+1);
                                }else {
                                    wordMap.put(word,1);
                                }
                            }
                        }
                    }

                }
            }
        }

        Set<String> wordSet = wordMap.keySet();
        for(String word : wordSet) {
            Integer freq = wordMap.get(word);
            redisTool.setValue(4,word,String.valueOf(freq));
        }
    }

    public static void main(String[] args) {
        new NounCounter().WordFreqCount();
    }
}
