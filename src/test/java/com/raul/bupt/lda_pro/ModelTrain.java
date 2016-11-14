package com.raul.bupt.lda_pro;

import com.raul.bupt.jgibblda.EstimatorNew;
import com.raul.bupt.jgibblda.RunEstimation;
import com.raul.bupt.segment.WordParticiple;
import com.raul.bupt.segment.impl.WordParticipleImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/14.
 */
public class ModelTrain {

    //分词工具
    private static final WordParticiple wordParticiple = new WordParticipleImpl();
    //reply文件名
    private static final String reply = "reply";
    //LDA模型构建对象
    private static final EstimatorNew estimator = new EstimatorNew();


    /**
     * 获取所有商家反馈，并进行分词处理（去停用词），将其保存到lda文件夹中...
     * @return
     */
    private static void getReplySegment() {
        String filePath = new String("corpus/pre/");
        List<String> replyList = new ArrayList<String>();

        for(File file:new File(filePath).listFiles()) {

            for(File subFile:file.listFiles()) {
                if(!subFile.getName().contains(reply)) {
                    continue;
                }

                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(subFile),"gbk");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    String temp = bufferedReader.readLine();
                    while(temp != null) {

                        temp = temp.substring(temp.lastIndexOf("\t")+1);
                        String tempWithoutStopWord = wordParticiple.wordSegmentWithoutStopWord(temp);

                        if(!replyList.contains(tempWithoutStopWord)) {
                            replyList.add(tempWithoutStopWord);

                        }

                        temp = bufferedReader.readLine();
                    }
                }catch (FileNotFoundException e) {
                    System.err.println(subFile.getAbsolutePath());
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("lda/articlesTraining.txt"));
            bufferedWriter.write(replyList.size() + "\r\n");
            for(String reply:replyList) {
                bufferedWriter.write(reply + "\r\n");
            }
            bufferedWriter.flush(); bufferedWriter.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        for (int i = 10; i < 15; i++) {
            RunEstimation.modelTraining(estimator, i);
            System.out.println("______________________________________");
        }

    }

}
