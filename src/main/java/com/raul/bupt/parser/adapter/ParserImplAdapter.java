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

        sentence = "电脑 挺 不错 的 ， 用 了 几 天 ， 感觉 还 是 可以 的 ， 游戏 基本 高 特效 不 卡 ， 建议 可以 弄 个 850 或者 60 的 卡 就 更 好 了 ， 虽然 漏 发 了 包 和 膜 ， 不过 又 给 我 补发 了 ， 还 算 诚信 ， 电脑 虽然 大 毛病 没有 但是 小 毛病 不少 ， 比如 触 模板 一 按 就 塌 下去 ， 还有 硬盘 有时候 会 有 响声 ， 定制 的 a 面 图案 掉落 等 ， 我 自己 买 的 彩 膜 贴 了 下 ， 算是 遮丑 吧 ， 总体 还 行 吧 ， 希望 能 用 久 一点 ， 另外 以后 最 好 配 个 说明书 ， 要 不 很 多 按键 不 知道 什么 功能 ， 希望 麦 本本 多 像 大 厂 学习 吧 ， 5分 实在 给 不 了 ， 给 好评 吧 ， 如果 大家 有 问题 可以 去 百度 麦 本本 吧 看 一下 ， 另外 感谢 一下 帮 我 远程 弄 驱动 的 和 送 我 膜 的 客 服";

        List<TaggedWord> posList = this.wordPosTag(sentence); //对句子进行词性标注
        List splitList = sentenceSplit(posList);
        System.out.println(sentence);

        //用来保存包含细粒度特征的语义关系
        List<TypedDependencyProxy> featureList = new ArrayList<TypedDependencyProxy>();
        for(Object split : splitList) {


            Tree depenTree = sentenceParse((String)split);   //获取句子的语义依赖树
            List<TaggedWord> wordPosList = this.wordPosTag(depenTree);  //对句子进行词性标注
            List<TypedDependency> grammarRelationList = this.grammarRelationExtract(depenTree); //获取句子的所有语义关系

            /**
             * 找出句子中所有细粒度特征语义关系
             */
            for (TypedDependency typedDependency : grammarRelationList) {
                GrammaticalRelation grammaticalRelation = typedDependency.reln();

                //获取语义关系的名称
                String grammaticalName = grammaticalRelation.getShortName();
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
