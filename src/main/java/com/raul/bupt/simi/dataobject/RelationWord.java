package com.raul.bupt.simi.dataobject;

/**
 * Created by Administrator on 2017/3/22.
 */
public class RelationWord {

    private String relationName;  //语义关系名称

    private String word;  //构成语义关系的词

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
