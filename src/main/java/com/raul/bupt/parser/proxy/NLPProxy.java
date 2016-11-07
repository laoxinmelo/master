package com.raul.bupt.parser.proxy;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.TreebankLanguagePack;


/**
 * Created by Administrator on 2016/11/7.
 */
public class NLPProxy {

    private static final String grammarModel = "edu/stanford/nlp/models/lexparser/chinesePCFG.ser.gz"; //需要加载的语义解析模型
    private static final int maxLength = 80;   //所需处理句子的最大长度


    /**
     * 加载所用模型
     * @return
     */
    public static LexicalizedParser getLexicalizedParserInstance() {
        String[] options = {"-maxLength", String.valueOf(maxLength)};
        LexicalizedParser lexicalizedParser = LexicalizedParser.loadModel(grammarModel, options);
        return lexicalizedParser;
    }


    /**
     * 获取语义解析模型,用于词性标注
     * @return
     */
    public static TreebankLanguagePack getTreebankLanguagePackInstance(LexicalizedParser lexicalizedParser) {
        if(lexicalizedParser == null) {
            throw new NullPointerException("NPE...");
        }

        return lexicalizedParser.getOp().langpack();
    }


    /**
     * 获取句法解析模型，用于获取语义关系，进行特征提取
     * @param treebankLanguagePack
     * @return
     */
    public static GrammaticalStructureFactory getGrammaticalStructureFactoryInstance(TreebankLanguagePack treebankLanguagePack) {
        if(treebankLanguagePack == null) {
            throw new NullPointerException("NPE...");
        }

        return treebankLanguagePack.grammaticalStructureFactory();
    }




}
