package com.raul.bupt.parser;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.TypedDependency;

import java.util.List;

/**
 * Created by Administrator on 2016/11/6.
 */
public interface Parser {


    /**
     * 对句子中各个词进行词性标注
     * @param sentence
     * @return
     */
    public List<TaggedWord> wordPosTag(String sentence);

    /**
     * 输出句法依赖树的结构
     * @param sentence
     */
    public void grammarTreePrint(String sentence);

    /**
     * 提取出句子中的所有语义关系
     * @param sentence
     */
    public List<TypedDependency> grammarRelationExtract(String sentence);
}
