package com.raul.bupt.lda_pro;

import com.raul.bupt.db.RedisTool;
import com.raul.bupt.db.impl.RedisToolImpl;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/14.
 */
public class ModelApply {

    //redis缓存工具
    private static final RedisTool redisTool = new RedisToolImpl();
    //商家反馈数据的存放db
    private static final int dbIndex = 3;
    //分隔符
    private static final String split = "\r\n";

    /**
     * 获取所有已完成分词及去停的反馈数据
     * @return
     */
    private static String[] getAllReply() {

        Set keys = redisTool.getKeys(dbIndex);
        for(Object key:keys) {
            String reviewId = String.valueOf(key);

            String totalContent = redisTool.getValue(dbIndex,reviewId);
            System.out.println(totalContent.split(split)[1]);
        }

        return null;
    }


    public static void main(String[] args) {

        getAllReply();

    }

}
