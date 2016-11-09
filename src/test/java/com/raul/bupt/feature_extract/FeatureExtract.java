package com.raul.bupt.feature_extract;

import com.raul.bupt.db.DBTool;
import com.raul.bupt.db.impl.DBToolImpl;
import com.raul.bupt.parser.adapter.ParserImplAdapter;
import com.raul.bupt.segment.WordParticiple;
import com.raul.bupt.segment.impl.WordParticipleImpl;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/7.
 */
public class FeatureExtract {

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
    public static List  getAllItem() {
        String sql = "select itemId from product where id between 3 and 10";
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

        int count = 1;
        for (Object itemId : itemList) {
            itemId = String.valueOf(itemId);
            List featureList = new ArrayList();

            System.out.println(count + "    " + itemId);

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

            if(featureList.size() > 0) {
                //保存各个itemId所对应的featureList
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream("./result/feature/" + itemId);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(featureList);
                    objectOutputStream.close();
                    fileOutputStream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            count++;
        }
    }
}
