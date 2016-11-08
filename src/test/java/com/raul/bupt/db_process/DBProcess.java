package com.raul.bupt.db_process;

import com.raul.bupt.db.DBTool;
import com.raul.bupt.db.RedisTool;
import com.raul.bupt.db.impl.DBToolImpl;
import com.raul.bupt.db.impl.RedisToolImpl;
import com.raul.bupt.segment.WordParticiple;
import com.raul.bupt.segment.impl.WordParticipleImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
     * 获取所有ItemId
     * @return
     */
    private static List  getAllItem() {
        String sql = "select itemId from product where id";
        List<String> itemList = new ArrayList<String>();

        ResultSet resultSet = dbTool.query(sql);
        try {
            while (resultSet.next()) {
                String itemId = resultSet.getString("itemId");
                if(!itemList.contains(itemId)) {
                    itemList.add(itemId);
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            return itemList;
        }
    }


    public static void main(String[] args) {

        List itemList = getAllItem();
        int itemNum = 0;
        for (Object itemId : itemList) {
            itemNum += 1;

            String sql = String.format("select reviewId,content from reply where itemId = \"%s\"",(String)itemId);
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

//                redisTool.setValue(0,(String) itemId,reviewIdSet);

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

                        redisTool.setValue(3,reviewId,value);
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
