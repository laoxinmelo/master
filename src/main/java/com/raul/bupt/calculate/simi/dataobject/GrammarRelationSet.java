package com.raul.bupt.calculate.simi.dataobject;

import com.raul.bupt.parser.dataobject.RelationDO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/3/22.
 */
public class GrammarRelationSet {

    //语料中所有语义关系所对应的数据对象
    public Map<String,HashMap<String,Integer>> relationMap;
    //语料中的所有词
    public Set<String> wordSet;

    public GrammarRelationSet( Map<String,HashMap<String,Integer>> relationMap,Set<String> wordSet) {

        this.relationMap = relationMap;
        this.wordSet = wordSet;
    }
}
