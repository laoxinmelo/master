package com.raul.bupt.parser.dataobject;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/9.
 */
public class WordDO implements Serializable{

    private String word;
    private String pos;
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public WordDO(String word, String pos,int index) {
        this.word = word;
        this.pos = pos;
        this.index = index;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    /**
     * 判断两个WordDO对象是否相等
     * @param wordDO
     * @return
     */
    public boolean equals(WordDO wordDO) {
        return this.getWord().equals(wordDO.getWord()) && this.getPos().equals(wordDO.getPos()) && this.getIndex() == wordDO.getIndex();

    }
}
