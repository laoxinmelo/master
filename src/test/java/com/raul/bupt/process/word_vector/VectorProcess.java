package com.raul.bupt.process.word_vector;

import com.raul.bupt.process.Process;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/17.
 */
public class VectorProcess implements Process{

    //名词词频统计结果保存路径
    private static final String freqRecordPath = "result/candAttribute.txt";
    //所提取特征结果的保存路径
    private static final String aspectBasedPath = "result/aspect-based.txt";
    //分隔符
    private static final String split = "\t";
    //候选属性词及其对应的词频
    private static final Map<String,Integer> wordMap = getFeatureWordFreq();


    private static Map<String,Integer> getFeatureWordFreq() {
        Map<String,Integer> wordFreqMap = new HashMap<String,Integer>();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(freqRecordPath)));
            BufferedReader br = new BufferedReader(inputStreamReader);
            String temp = br.readLine();
            while(temp != null) {
                String word = temp.substring(0,temp.lastIndexOf(split));
                Integer freq = Integer.valueOf(temp.substring(temp.lastIndexOf(split)+1));

                wordFreqMap.put(word,freq);
                temp = br.readLine();
            }
            br.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

        return wordFreqMap;
    }

    public static void main(String[] args) throws Exception{
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(aspectBasedPath)));
        BufferedReader br = new BufferedReader(inputStreamReader);
        String temp = br.readLine();

        List<String[]> featureArrayList = new ArrayList<String[]>();

        int maxSize = 0;
        while(temp != null) {
            String[] featureArray = temp.trim().split(split);
            featureArrayList.add(featureArray);
            if(maxSize< featureArray.length) {
                maxSize = featureArray.length;
            }
            temp = br.readLine();
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter("result/featureExtraction.txt"));
        for(int i=0;i<maxSize;i++) {
            String featureStr = "";
            for(int j=0;j<featureArrayList.size();j++) {
                String[] featureArray = featureArrayList.get(j);
                if(featureArray.length > i) {
                    featureStr += featureArray[i] + split + wordMap.get(featureArray[i])  + split;
                }else {
                    featureStr += "" + split + "" + split;
                }
            }
            System.out.println(featureStr);
            bw.write(featureStr + "\r\n");
        }

        bw.flush(); bw.close();
        br.close();

    }



    public void execute() {

    }
}
