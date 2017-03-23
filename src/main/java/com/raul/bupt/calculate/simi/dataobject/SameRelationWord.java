package com.raul.bupt.calculate.simi.dataobject;

/**
 * 两个词的相关词列表中都包含的DO
 * Created by Administrator on 2017/3/23.
 */
public class SameRelationWord {

    private String relationName;
    private String word;
    private double value1;
    private double value2;

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public double getValue1() {
        return value1;
    }

    public void setValue1(double value1) {
        this.value1 = value1;
    }

    public double getValue2() {
        return value2;
    }

    public void setValue2(double value2) {
        this.value2 = value2;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public SameRelationWord(String relationName,String word,double value1,double value2) {
        this.relationName = relationName;
        this.word = word;
        this.value1 = value1;
        this.value2 = value2;
    }
}
