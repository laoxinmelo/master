package com.raul.bupt.parser.dataobject;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/7.
 */
public class RelationDO implements Serializable{

    private String relationName;  //语义关系的类型
    private WordDO govWordDO; //第一个词
    private WordDO depWordDO;  //第二个词
    private boolean withNeg = false; //是否包含否定含义

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public WordDO getGovWordDO() {
        return govWordDO;
    }

    public void setGovWordDO(WordDO govWordDO) {
        this.govWordDO = govWordDO;
    }

    public WordDO getDepWordDO() {
        return depWordDO;
    }

    public void setDepWordDO(WordDO depWordDO) {
        this.depWordDO = depWordDO;
    }

    public boolean isWithNeg() {
        return withNeg;
    }

    public void setWithNeg(boolean withNeg) {
        this.withNeg = withNeg;
    }

    public String toString() {
        return this.relationName + "\t" + this.govWordDO.getWord() + "\t" + this.govWordDO.getPos() + "\t"
                + this.depWordDO.getWord() + "\t" + this.depWordDO.getPos() + "\t" + this.withNeg;
    }


}
