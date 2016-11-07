package com.raul.bupt.parser.proxy;

import edu.stanford.nlp.trees.TypedDependency;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/7.
 */
public class TypedDependencyProxy implements Serializable{

    private TypedDependency typedDependency; //语义依赖关系
    private String govPos; //第一个词的词性
    private String depPos; //第二个词的词性


    public TypedDependency getTypedDependency() {
        return typedDependency;
    }

    public void setTypedDependency(TypedDependency typedDependency) {
        this.typedDependency = typedDependency;
    }

    public String getGovPos() {
        return govPos;
    }

    public void setGovPos(String govPos) {
        this.govPos = govPos;
    }

    public String getDepPos() {
        return depPos;
    }

    public void setDepPos(String depPos) {
        this.depPos = depPos;
    }

    public String toString() {
        return this.typedDependency.toString();
    }
}
