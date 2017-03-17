package com.raul.bupt.feature_extract;

import com.raul.bupt.db.DBTool;
import com.raul.bupt.db.RedisTool;
import com.raul.bupt.db.impl.DBToolImpl;
import com.raul.bupt.db.impl.RedisToolImpl;
import com.raul.bupt.parser.adapter.ParserImplAdapter;
import com.raul.bupt.parser.dataobject.RelationDO;
import com.raul.bupt.segment.WordParticiple;
import com.raul.bupt.segment.impl.WordParticipleImpl;

import java.io.*;
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

    //数据库操作工具
//    private static final DBTool dbTool = new DBToolImpl();

    //redis操作工具
    private static final RedisTool redisTool = new RedisToolImpl();
    //语义解析工具
    private static final ParserImplAdapter parserImplAdapter = new ParserImplAdapter();

    //分词工具
//    private static final WordParticiple wordParticiple = new WordParticipleImpl();

    //reviewId的分隔符
    private static final String split = ";";


    public static void main(String[] args) throws Exception{
//        featureExtract4PC();
        grammarExtract4PC();
    }

    /**
     * 提取电子烟商品的属性特征
     * @throws Exception
     */
    public static void featureExtract4Ecigar() throws Exception {

        //读取电子烟评论数据
        InputStreamReader isr = new InputStreamReader(new FileInputStream(new File("corpus/taggerNote.txt")));
        BufferedReader br = new BufferedReader(isr);

        String temp = br.readLine();

        String item = "";
        List<RelationDO> relationDOList = new ArrayList<RelationDO>();

        int count = 1;
        while(temp != null) {
            String itemId = temp.substring(0,temp.indexOf("\t"));
            String content = temp.substring(temp.lastIndexOf("\t")+1);



            if(item.equals(itemId)) {
                try{
                    List tempRelationList = parserImplAdapter.featureExtract(content);
                    relationDOList.addAll(tempRelationList);
                    System.out.println(count + "    " + itemId + "   " + content);
                } catch (OutOfMemoryError e) {
                    System.err.println(content);
                } catch (Exception e) {
                    System.err.println(content);
                }

                count++;
                temp = br.readLine();
            }else if(!item.equals(itemId) || temp == null) {
                if(relationDOList.size() != 0) {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("result/ecigar/" + item));
                    objectOutputStream.writeInt(relationDOList.size());
                    for(RelationDO relationDO : relationDOList) {
                        objectOutputStream.writeObject(relationDO);
                    }
                    objectOutputStream.close();
                    System.out.println("________________________________");
                }

                item = itemId;
                relationDOList = new ArrayList<RelationDO>();

            }
        }

        br.close();

    }


    /**
     * 提取游戏本商品中的所有语义关系
     * @throws Exception
     */
    public static void grammarExtract4PC() throws Exception {

        Set itemIdSet = redisTool.getKeys(0);
        Iterator iterator = itemIdSet.iterator();

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("result/grammarList"));
        int totalCount = 0;

        while(iterator.hasNext()) {
            String itemId = (String) iterator.next();

            String reviewIdSet = redisTool.getValue(0, itemId);
            String[] reviewArray = reviewIdSet.split(split);
            if(reviewArray.length == 0) {
                continue;
            }

            int dbIndex = 1;
            for (String reviewId : reviewArray) {

                String content = redisTool.getValue(dbIndex, reviewId);
                if(content == null) {
                    continue;
                }
                String[] contentArray = content.split("\r\n");
                if (contentArray.length == 3) {
                    try {
                        List relationDOList = parserImplAdapter.allFeatureExtract(contentArray[2]);
                        for(Object relationDO:relationDOList) {
                            objectOutputStream.writeObject(relationDO);
                        }
                        System.out.println(itemId + "   " + reviewId + "    " + (++totalCount));
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        objectOutputStream.close();
    }


    /**
     * 提取游戏本商品的属性特征
     * @throws Exception
     */
    public static void featureExtract4PC() throws Exception{

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
            if(reviewArray.length == 0) {
                continue;
            }

            for (int dbIndex = 1; dbIndex <= 1; dbIndex++) {
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
                        } catch (Exception e) {
                            memoryWriter.write(itemId + "\t" + reviewId + "\t" +  contentArray[2] + "\r\n");
                        }
                    } else {
                           reviewWriter.write(itemId + "\t" + reviewId + "\r\n");
                    }

                    totalCount += 1;
                }
            }

            /**
             * 保存细粒度特征提取结果
             * 存放位置为result/feature
             * 格式为：数量 + RelationDO
             */
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("result/feature/" + itemId));
            objectOutputStream.writeInt(relationDOList.size());
            for(RelationDO relationDO : relationDOList) {
                objectOutputStream.writeObject(relationDO);
            }
            objectOutputStream.close();
            System.out.println("________________________________");
        }

        memoryWriter.close();
        reviewWriter.close();
    }
}
