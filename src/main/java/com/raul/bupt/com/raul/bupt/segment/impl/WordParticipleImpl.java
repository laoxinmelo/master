package com.raul.bupt.com.raul.bupt.segment.impl;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.raul.bupt.com.raul.bupt.segment.WordParticiple;

import java.util.List;

/**
 * Created by Administrator on 2016/11/6.
 */
public class WordParticipleImpl  implements WordParticiple{

    //分词工具
    JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();

    /**
     * 对句子进行分词
     * @param sentence
     */
    public String wordSegment(String sentence) {

        if(sentence == null) {
            throw new NullPointerException("The sentence is NULL...");
        }

        String wordSegmentResult = ""; //用来保存分词后的结果...

        //分词处理
        List<String> wordList = jiebaSegmenter.sentenceProcess(sentence.trim());
        for(String word : wordList) {
               wordSegmentResult += word + " ";
        }

        System.out.printf(wordSegmentResult);
        return wordSegmentResult;
    }

    public static void main(String[] args) {
        WordParticiple wordParticiple = new WordParticipleImpl();

        wordParticiple.wordSegment("我爱北京天安门...");
    }

}
