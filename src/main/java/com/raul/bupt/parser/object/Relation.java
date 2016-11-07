package com.raul.bupt.parser.object;

/**
 * Created by Administrator on 2016/11/7.
 */
public class Relation {

    private String previousItem;   //第一个词
    private int previousSeq;  //第一个词的出现位置
    private String previousPOS;  //第一个词的的词性
    private String afterItem;  //第二个词
    private int afterSeq;   //第二个词的出现位置
    private String afterPOS;  //第二个词的词性
    private String relation;  //语义关系
    private boolean withNeg;  //是否带有否定含义

    public String getPreviousItem() {
        return previousItem;
    }

    public void setPreviousItem(String previousItem) {
        this.previousItem = previousItem;
    }

    public int getPreviousSeq() {
        return previousSeq;
    }

    public void setPreviousSeq(int previousSeq) {
        this.previousSeq = previousSeq;
    }

    public String getPreviousPOS() {
        return previousPOS;
    }

    public void setPreviousPOS(String previousPOS) {
        this.previousPOS = previousPOS;
    }

    public String getAfterItem() {
        return afterItem;
    }

    public void setAfterItem(String afterItem) {
        this.afterItem = afterItem;
    }

    public int getAfterSeq() {
        return afterSeq;
    }

    public void setAfterSeq(int afterSeq) {
        this.afterSeq = afterSeq;
    }

    public String getAfterPOS() {
        return afterPOS;
    }

    public void setAfterPOS(String afterPOS) {
        this.afterPOS = afterPOS;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public boolean isWithNeg() {
        return withNeg;
    }

    public void setWithNeg(boolean withNeg) {
        this.withNeg = withNeg;
    }
}
