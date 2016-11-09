package com.raul.bupt.db_process;

import com.raul.bupt.db.DBTool;
import com.raul.bupt.db.RedisTool;
import com.raul.bupt.db.impl.DBToolImpl;
import com.raul.bupt.db.impl.RedisToolImpl;
import com.raul.bupt.feature_extract.FeatureExtract;
import com.raul.bupt.segment.WordParticiple;
import com.raul.bupt.segment.impl.WordParticipleImpl;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


/**
 * 用来进行一些数据存储的处理工作
 */


/**
 * Created by Administrator on 2016/11/7.
 */
public class DBProcess {

    //数据库操作工具
    private static final DBTool dbTool = new DBToolImpl();
    //redis操作工具
    private static final RedisTool redisTool = new RedisToolImpl();
    //分词工具
    private static final WordParticiple wordParticiple = new WordParticipleImpl();


    /**
     * 将分词结果存储到redis中(有词性标注)
     * @throws Exception
     */
    public static void RedisSaveSegmentWithPos() throws Exception{

        for(File file : new File("result/temp").listFiles()) {

            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),"utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String temp = bufferedReader.readLine();



            while (temp != null) {

                String reviewId = temp.substring(0, temp.indexOf("\t"));
                String content = temp.substring(temp.indexOf("\t")+1);

                if(file.getName().contains("append")) {
                    String valueArray = redisTool.getValue(2,reviewId);
                    if(valueArray!=null) {
                        valueArray += "\r\n" + content;
                        redisTool.setValue(2,reviewId,valueArray);
                    }
                }

                if(file.getName().contains("review")) {
                    String valueArray = redisTool.getValue(1,reviewId);
                    if(valueArray!=null) {
                        valueArray += "\r\n" + content;
                        redisTool.setValue(2,reviewId,valueArray);
                    }
                }

                if(file.getName().contains("reply")) {
                    String valueArray = redisTool.getValue(3,reviewId);
                    if(valueArray!=null) {
                        valueArray += "\r\n" + content;
                        redisTool.setValue(2,reviewId,valueArray);
                    }
                }



                temp = bufferedReader.readLine();
            }

            bufferedReader.close();
        }

    }


    /**
     * 将分词结果存储到redis中(无词性标注)
     * @param dbIndex
     */
    public static void RedisSave(int dbIndex) {
        List itemList = FeatureExtract.getAllItem();
        int itemNum = 0;

        String tableName = "";
        if(dbIndex == 1) {
            tableName = "review";
        }else if(dbIndex == 2) {
            tableName = "appendreview";
        }else if(dbIndex == 3) {
            tableName = "reply";
        }else {
            return ;
        }

        for (Object itemId : itemList) {
            itemNum += 1;

            String sql = String.format("select reviewId,content from %s where itemId = \"%s\"",tableName,(String)itemId);
            ResultSet resultSet = dbTool.query(sql);

            HashMap<String, String> reviewMap = new HashMap<String, String>();
            String reviewIdSet = "";
            try {
                while (resultSet.next()) {
                    String reviewId = resultSet.getString("reviewId");
                    String content = resultSet.getString("content");

                    reviewIdSet += reviewId + ";";
                    reviewMap.put(reviewId, content);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            if(!reviewIdSet.equals("")) {
                reviewIdSet = reviewIdSet.substring(0, reviewIdSet.lastIndexOf(";"));

                Set keySet = reviewMap.keySet();
                Iterator iterator = keySet.iterator();

                int count = 0;
                while (iterator.hasNext()) {
                    count += 1;

                    String reviewId = (String) iterator.next();
                    String content = reviewMap.get(reviewId);


                    try {
                        String segment = wordParticiple.wordSegment(content, false).replaceAll("\"", "_");
                        String segmentNoSW = wordParticiple.wordSegmentWithoutStopWord(content);

                        String value = segment + "\r\n" + segmentNoSW;

                        redisTool.setValue(dbIndex,reviewId,value);
                        System.out.println(itemNum + "  " + count);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(reviewId + " " + content);
                    }
                }
            }
        }
    }
}
