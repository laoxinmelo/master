package com.raul.bupt.feature_extract;

import com.raul.bupt.db.DBTool;
import com.raul.bupt.db.RedisTool;
import com.raul.bupt.db.impl.DBToolImpl;
import com.raul.bupt.db.impl.RedisToolImpl;
import com.raul.bupt.parser.adapter.ParserImplAdapter;
import com.raul.bupt.parser.dataobject.RelationDO;
import com.raul.bupt.segment.WordParticiple;
import com.raul.bupt.segment.impl.WordParticipleImpl;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/7.
 */
public class FeatureExtract {

    //redis操作工具
    private static final RedisTool redisTool = new RedisToolImpl();
    //语义解析工具
    private static final ParserImplAdapter parserImplAdapter = new ParserImplAdapter();
    //分词工具
    private static final WordParticiple wordParticiple = new WordParticipleImpl();
    //reviewId的分隔符
    private static final String split = ";";


    public static void main(String[] args) throws Exception{

        Set itemIdSet = redisTool.getKeys(0);
        Iterator iterator = itemIdSet.iterator();
        int totalCount = 1;

        BufferedWriter memoryWriter = new BufferedWriter(new FileWriter("result/problem/outOfMemory.txt")); //保存无法处理的review
        BufferedWriter reviewWriter = new BufferedWriter(new FileWriter("result/problem/review.txt"));

        while(iterator.hasNext()) {


            String itemId = (String) iterator.next();
            List<RelationDO> relationDOList = new ArrayList<RelationDO>();


            String reviewIdSet = redisTool.getValue(0, itemId);
            String[] reviewArray = reviewIdSet.split(split);

            for (int dbIndex = 1; dbIndex <= 2; dbIndex++) {
                for (String reviewId : reviewArray) {

                    String content = redisTool.getValue(dbIndex, reviewId);
                    if(content == null) {
                        continue;
                    }
                    String[] contentArray = content.split("\r\n");
                    if (contentArray.length == 3) {
                        try {
                            List tempRelationList = parserImplAdapter.featureExtract(contentArray[2]);
                            relationDOList.addAll(tempRelationList);
                            System.out.println(itemId + "   " + reviewId + "    " + totalCount + "  " + dbIndex);
                        } catch (OutOfMemoryError e) {
                            memoryWriter.write(itemId + "\t" + reviewId + "\t" +  contentArray[2] + "\r\n");
                        }
                    } else {
                           reviewWriter.write(itemId + "\t" + reviewId + "\r\n");
                    }

                    totalCount += 1;
                }
            }

            //保存结果
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("result/feature/" + itemId));
            objectOutputStream.writeObject(relationDOList);
            objectOutputStream.close();
            System.out.println("________________________________");
        }

        memoryWriter.close();
        reviewWriter.close();
    }
}
