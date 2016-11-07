package com.raul.bupt.parser.adapter;

import com.raul.bupt.parser.impl.ParserImpl;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

import java.util.List;

/**
 * Created by Administrator on 2016/11/7.
 */
public class ParserImplAdapter  extends ParserImpl{

    //符合要求的语义关系组
    private static final String[] featureRelationArray = {"amod","nsubj","rcmod"};

    /**
     * 根据句子的语义依赖模型，对句子中的各个词进行词性标注
     * @param depenTree
     * @return
     */
    private List<TaggedWord> wordPosTag(Tree depenTree) {

        if(depenTree == null) {
            throw new NullPointerException("The Input Dependency Tree is NULL...");
        }
        return depenTree.taggedYield();
    }

    /**
     * 根据句子的语义依赖模型，获取其中所包含的所有语义关系
     * @param depenTree
     * @return
     */
    private List<TypedDependency> grammarRelationExtract(Tree depenTree) {
        if(depenTree == null) {
            throw new NullPointerException("The Input Dependency Tree is NULL...");
        }
        return grammaticalStructureFactory.newGrammaticalStructure(depenTree).typedDependenciesCCprocessed();
    }

    /**
     * 找出符合要求的语义关系
     * @param sentence
     */
    public void featureExtract(String sentence) {
        Tree depenTree = sentenceParse(sentence);   //获取句子的语义依赖树
        List<TaggedWord> wordPosList = this.wordPosTag(depenTree); //对句子进行词性标注
        List<TypedDependency> grammarRelationList = this.grammarRelationExtract(depenTree); //获取句子的所有语义关系

        for(TypedDependency typedDependency:grammarRelationList) {
            GrammaticalRelation grammaticalRelation = typedDependency.reln();
            System.out.println(typedDependency);
            System.out.println(typedDependency.gov().value() + "    " + typedDependency.dep().value());
        }
    }


    public static void main(String[] args) {
        String sentence = "今天 天气 很 好";

        ParserImplAdapter parserAdapter = new ParserImplAdapter();

        parserAdapter.featureExtract(sentence);

    }

}
