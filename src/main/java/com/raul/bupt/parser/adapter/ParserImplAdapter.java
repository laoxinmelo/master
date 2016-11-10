package com.raul.bupt.parser.adapter;

import com.raul.bupt.parser.dataobject.WordDO;
import com.raul.bupt.parser.impl.ParserImpl;
import com.raul.bupt.parser.dataobject.RelationDO;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TypedDependency;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/7.
 */
public class ParserImplAdapter  extends ParserImpl{

    //符合要求的语义关系组
    private static final String[] featureRelationArray = {"amod","nsubj","rcmod"};
    //否定的语义关系
    private static final String negRelation = "neg";
    //无效的关系
    private static final String rootRelation = "root";
    //标点符号
    private static final String punctuation = "PU";
    //word与pos的分隔符
    private static final String splitIndex = "#";
    //否定词词组
    private static final List<String> negWordList = getNegWordList();


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
     * 获取否定词词组
     * @return
     */
    private static List<String> getNegWordList() {
        File file = new File("corpus/neg/Negation.txt");
        List<String> negWordList = new ArrayList<String>();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),"gbk");
            BufferedReader br = new BufferedReader(inputStreamReader);
            String temp = br.readLine();

            while(temp != null) {
                negWordList.add(temp);
                temp = br.readLine();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return negWordList;
    }

    /**
     * 对句子中的各个词进行词性标注
     * @param sentence
     * @return
     */
    @Override
    public List<WordDO> wordPosTag(String sentence) {
        List wordDOList = new ArrayList<WordDO>();

        int index = 1;
        for(String wordUnit : sentence.split(" ")) {
            if(wordUnit.contains(splitIndex)) {
                wordUnit = wordUnit.trim();
                String word = wordUnit.substring(0,wordUnit.lastIndexOf(splitIndex));
                String pos = wordUnit.substring(wordUnit.lastIndexOf(splitIndex)+1);
                WordDO wordDO = new WordDO(word,pos,index);
                wordDOList.add(wordDO);
                index += 1;
            }
        }
        return wordDOList;
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
     * @param wordDOList
     * @return
     */
    private List sentenceSplit(List<WordDO> wordDOList) {

        List splitList = new ArrayList();

        String sentence = "";
        for(WordDO wordDO : wordDOList) {
            String word = wordDO.getWord();
            String pos = wordDO.getPos();

            if(pos.equals(punctuation)) {
                if(!sentence.equals("")) {
                    splitList.add(sentence);
                }
                sentence = "";

            }else {
                sentence += word + "#" + pos + " ";
            }
        }

        if(!sentence.equals("")) {
            splitList.add(sentence);
        }

        return splitList;
    }

    /**
     * 预处理
     * @param sentence
     * @return
     */
    private String preprocess(String sentence) {

        String content = "";
        for(String wordUnit : sentence.trim().split(" ")) {
            content += wordUnit.substring(0,wordUnit.lastIndexOf("#")) + " ";
        }
        return content;
    }

    /**
     * 找出符合要求的语义关系
     * @param sentence
     */
    public List featureExtract(String sentence) {

        List splitList = sentenceSplit(this.wordPosTag(sentence));//对句子进行词性标注

        //用来保存包含细粒度特征的语义关系
        List<RelationDO> featureList = new ArrayList<RelationDO>();
        for(Object split : splitList) {

            List<WordDO> wordPosList = this.wordPosTag((String) split);  //对句子进行词性标注
            List<RelationDO> negList = new ArrayList<RelationDO>(); //包含所有否定关系
            List<RelationDO> splitFeatureList = new ArrayList<RelationDO>();

            String content = preprocess((String) split);
            Tree depenTree = sentenceParse(content);   //获取句子的语义依赖树
            List<TypedDependency> grammarRelationList = this.grammarRelationExtract(depenTree); //获取句子的所有语义关系

            /**
             * 找出句子中所有细粒度特征语义关系
             */
            for (TypedDependency typedDependency : grammarRelationList) {
                GrammaticalRelation grammaticalRelation = typedDependency.reln();

                //获取语义关系的名称
                String grammaticalName = grammaticalRelation.getShortName();
                if ((!containAspect(grammaticalName)) && (!grammaticalName.equals(negRelation))) {
                    continue;
                }

                RelationDO relationDO = new RelationDO();
                relationDO.setRelationName(typedDependency.reln().getShortName());

                TreeGraphNode govNode = typedDependency.gov();
                WordDO govWordDO = wordPosList.get(govNode.index()-1);
                relationDO.setGovWordDO(govWordDO);

                TreeGraphNode depNode = typedDependency.dep();
                WordDO depWordDO = wordPosList.get(depNode.index()-1);
                relationDO.setDepWordDO(depWordDO);

                if(containAspect(grammaticalName)) {
                    splitFeatureList.add(relationDO);
                }else if(negWordList.contains(govWordDO.getWord()) ||
                        negWordList.contains(depWordDO.getWord())){
                    negList.add(relationDO);
                }
            }

            //考虑细粒度特征语义关系中的否定含义
            if(negList.size() != 0) {
                   for(RelationDO relationDO:splitFeatureList) {
                       WordDO govWordDO = relationDO.getGovWordDO();
                       WordDO depWordDO = relationDO.getDepWordDO();

                       for(RelationDO negRelationDO:negList) {
                           WordDO negGovWordDO = negRelationDO.getGovWordDO();
                           WordDO negDepWordDO = negRelationDO.getDepWordDO();

                           if(govWordDO.equals(negGovWordDO) || govWordDO.equals(negDepWordDO) ||
                                   depWordDO.equals(negGovWordDO) || depWordDO.equals(negDepWordDO)) {
                               relationDO.setWithNeg(true);
                           }
                       }
                   }
            }

            featureList.addAll(splitFeatureList);
        }
        return featureList;
    }


    public static void main(String[] args) {
        String sentence = "笔记本#NN 不#AD 好看#VA";
        ParserImplAdapter parserAdapter = new ParserImplAdapter();
        System.out.println(parserAdapter.featureExtract(sentence));
    }

}
