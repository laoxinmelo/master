package com.raul.bupt.segment;

import java.util.List;

/**
 * Created by Administrator on 2016/11/6.
 */
public interface WordParticiple {
    /**
     * 对句子进行分词
     * @param sentence
     * @return
     */
    public String wordSegment(String sentence, boolean pos);

    /**
     * 对句子进行分词处理，删除句子中的标点符号以及停用词
     * @param sentence
     * @return
     */
    public String wordSegmentWithoutStopWord(String sentence);

    /**
     * 读取词典，将新词导入到用户词典当中
     */
    public void updateUserDict();

    /**
     * 将词导入到用户词典当中
     */
    public void addNewWord(List wordList);

    /**
     * 找出句子中的关键词，并导入到用户词典当中
     * @param sentence
     */
    public void findKeyWords(String sentence);

    /**
     * 找出句子中的关键词，并导入到用户词典当中
     * @param sentence
     */
    public void findNewWords(String sentence);
}
