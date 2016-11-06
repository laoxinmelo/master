package com.raul.bupt.segment.impl;

import com.raul.bupt.segment.NLPIRProxy;
import com.raul.bupt.segment.WordParticiple;
import kevin.zhang.NLPIR;

import java.awt.*;
import java.io.*;

/**
 * Created by Administrator on 2016/11/6.
 */
public class WordParticipleImpl  implements WordParticiple{

    //完成了初始化的分词工具
    private NLPIR nlpir = NLPIRProxy.getInstance();
    //获取所有停用词
    private List stopWordList = getStopWordList();


    /**
     * 对句子进行分词
     * @param sentence
     * @param pos  表示是否需要进行词性标注
     * @return
     */
    public String wordSegment(String sentence,boolean pos) {

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
            System.out.println(wordSegmentResult);
            return wordSegmentResult;
        }
    }

    /**
     * 获取所有停用词
     * @return
     */
    private List getStopWordList() {
        return null;
    }

    /**
     * 对输入文本进行预处理
     * @param sentence
     * @return
     */
    private String preprocess(String sentence) {
        sentence = sentence.trim().replaceAll("(?m)^[ 　\r\n]+|[ 　]+$","").toLowerCase(); //去掉开头和结尾的空格，并将所有字母转换为小写
        sentence = sentence.replaceAll("\\s+", " ").replaceAll("[\r\n]", "，");  //去掉多余的空格和换行符

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
        return null;
    }

    /**
     * 将新词词典导入到用户词典当中，并进行保存
     * @param filesPath
     */
    public void updateUserDict(String filesPath) {

        File[] files = new File(filesPath).listFiles();

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
                System.err.println("FileNotFound...");
            }catch (IOException e) {}
        }
    }



    public static void main(String[] args) throws Exception{

        WordParticiple wordParticiple = new WordParticipleImpl();

        String sentence = "卡卡在AC米兰...";
        wordParticiple.wordSegment(sentence,false);


    }
}
