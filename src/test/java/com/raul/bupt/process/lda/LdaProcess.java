package com.raul.bupt.process.lda;

import com.raul.bupt.db.RedisTool;
import com.raul.bupt.db.impl.RedisToolImpl;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/17.
 */
public class LdaProcess{

     //redis缓存工具
     private static final RedisTool redisTool = new RedisToolImpl();


    /**
     * 获取各个商家反馈的主题向量
     * @return
     */
    private static Map<String,ArrayList<Double[]>> getReviewFeedbackMap() throws Exception{

        Map<String,ArrayList<Double[]>> reviewFeedbackMap = new HashMap<String,ArrayList<Double[]>>();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("result/LDApredict.txt"))));
        String temp = br.readLine();

        while(temp != null) {
            String reviewId = temp.substring(0,temp.lastIndexOf("\t"));
            String vectorStr = temp.substring(temp.lastIndexOf("\t")+1);

            String[] vectorStrArray = vectorStr.split(" ");
            Double[] valueArray = new Double[vectorStrArray.length];
            for(int i=0;i<valueArray.length;i++) {
                valueArray[i] = Double.valueOf(vectorStrArray[i]);
            }

            ArrayList<Double[]> vectorList = null;
            if(reviewFeedbackMap.containsKey(reviewId)) {
                vectorList = reviewFeedbackMap.get(reviewId);
            }else {
                vectorList = new ArrayList<Double[]>();
            }
            vectorList.add(valueArray);
            reviewFeedbackMap.put(reviewId,vectorList);

            temp = br.readLine();
        }
        return reviewFeedbackMap;
    }

    public static void main(String[] args) throws Exception{

        Map<String,ArrayList<Double[]>>  reviewFeedbackMap = getReviewFeedbackMap();

        BufferedWriter bw = new BufferedWriter(new FileWriter("result/feedback.txt"));

        Set<String> itemIdSet = redisTool.getKeys(0);
        for(String itemId:itemIdSet) {
            String reviewIdStr = redisTool.getValue(0,itemId);
            Double[] totalValueArray = new Double[15];
            for(int i=0;i<totalValueArray.length;i++) {
                totalValueArray[i] = 0.0;
            }
            int count = 0;
            String[] reviewIdArray = reviewIdStr.split(";");

            for(String reviewId:reviewIdArray) {
                if(reviewFeedbackMap.containsKey(reviewId)) {
                    ArrayList<Double[]> valueList = reviewFeedbackMap.get(reviewId);
                    count += valueList.size();
                    for(Double[] valueArray:valueList) {
                        for(int i=0;i<valueArray.length;i++) {
                            totalValueArray[i] += valueArray[i];
                        }
                    }
                }
            }


            String output = itemId + "\t" + count;
            for(Double value:totalValueArray) {
                output += "\t" + value;
            }

            System.out.println(output);
            bw.write(output + "\r\n");
        }

        bw.flush();  bw.close();
    }

}
