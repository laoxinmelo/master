package com.raul.bupt.others;

import com.raul.bupt.segment.impl.WordParticipleImpl;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/4.
 */
public class WFT {


    //停用词保存路径
    private static final String filePath = "corpus/fencijieguo.txt";

    //停用词词表
    private static final List<String> stopWordList = WordParticipleImpl.stopWordList;

    //分隔符
    private static final String splitIndex = "/";


    /**
     * 获取反馈
     * @return
     * @throws IOException
     */
    private List<String> getFeedback() throws IOException {

        File file = new File(filePath);
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
        BufferedReader br = new BufferedReader(inputStreamReader);

        List<String> feedbackList = new ArrayList<String>();

        String temp = br.readLine();
        while(temp != null) {

            if(!temp.equals("")) {
                String[] wordArray = temp.split(splitIndex);

                String sentence = "";
                for(String word:wordArray) {
                    if(!stopWordList.contains(word)) {
                        sentence += word + " ";
                    }
                }
                feedbackList.add(sentence);
            }
            temp = br.readLine();
        }

        return feedbackList;
    }


    public static void main(String[] args) throws Exception{
        WFT wft = new WFT();

        List<String> feedbackList = wft.getFeedback();

        BufferedWriter bw = new BufferedWriter(new FileWriter("articlesTraining.txt"));
        bw.write(feedbackList.size() + "\r\n");
        for(String feedback:feedbackList) {
            bw.write(feedback + "\r\n");
        }

        bw.flush(); bw.close();
    }

}
