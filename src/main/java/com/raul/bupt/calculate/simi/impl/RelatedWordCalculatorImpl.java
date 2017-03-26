package com.raul.bupt.calculate.simi.impl;

import com.raul.bupt.parser.dataobject.RelationDO;
import com.raul.bupt.calculate.simi.RelatedWordCalculator;
import com.raul.bupt.calculate.simi.dataobject.GrammarRelationSet;
import com.raul.bupt.calculate.simi.dataobject.RelationWord;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/3/22.
 */
public class RelatedWordCalculatorImpl implements RelatedWordCalculator{

    //语料中的所有RelationDO
    private static final GrammarRelationSet grammarRelationSet = getGrammarRelationSet();
    //RelationDO保存路径
    private static final String grammarSavePath = "./result/grammarList";


    /**
     * 获取语料中所对应的wordList,relationList,relationDOList
     *
     * @return
     */
    private static GrammarRelationSet getGrammarRelationSet() {


        try {
            List<RelationDO> relationDOList = new ArrayList<RelationDO>();
            Set<String> wordSet = new HashSet<String>();
            Set<String> relationSet = new HashSet<String>();

            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(grammarSavePath));
            RelationDO relationDO = (RelationDO) objectInputStream.readObject();

            while(relationDO != null) {
                relationDOList.add(relationDO);
                try {
                    relationDO = (RelationDO) objectInputStream.readObject();

                    String relationName = relationDO.getRelationName();
                    relationSet.add(relationName);

                    String depWord = relationDO.getDepWordDO().getWord();
                    String govWord = relationDO.getGovWordDO().getWord();

                    wordSet.add(depWord);
                    wordSet.add(govWord);

                }catch (EOFException e) {
                    break;
                }
            }

            GrammarRelationSet grammarRelationSet = new GrammarRelationSet(relationDOList,wordSet,relationSet);
            return grammarRelationSet;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 计算与之相关的所有RelationWord集合
     *
     * @param word
     * @return
     */
    public List<RelationWord> calculateRelatedWordList(String word) {

        List<RelationDO> relationDOList = grammarRelationSet.relationDOList;
        Set<String> wordSet = grammarRelationSet.wordSet;
        Set<String> relationSet = grammarRelationSet.relationSet;

        if(!wordSet.contains(word)) {
            return null;
        }


        List<RelationWord> relationWordList = new ArrayList<RelationWord>();
        System.out.println(word + ":");

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("./result/relationWord/" + word));


            for (String candidateWord : wordSet) {
                if (candidateWord.equals(word)) {
                    continue;
                }

                for (String relationName : relationSet) {

                    int WordRelationCandidateWord = 0;  //包含了输入词、候选词以及相关语义关系的数量
                    int AnywordRelationAnyword = 0;  //包含了相关语义关系的数量
                    int WordRelationAnyword = 0;  //包含了输入词、相关语义关系的数量
                    int AnywordRelationCandidateWord = 0;  //包含了候选词、相关语义关系的数量

                    for (RelationDO relationDO : relationDOList) {

                        String DORealtionName = relationDO.getRelationName();
                        String DOGovWord = relationDO.getGovWordDO().getWord();
                        String DODepWord = relationDO.getDepWordDO().getWord();

                        if (!DORealtionName.equals(relationName)) {
                            continue;
                        }

                        AnywordRelationAnyword++;

                        if (DOGovWord.equals(word)) {
                            WordRelationAnyword++;
                        }

                        if (DODepWord.equals(candidateWord)) {
                            AnywordRelationCandidateWord++;
                        }

                        if (DOGovWord.equals(word) && DODepWord.equals(candidateWord)) {
                            WordRelationCandidateWord++;
                        }
                    }

                    double numerator = Double.valueOf(WordRelationCandidateWord * AnywordRelationAnyword);
                    double denominator = Double.valueOf(WordRelationAnyword * AnywordRelationCandidateWord);

                    if (denominator > 0) {

                        double value = Math.log(numerator / denominator + 1);
                        if (value > 0) {
                            RelationWord relationWord = new RelationWord(relationName, candidateWord, value);
                            relationWordList.add(relationWord);
                            objectOutputStream.writeObject(relationWord);
                            System.out.println(word + "   " + relationName + " " + candidateWord + "    " + value);
                        }
                    }
                }
            }

            objectOutputStream.flush();  objectOutputStream.close();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("______________________________________");
        return relationWordList;

    }

    public static void main(String[] args) {

        RelatedWordCalculator relatedWordCalculator = new RelatedWordCalculatorImpl();
        Set<String> wordSet = grammarRelationSet.wordSet;
        for(String word:wordSet) {
            relatedWordCalculator.calculateRelatedWordList(word);
        }
    }

}
