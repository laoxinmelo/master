package com.raul.bupt.calculate.simi.impl;

import com.raul.bupt.parser.dataobject.RelationDO;
import com.raul.bupt.calculate.simi.RelatedWordCalculator;
import com.raul.bupt.calculate.simi.dataobject.GrammarRelationSet;
import com.raul.bupt.calculate.simi.dataobject.RelationWord;

import java.io.*;
import java.util.*;

/**
 * Created by Administrator on 2017/3/22.
 */
public class RelatedWordCalculatorImpl implements RelatedWordCalculator{

    //语料中的所有RelationDO
    private static final GrammarRelationSet grammarRelationSet = getGrammarRelationSet();
    //RelationDO保存路径
    private static final String grammarSavePath = "./result/grammarList";

    private static final String splitIndex = "_";


    /**
     * 获取语料中所对应的wordList,relationList,relationDOList
     *
     * @return
     */
    private static GrammarRelationSet getGrammarRelationSet() {


        try {
            Set<String> wordSet = new HashSet<String>();
            Map<String,HashMap<String,Integer>> relationMap = new HashMap<String,HashMap<String,Integer>>();


            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(grammarSavePath));
            RelationDO relationDO = new RelationDO();

            int count = 0;
            while(relationDO != null) {

                try {
                    relationDO = (RelationDO) objectInputStream.readObject();
                }catch (EOFException e) {
                    break;
                }
                System.out.println((++count) + "    " + relationDO);

                String relationName = relationDO.getRelationName();

                String depWord = relationDO.getDepWordDO().getWord().trim();
                String govWord = relationDO.getGovWordDO().getWord().trim();
                String wordKey = govWord + splitIndex + depWord;

                if(wordKey.endsWith(splitIndex) || wordKey.startsWith(splitIndex)) {
                    continue;
                }

                wordSet.add(depWord);
                wordSet.add(govWord);

                HashMap<String,Integer> wordKeyMap = new HashMap<String,Integer>();
                if(relationMap.containsKey(relationName)) {
                    wordKeyMap = relationMap.get(relationName);
                }

                int freqValue = 0;
                if(wordKeyMap.containsKey(wordKey)) {
                    freqValue = wordKeyMap.get(wordKey);
                }
                wordKeyMap.put(wordKey,freqValue+1);

                relationMap.put(relationName,wordKeyMap);
            }

            GrammarRelationSet grammarRelationSet = new GrammarRelationSet(relationMap,wordSet);
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
    public void calculateRelatedWordList(String word) {

        Map<String,HashMap<String,Integer>> relationMap = grammarRelationSet.relationMap;
        Set<String> wordSet = grammarRelationSet.wordSet;

        if(!wordSet.contains(word)) {
            return ;
        }

        File[] files = new File("result/relationWord").listFiles();
        for(File file:files) {
            if(file.getName().equals(word)) {
                return ;
            }
        }


        List<RelationWord> relationWordList = new ArrayList<RelationWord>();
        System.out.println(word + ":");

        try {

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream( "result/relationWord/" + word));


            for (String candidateWord : wordSet) {
                if (candidateWord.equals(word)) {
                    continue;
                }

                Set<String> relationSet = relationMap.keySet();
                for (String relationName : relationSet) {

                    int WordRelationCandidateWord = 0;  //包含了输入词、候选词以及相关语义关系的数量
                    int AnywordRelationAnyword = 0;  //包含了相关语义关系的数量
                    int WordRelationAnyword = 0;  //包含了输入词、相关语义关系的数量
                    int AnywordRelationCandidateWord = 0;  //包含了候选词、相关语义关系的数量

                    HashMap<String,Integer> wordKeyMap = relationMap.get(relationName);
                    Set<String> wordKeySet = wordKeyMap.keySet();

                    String candidateKey = word + splitIndex + candidateWord;
                    if(!wordKeySet.contains(candidateKey)) {
                        continue;
                    } else {
                        for (String wordKey : wordKeySet) {

                            try {
                                String[] relationDOStringArray = wordKey.split(splitIndex);

                                String DOGovWord = relationDOStringArray[0];
                                String DODepWord = relationDOStringArray[1];
                                int wordKeyFreq = wordKeyMap.get(wordKey);

                                AnywordRelationAnyword += wordKeyFreq;

                                if (DOGovWord.equals(word)) {
                                    WordRelationAnyword += wordKeyFreq;
                                }

                                if (DODepWord.equals(candidateWord)) {
                                    AnywordRelationCandidateWord += wordKeyFreq;
                                }

                                if (DOGovWord.equals(word) && DODepWord.equals(candidateWord)) {
                                    WordRelationCandidateWord += wordKeyFreq;
                                }
                            }catch (Exception e) {
                                System.out.println("Error!" + wordKey);
                            }
                        }

                        double numerator = Double.valueOf(WordRelationCandidateWord * AnywordRelationAnyword);
                        double denominator = Double.valueOf(WordRelationAnyword * AnywordRelationCandidateWord);

                        if (numerator > 0 && denominator > 0) {

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
            }

            objectOutputStream.flush();  objectOutputStream.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("______________________________________");

    }

}