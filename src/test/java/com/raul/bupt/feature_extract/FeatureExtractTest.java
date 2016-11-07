package com.raul.bupt.feature_extract;

import com.raul.bupt.db.DBTool;
import com.raul.bupt.db.impl.DBToolImpl;
import com.raul.bupt.parser.adapter.ParserImplAdapter;
import com.raul.bupt.segment.WordParticiple;
import com.raul.bupt.segment.impl.WordParticipleImpl;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/11/7.
 */
public class FeatureExtractTest {

    //数据库操作工具
    private static final DBTool dbTool = new DBToolImpl();
    //语义解析工具
    private static final ParserImplAdapter parserImplAdapter = new ParserImplAdapter();
    //分词工具
    private static final WordParticiple wordParticiple = new WordParticipleImpl();


    /**
     * 获取所有ItemId
     * @return
     */
    private static List  getAllItem() {
        String sql = "select distinct itemId from review";
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

        List itemList = getAllItem();  //获取所有itemId
        HashMap itemMap = new HashMap();  //用来保存

        for (Object itemId : itemList) {
            itemId = String.valueOf(itemId);
            List featureList = new ArrayList();

            String sql = String.format("select content from review where itemId=\"%s\"" ,itemId);
            ResultSet resultSet = dbTool.query(sql);

            try {

                while (resultSet.next()) {
                    String content = resultSet.getString("content");
                    String participleContent = wordParticiple.wordSegment(content, false);
                    List tempFeatureList = parserImplAdapter.featureExtract(participleContent);
                    featureList.addAll(tempFeatureList);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.println(itemId + ":" + featureList);
            itemMap.put(itemId,featureList);
        }
    }
}
