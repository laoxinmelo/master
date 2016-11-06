package com.raul.bupt.segment;

import com.huaban.analysis.jieba.JiebaSegmenter;

import java.io.File;

/**
 * Created by Administrator on 2016/11/6.
 */
public interface WordParticiple {
    /**
     * 对句子进行分词，无词性标注
     * @param sentence
     * @return
     */
    public String wordSegment(String sentence, boolean pos);

    /**
     * 将新词导入到用户词典当中
     */
    public void updateUserDict(String filesPath);
}
