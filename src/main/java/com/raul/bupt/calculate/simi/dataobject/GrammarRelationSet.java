package com.raul.bupt.calculate.simi.dataobject;

import com.raul.bupt.parser.dataobject.RelationDO;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/3/22.
 */
public class GrammarRelationSet {

    //语料中的所有RelationDO
    public List<RelationDO> relationDOList;
    //语料中的所有词
    public Set<String> wordSet;
    //语料中的所有关系
    public Set<String> relationSet;

    public GrammarRelationSet(List<RelationDO> relationDOList,Set<String> wordSet,Set<String> relationSet) {

        this.relationDOList = relationDOList;
        this.wordSet = wordSet;
        this.relationSet = relationSet;
    }
}
