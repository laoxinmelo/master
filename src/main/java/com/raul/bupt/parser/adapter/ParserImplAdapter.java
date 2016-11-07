package com.raul.bupt.parser.adapter;

import com.raul.bupt.parser.impl.ParserImpl;
import com.raul.bupt.parser.proxy.TypedDependencyProxy;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/7.
 */
public class ParserImplAdapter  extends ParserImpl{

    //符合要求的语义关系组
    private static final String[] featureRelationArray = {"amod","nsubj","rcmod"};
    //无效的关系
    private static final String rootRelation = "root";

    private static final String punctuation = "PU";


    /**
     * 判断某种语义关系是否符合要求
     * @param candRelation
     * @return
     */
    private boolean containAspect(String candRelation) {
        boolean contain = false;
        for(String relation:featureRelationArray) {
            if(candRelation.equals(relation)) {
                contain = true;
            }
        }
        return contain;
    }

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
     * 根据标点符号，将句子划分成各个小段落
     * @param taggedList
     * @return
     */
    private List sentenceSplit(List<TaggedWord> taggedList) {

        List splitList = new ArrayList();

        String sentence = "";
        for(TaggedWord taggedWord : taggedList) {
            String word = taggedWord.word();
            String pos = taggedWord.tag();

            if(pos.equals(punctuation)) {
                if(!sentence.equals("")) {
                    splitList.add(sentence);
                }
                sentence = "";

            }else {
                sentence += word + " ";
            }
        }

        if(!sentence.equals("")) {
            splitList.add(sentence);
        }

        return splitList;
    }

    /**
     * 找出符合要求的语义关系
     * @param sentence
     */
    public List featureExtract(String sentence) {

        List<TaggedWord> posList = this.wordPosTag(sentence); //对句子进行词性标注
        List splitList = sentenceSplit(posList);

        //用来保存包含细粒度特征的语义关系
        List<TypedDependencyProxy> featureList = new ArrayList<TypedDependencyProxy>();

        for(Object split : splitList) {

            Tree depenTree = sentenceParse((String)split);   //获取句子的语义依赖树
            List<TaggedWord> wordPosList = this.wordPosTag(sentence);  //对句子进行词性标注
            List<TypedDependency> grammarRelationList = this.grammarRelationExtract(depenTree); //获取句子的所有语义关系

            /**
             * 找出句子中所有细粒度特征语义关系
             */
            for (TypedDependency typedDependency : grammarRelationList) {
                GrammaticalRelation grammaticalRelation = typedDependency.reln();

                //获取语义关系的名称
                String grammaticalName = grammaticalRelation.getShortName();
                if (grammaticalName.equals(rootRelation)) {
                    continue;
                }
                if ((!containAspect(grammaticalName))) {
                    continue;
                }

                //获取语义关系中两个词的词性
                String govPos = wordPosList.get(typedDependency.gov().index() - 1).tag();
                String depPos = wordPosList.get(typedDependency.dep().index() - 1).tag();

                TypedDependencyProxy typedDependencyProxy = new TypedDependencyProxy();
                typedDependencyProxy.setTypedDependency(typedDependency);
                typedDependencyProxy.setGovPos(govPos);
                typedDependencyProxy.setDepPos(depPos);
                featureList.add(typedDependencyProxy);

            }
        }

        return featureList;
    }


    public static void main(String[] args) {
        String sentence = "今天 天气 不 好";
        ParserImplAdapter parserAdapter = new ParserImplAdapter();
        parserAdapter.featureExtract(sentence);
    }

}
