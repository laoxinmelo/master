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

    //����˳�ʼ���ķִʹ���
    private NLPIR nlpir = NLPIRProxy.getInstance();
    //����ͣ�ô�
    public static List<String> stopWordList = getStopWordList();
    //ͣ�ôʴ��·��
    private static final String stopWordPath = "./corpus/stopword/����ͣ�ôʱ�.txt";
    //�ʵ���·��
    private static final String dictFilePath = "./corpus/dict/";
    //��������ICTCLAS�еı�ע
    private static final String punctuation = "w";
    //�´ʺ͹ؼ��ʵĶ�Ӧ����
    private static final int wordNum = 10;


    /**
     * �Ծ��ӽ��зִ�
     * @param sentence
     * @param pos  ��ʾ�Ƿ���Ҫ���д��Ա�ע
     * @return
     */
    public String wordSegment(String sentence,boolean pos) {

        if(sentence == null) {
            throw new NullPointerException("The Input Sentence is NULL....");
        }

        sentence = preprocess(sentence);

        String wordSegmentResult = "";

        try {

            //�Դ������ļ����ݵ��ַ����ִ�
            byte [] resBytes = nlpir.NLPIR_ParagraphProcess(sentence.getBytes("utf-8"), 1);
            wordSegmentResult = new String(resBytes, "utf-8").replaceAll(" +", " ").trim();
            wordSegmentResult = delMeanless(wordSegmentResult,pos); //ɾ��һЩ��Ч����

        }catch (UnsupportedEncodingException e) {
            System.err.println("NLPIR File Encoding Error...");
        }
        finally {
            return wordSegmentResult;
        }
    }

    /**
     * ��ȡ����ͣ�ô�
     * @return
     */
     private static List<String> getStopWordList() {
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
     * �������ı�����Ԥ����
     * @param sentence
     * @return
     */
    private String preprocess(String sentence) {
        sentence = sentence.trim().replaceAll("(?m)^[ ��\r\n]+|[ ��]+$","").toLowerCase(); //ȥ����ͷ�ͽ�β�Ŀո񣬲���������ĸת��ΪСд
        sentence = sentence.replaceAll("\\s+", " ").replaceAll("[\r\n]", "��");  //ȥ������Ŀո�ͻ��з�

        try {
            //���б���ת��
            byte[] bytes = sentence.getBytes("utf-8");
            sentence = new String(bytes);
        }catch(UnsupportedEncodingException e) {
        }finally {
            return sentence;
        }
    }

    /**
     * ȥ����Ч���ݣ���ո�Tab����
     * @param sentence
     * @return
     */
    private String delMeanless(String sentence,boolean withPos) {

        String segmentSentence = "";
        for(String wordUnit:sentence.split(" ")) {

            if(!wordUnit.contains("/")) {
                continue;
            }

            String word = wordUnit.substring(0,wordUnit.indexOf("/"));
            String pos = wordUnit.substring(wordUnit.indexOf("/")+1);

            if(withPos) {
                segmentSentence += wordUnit + " ";
            }else {
                segmentSentence += word + " ";
            }
        }
        return segmentSentence.trim();
    }

    /**
     * �Ծ��ӽ��зִʴ���ɾ�����еı����ź�ͣ�ô�
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

            if(!wordUnit.contains("/")) {
                continue;
            }

            String word = wordUnit.substring(0,wordUnit.indexOf("/"));
            String pos = wordUnit.substring(wordUnit.indexOf("/")+1);

            if(stopWordList.contains(word) || pos.startsWith(punctuation)) {
                continue;
            }
            segmentSentenceWithoutStopWord += word + " ";
        }

        return segmentSentenceWithoutStopWord.trim();
    }

    /**
     * ���´ʴʵ䵼�뵽�û��ʵ䵱�У������б���
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
     * ���´ʼ��뵽�û��ʵ��в�����
     * @param wordList
     */
    public void addNewWord(List wordList) {
        for(Object word : wordList) {
                saveNewWord((String) word);
        }
    }

    /**
    * �ҳ������еĹؼ��ʣ������뵽�û��ʵ䵱��
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
                System.out.println("keyWord : " + keyWord);
                saveNewWord(keyWord);
            }

        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    /**
     * �ҳ������еĹؼ��ʣ������뵽�û��ʵ䵱��
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
                System.out.println("newWord : " + newWord);
                saveNewWord(newWord);
            }

        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

    }

    /**
     *
     * @param word
     */
    private void saveNewWord(String word) {
        try {
            byte[] ret = word.toString().getBytes("utf-8");
            nlpir.NLPIR_AddUserWord(ret);
            nlpir.NLPIR_SaveTheUsrDic();
            nlpir.NLPIR_ImportUserDict(ret);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }


    /**
     * ����������
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{

        WordParticiple wordParticiple = new WordParticipleImpl();
        wordParticiple.updateUserDict();
    }



}
