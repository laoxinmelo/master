package com.raul.bupt.db_process;

import com.raul.bupt.db.DBTool;
import com.raul.bupt.db.impl.DBToolImpl;
import com.raul.bupt.segment.WordParticiple;
import com.raul.bupt.segment.impl.WordParticipleImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/7.
 */
public class DBProcess {

    //数据库操作工具
    private static final DBTool dbTool = new DBToolImpl();
    //分词工具
    private static final WordParticiple wordParticiple = new WordParticipleImpl();
    //记录上次停止的id...
    private static final int lastTime = 1;

    public static void main(String[] args) {

        String sql = "select reviewId,content from review";
        ResultSet resultSet = dbTool.query(sql);

        HashMap<String,String> reviewMap = new HashMap<String,String>();
        try {
            while (resultSet.next()) {
                String reviewId = resultSet.getString("reviewId");
                String content = resultSet.getString("content");


                reviewMap.put(reviewId,content);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        Set keySet = reviewMap.keySet();
        Iterator iterator = keySet.iterator();

        int count = 0;
        while (iterator.hasNext()) {
            count += 1;

            if(count<lastTime) {
                continue;
            }

            String reviewId = (String)iterator.next();
            String content = reviewMap.get(reviewId);

            String segment = wordParticiple.wordSegment(content,false).replaceAll("\"","_");
            String segmentNoSW = wordParticiple.wordSegmentWithoutStopWord(content);

            String updateSql = String.format("update review set segment=\"%s\" , segmentNoSW=\"%s\" where reviewId=\"%s\"",segment,segmentNoSW,reviewId);
            System.out.println(count + "    " + updateSql);
            dbTool.execute(updateSql);

        }
    }
}
