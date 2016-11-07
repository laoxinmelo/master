package com.raul.bupt.parser.impl;

import com.raul.bupt.parser.Parser;
import com.raul.bupt.parser.proxy.NLPProxy;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.*;

import java.util.List;

/**
 * Created by Administrator on 2016/11/7.
 */
public class ParserImpl implements Parser {

    //语义解析模型加载工具
    protected static final LexicalizedParser lexicalizedParser = NLPProxy.getLexicalizedParserInstance();
    //语义解析模型，用于词性标注
    protected static final TreebankLanguagePack treebankLanguagePack = NLPProxy.getTreebankLanguagePackInstance(lexicalizedParser);
    //句法依赖解析模型，用于语义依赖分析
    protected static final GrammaticalStructureFactory grammaticalStructureFactory = NLPProxy.getGrammaticalStructureFactoryInstance(treebankLanguagePack);


    /**
     * 对句子进行解析，获取其语义依赖树，用于进行词性标注
     * @param sentence
     * @return
     */
    protected Tree sentenceParse(String sentence) {
        if(sentence == null)  {
            throw new NullPointerException("The Input Sentence is NULL...");
        }

        return lexicalizedParser.parse(sentence);
    }

    /**
     * 对句子中各个词进行词性标注
     * @param sentence
     * @return
     */
    public List<TaggedWord> wordPosTag(String sentence) {

        if(sentence == null)  {
            throw new NullPointerException("The Input Sentence is NULL...");
        }

        Tree depenTree = this.sentenceParse(sentence);
        return depenTree.taggedYield();
    }

    /**
     * 输出句法依赖树
     * @param sentence
     */
    public void grammarTreePrint(String sentence) {
        sentenceParse(sentence).pennPrint();
    }

    /**
     * 获取句子中的所有语义关系
     * @param  sentence
     * @return
     */
    public List<TypedDependency> grammarRelationExtract(String sentence) {
        if(sentence == null) {
            throw new NullPointerException("The Input Sentence is NULL...");
        }
        return grammaticalStructureFactory.newGrammaticalStructure(sentenceParse(sentence)).typedDependenciesCCprocessed();
    }
}
