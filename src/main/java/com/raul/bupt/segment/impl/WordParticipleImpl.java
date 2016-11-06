package com.raul.bupt.segment.impl;

import com.raul.bupt.segment.WordParticiple;
import kevin.zhang.NLPIR;

import java.io.*;

/**
 * Created by Administrator on 2016/11/6.
 */
public class WordParticipleImpl  implements WordParticiple{

    //分词工具
    private static NLPIR nlpir = new NLPIR();

    /**
     * 对句子进行分词
     * @param sentence
     * @param pos  表示是否需要进行词性标注
     * @return
     */
    public String wordSegment(String sentence,boolean pos) {

        String wordSegmentResult = "";

        try {
            // NLPIR_Init方法第二个参数设置0表示编码为GBK, 1表示UTF8编码(此处结论不够权威)
            if (!NLPIR.NLPIR_Init("./".getBytes("utf-8"), 1)) {
                System.out.println("NLPIR初始化失败...");
            }

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
     * 将新词词典导入到用户词典当中，并进行保存
     * @param filesPath
     */
    public void updateUserDict(String filesPath) {

        File[] files = new File(filesPath).listFiles();

        for(File file : files) {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),"gbk");
                BufferedReader br = new BufferedReader(inputStreamReader);

                String word = br.readLine();
                while(word != null) {
                    word = word.trim();

                    word = "AC米兰";

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


    static final WordParticiple wordParticiple = new WordParticipleImpl();
    public static void main(String[] args) {


        String sentence = "我爱AC米兰...";
        wordParticiple.wordSegment(sentence,false);


    }
}
