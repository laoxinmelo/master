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

            if(!file.getName().contains("result_")) {
                continue;
            }

            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),"utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String temp = bufferedReader.readLine();

            while (temp != null) {

                String reviewId = temp.substring(0, temp.indexOf("\t"));
                String content = temp.substring(temp.indexOf("\t")+1);

                System.out.println(reviewId);

                if(file.getName().contains("append")) {
                    String value = redisTool.getValue(2,reviewId);
                    if(value!=null) {
                        String[] valueArray = value.split("\r\n");

                        value = valueArray[0] + "\r\n" + valueArray[1] + "\r\n" + content;
                        redisTool.setValue(2,reviewId,value);
                        System.out.println(value);
                        System.out.println("______________________");
                    }
                }

                if(file.getName().contains("review")) {
                    String value = redisTool.getValue(2,reviewId);
                    if(value!=null) {
                        String[] valueArray = value.split("\r\n");

                        value = valueArray[0] + "\r\n" + valueArray[1] + "\r\n" + content;
                        redisTool.setValue(1,reviewId,value);
                        System.out.println(value);
                        System.out.println("______________________");
                    }
                }

                if(file.getName().contains("reply")) {
                    String value = redisTool.getValue(2,reviewId);
                    if(value!=null) {
                        String[] valueArray = value.split("\r\n");

                        value = valueArray[0] + "\r\n" + valueArray[1] + "\r\n" + content;
                        redisTool.setValue(3,reviewId,value);
                        System.out.println(value);
                        System.out.println("______________________");
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
    public static void RedisSave(int dbIndex) throws Exception{
        List itemList = FeatureExtract.getAllItem();
        int itemNum = 0;
        int totalCount = 0;

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

            String sql = String.format("select reviewId,content from %s where itemId = \"%s\"",tableName,itemId);
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

                if(dbIndex == 1) {
                    redisTool.setValue(0, (String) itemId, reviewIdSet);
                }

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

                        if(dbIndex == 3) {
                            redisTool.setValue(dbIndex,String.valueOf(totalCount),value);
                        }else {
                            redisTool.setValue(dbIndex,reviewId,value);
                        }
                        totalCount += 1;

                        System.out.println(totalCount);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(reviewId + " " + content);
                    }
                }
            }
        }
    }

    /**
     * 将没有词性标注的分词结果输出保存在txt文件中
     * 在eclipse中用stanford-postagger进行词性标注
     * stanford-postagger和stanford-parser存在冲突，因此未在此项目中引用
     * @param dbIndex
     */
    public static void RedisOutput(int dbIndex) throws Exception{
        String fileName = "";
        if(dbIndex == 1) {
            fileName = "review";
        }else if(dbIndex == 2) {
            fileName = "append";
        } else if(dbIndex == 3) {
            fileName = "reply";
        }

        Set keySet = redisTool.getKeys(dbIndex);
        Iterator iterator = keySet.iterator();

        BufferedWriter bw = new BufferedWriter(new FileWriter("result/temp/" + fileName + ".txt"));

        while(iterator.hasNext()) {
            String reviewId = (String)iterator.next();
            String content = redisTool.getValue(dbIndex,reviewId).split("\r\n")[0];
            System.out.println(fileName + " " + reviewId + " " + content );

            bw.write(new String((reviewId + "   " + content).getBytes(),"utf-8") + "\r\n");
        }

        bw.flush();  bw.close();
    }

    /**
     * 在所有评论和反馈文本中发现关键词和新词，并导入到用户词典当中
     */
    public static void updateUserDict(String tableName) {
        String sql = String.format("select content from %s",tableName);
        ResultSet resultSet = dbTool.query(sql);

        try {
            while (resultSet.next()) {
                String content = resultSet.getString("content");

                wordParticiple.findKeyWords(content);
                wordParticiple.findNewWords(content);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception{

//        updateUserDict("review");
//        updateUserDict("appendreview");
//        updateUserDict("reply");


        System.out.println(redisTool.getValue(1,"289408028959"));
//        RedisSaveSegmentWithPos();

//        for(int i=3;i<=3;i++) {
//            try {
//
//                 RedisSave(i);
//                 RedisOutput(i);
//            }catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }
}
