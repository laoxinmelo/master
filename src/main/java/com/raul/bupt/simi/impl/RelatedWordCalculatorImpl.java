package com.raul.bupt.simi.impl;

import com.raul.bupt.parser.dataobject.RelationDO;
import com.raul.bupt.simi.RelatedWordCalculator;
import com.raul.bupt.simi.dataobject.GrammarRelationSet;
import com.raul.bupt.simi.dataobject.RelationWord;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
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
    private static final String grammarSavePath = "E:\\Project\\JavaProject\\master\\result\\grammarList";


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



        return null;
    }

    public static void main(String[] args) {
//        for(RelationDO relationDO:relationDOList) {
//            System.out.println(relationDO);
//        }
    }

}
