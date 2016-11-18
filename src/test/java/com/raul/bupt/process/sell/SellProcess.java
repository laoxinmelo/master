package com.raul.bupt.process.sell;

import com.raul.bupt.db.DBTool;
import com.raul.bupt.db.RedisTool;
import com.raul.bupt.db.impl.DBToolImpl;
import com.raul.bupt.db.impl.RedisToolImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/18.
 */
public class SellProcess {

    //数据库操作工具
    private static final DBTool dbTool = new DBToolImpl();
    //redis操作工具
    private static final RedisTool redisTool = new RedisToolImpl();

    public static void main(String[] args) throws IOException{

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("result/sale.txt")));

        Set<String> itemIdSet = redisTool.getKeys(0);
        for(String itemId :itemIdSet) {
            try {
                String sql = String.format("select price,tradeNum from product where itemId = \"%s\"",itemId);
                ResultSet resultSet = dbTool.query(sql);
                while (resultSet.next()) {
                    float price = resultSet.getFloat("price");
                    int tradeNum = resultSet.getInt("tradeNum");

                    System.out.println(itemId + "   " + price + "   " + tradeNum);
                    bw.write(itemId + "\t" + price + "\t" + tradeNum + "\r\n");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        bw.flush();  bw.close();

    }
}
