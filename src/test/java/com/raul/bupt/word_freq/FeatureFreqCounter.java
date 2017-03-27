package com.raul.bupt.word_freq;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/20.
 */
public class FeatureFreqCounter {

    //词频结果保存路径
    private static final String FREQ_PATH = "E:\\Project\\JavaProject\\master\\result\\ecigar\\result\\candAttribute.txt";

    //特征结果保存路径
    private static final String FEATURE_PATH = "E:\\Project\\JavaProject\\master\\result\\ecigar\\featureList.txt";

    private static final String TEMP_SAVE_PATH = "E:\\Project\\JavaProject\\master\\result\\ecigar\\";

    private static final String TAB_SPLIT = "\t";

    /**
     * 计算各个特征维度下特征词的保存结果
     *
     * @param featureList
     * @param wordFreqMap
     */
    private static void featureFreqCalculate(List<String[]> featureList,Map<String,String> wordFreqMap) {

        for(int i=0;i<featureList.size();i++) {

            String[] featureArray = featureList.get(i);
            String startLine = "Clsuter_" + (i+1) + TAB_SPLIT + "NNFreq" + TAB_SPLIT + "WordFreq";

            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(TEMP_SAVE_PATH + "feature_" + (i+1) + ".txt"));
                bufferedWriter.write(startLine + "\r\n");

                for(String featureWord:featureArray) {
                    String freqStr = wordFreqMap.get(featureWord);
                    bufferedWriter.write(featureWord + TAB_SPLIT + freqStr + "\r\n");
                }

                bufferedWriter.flush(); bufferedWriter.close();

            }catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取词频统计结果
     *
     * @return
     */
    private static Map<String,String> getFreqList() {
        try {

            Map<String,String> wordFreqMap = new HashMap<String,String>();

            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(FREQ_PATH)));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String temp = bufferedReader.readLine();
            while(temp != null) {
                if(temp.contains(TAB_SPLIT)) {

                    String word = temp.substring(0,temp.indexOf(TAB_SPLIT));
                    String freqStr = temp.substring(temp.indexOf(TAB_SPLIT)+1);

                    wordFreqMap.put(word,freqStr);
                }

                temp = bufferedReader.readLine();
            }

            return wordFreqMap;

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    /**
     * 获取各个特征维度
     *
     * @return
     */
    private static List<String[]> getFeatureList() {

        try {

            List<String[]> featureList = new ArrayList<String[]>();

            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(FEATURE_PATH)));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String temp = bufferedReader.readLine();
            while(temp != null) {
                if(temp.contains(TAB_SPLIT)) {

                    String[] featureArray = temp.split(TAB_SPLIT);
                    featureList.add(featureArray);
                }

                temp = bufferedReader.readLine();
            }

            return featureList;

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


    public static void main(String[] args) {

        List<String[]> featureList = getFeatureList();
        Map<String,String> wordFreqMap = getFreqList();


        featureFreqCalculate(featureList,wordFreqMap);

    }
}
