package com.raul.bupt.segment.impl;

import com.raul.bupt.segment.proxy.NLPIRProxy;
import com.raul.bupt.segment.WordParticiple;
import com.sun.deploy.util.StringUtils;
import kevin.zhang.NLPIR;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

/**
 * Created by Administrator on 2016/11/6.
 */
public class WordParticipleImpl  implements WordParticiple{

    //完成了初始化的分词工具
    private NLPIR nlpir = NLPIRProxy.getInstance();
    //所有停用词
    private List<String> stopWordList = getStopWordList();
    //停用词存放路径
    private static final String stopWordPath = "./corpus/stopword/中文停用词表.txt";
    //词典存放路径
    private static final String dictFilePath = "./corpus/dict/";
    //标点符号在ICTCLAS中的标注
    private static final String punctuation = "w";
    //新词和关键词的对应数量
    private static final int wordNum = 10;


    /**
     * 对句子进行分词
     * @param sentence
     * @param pos  表示是否需要进行词性标注
     * @return
     */
    public String wordSegment(String sentence,boolean pos) {

        if(sentence == null) {
            throw new NullPointerException("The Input Sentence is NULL....");
        }

        sentence = preprocess(sentence);

        String wordSegmentResult = "";

        try {
            //判断是否需要词性标注
            int withPos = 0;
            if(pos) {
                withPos = 1;
            }

            //对储存了文件内容的字符串分词
            byte [] resBytes = nlpir.NLPIR_ParagraphProcess(sentence.getBytes("utf-8"), withPos);
            wordSegmentResult = new String(resBytes, "utf-8").replaceAll("\\s+", " ").trim();

        }catch (UnsupportedEncodingException e) {
            System.err.println("NLPIR File Encoding Error...");
        }
        finally {
            return wordSegmentResult;
        }
    }

    /**
     * 获取所有停用词
     * @return
     */
    private List<String> getStopWordList() {
        File file = new File(stopWordPath);
        List<String> stopWordList = new ArrayList<String>();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),"gbk");
            BufferedReader br = new BufferedReader(inputStreamReader);

            String word = br.readLine();
            while(word != null) {
                word = word.trim();
                if(!stopWordList.contains(word)) {
                      stopWordList.add(word);
                }
                word = br.readLine();
            }

        }catch (FileNotFoundException e) {
            System.err.println("FileNotFound...");
        }catch (IOException e) {}
        finally {
            return stopWordList;
        }

    }

    /**
     * 对输入文本进行预处理
     * @param sentence
     * @return
     */
    private String preprocess(String sentence) {
        sentence = sentence.trim().replaceAll("(?m)^[ 　\r\n]+|[ 　]+$","").toLowerCase(); //去掉开头和结尾的空格，并将所有字母转换为小写
        sentence = sentence.replaceAll("\\s+", " ").replaceAll("[\r\n]", "，");  //去掉多余的空格和换行符
        sentence = sentence.replaceAll(" ","，");

        try {
            //进行编码转换
            byte[] bytes = sentence.getBytes("utf-8");
            sentence = new String(bytes);
        }catch(UnsupportedEncodingException e) {
        }finally {
            return sentence;
        }
    }

    /**
     * 对句子进行分词处理，删除其中的标点符号和停用词
     * @param sentence
     * @return
     */
    public String wordSegmentWithoutStopWord(String sentence) {
        if(sentence == null) {
            throw new NullPointerException("The Input Sentence is NULL....");
        }

        String segmentSentence = wordSegment(sentence,true);
        String segmentSentenceWithoutStopWord = "";
        for(String wordUnit:segmentSentence.split(" ")) {
            String word = wordUnit.substring(0,wordUnit.indexOf("/"));
            String pos = wordUnit.substring(wordUnit.indexOf("/")+1);

            if(stopWordList.contains(word) || pos.startsWith(punctuation)) {
                continue;
            }
            segmentSentenceWithoutStopWord += word + " ";
        }

        return segmentSentenceWithoutStopWord;
    }

    /**
     * 将新词词典导入到用户词典当中，并进行保存
     * @param
     */
    public void updateUserDict() {

        File[] files = new File(dictFilePath).listFiles();

        for(File file : files) {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),"utf-8");
                BufferedReader br = new BufferedReader(inputStreamReader);

                String word = br.readLine();
                while(word != null) {
                    word = word.trim();


                    byte[] ret = word.getBytes("utf-8");
                    nlpir.NLPIR_AddUserWord(ret);
                    nlpir.NLPIR_SaveTheUsrDic();
                    nlpir.NLPIR_ImportUserDict(ret);

                    System.out.println(word);
                    word = br.readLine();
                }

            }catch (FileNotFoundException e) {
                System.err.println("File Not Found...");
            }catch (IOException e) {}
        }
    }

    /**
     * 将新词加入到用户词典中并保存
     * @param wordList
     */
    public void addNewWord(List wordList) {
        for(Object word : wordList) {
            try {
                byte[] ret = word.toString().getBytes("utf-8");
                nlpir.NLPIR_AddUserWord(ret);
                nlpir.NLPIR_SaveTheUsrDic();
                nlpir.NLPIR_ImportUserDict(ret);
            }catch (UnsupportedEncodingException e) {
                System.err.println("Encoding Error...");
            }
        }
    }


    /**
     * 测试主函数
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{

        WordParticiple wordParticiple = new WordParticipleImpl();
        wordParticiple.updateUserDict();

    }

    /**
    * 找出句子中的关键词，并导入到用户词典当中
    * @param sentence
    */
    public void findKeyWords(String sentence) {
        try {
            byte[] bytes = nlpir.NLPIR_GetKeyWords(sentence.getBytes("utf-8"), wordNum, false);
            String keyWordStr = new String(bytes).trim().replaceAll("\\s+"," ");
            if(keyWordStr.equals(" ") || keyWordStr.equals("")) {
                return ;
            }

            for(String keyWord:keyWordStr.split(" ")){
                System.out.println(keyWord);
                byte[] ret = keyWord.toString().getBytes("utf-8");
                nlpir.NLPIR_AddUserWord(ret);
                nlpir.NLPIR_SaveTheUsrDic();
                nlpir.NLPIR_ImportUserDict(ret);
            }

        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }


    /**
     * 找出句子中的关键词，并导入到用户词典当中
     * @param sentence
     */
    public void findNewWords (String sentence){
        try {
            byte[] bytes = nlpir.NLPIR_GetNewWords(sentence.getBytes("utf-8"), wordNum, false);
            String newWordStr = new String(bytes).trim().replaceAll("\\s+"," ");

            if(newWordStr.equals(" ") || newWordStr.equals("")) {
                return ;
            }

            for(String newWord:newWordStr.split(" ")){
                System.out.println(newWord);
                byte[] ret = newWord.toString().getBytes("utf-8");
                nlpir.NLPIR_AddUserWord(ret);
                nlpir.NLPIR_SaveTheUsrDic();
                nlpir.NLPIR_ImportUserDict(ret);
            }

        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

    }

}
