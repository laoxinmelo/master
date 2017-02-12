package com.raul.bupt.db.impl;

import com.raul.bupt.db.RedisTool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

/**
 * Created by Administrator on 2016/11/8.
 */
public class RedisToolImpl implements RedisTool {

    /**
     * 在redis中：
     * dbIndex=0表示商品id与评论id之间的关系
     * dbIndex=1表示初始评论的缓存
     * dbIndex=2表示追加评论的缓存
     * dbIndex=3表示商家反馈的缓存
     * dbIndex=4表示游戏本对应的隐性情感词
     * dbIndex=5表示游戏本对应的属性特征
     * dbIndex=6表示电子烟对应的隐性情感词
     * dbIndex=7表示电子烟对应的属性特征
     */

    private static final String host = "127.0.0.1";  //本地redis库
    private static final int port = 6379;  //端口号
    private static final int timeout = 10000000;
    //redis操作工具
    private static final Jedis jedis = new Jedis(host,port,timeout);


    /**
     * redis查询功能
     * @param key
     * @return
     */
    public String getValue(int dbIndex,String key) {
        jedis.select(dbIndex);
        return jedis.get(key);
    }

    /**
     * redis设置值的功能
     * @param key
     * @param value
     */
    public void setValue(int dbIndex,String key,Object value) {
        jedis.select(dbIndex); //选择数据库
        jedis.set(key,(String)value);  //在该数据库中插入对应的值
    }

    /**
     * 获取所有的键值
     * @param dbIndex
     */
    public Set getKeys(int dbIndex) {
        jedis.select(dbIndex);
        return jedis.keys("*");
    }

    /**
     * 删除某个键值及其对应的value
     * @param key
     */
    public void deleteKey(int dbIndex,String key) {
        jedis.select(dbIndex);
        jedis.del(key);
    }

    /**
     * 获取数据库的存储数
     * @param dbIndex
     */
    public Long getSize(int dbIndex) {
        jedis.select(dbIndex);
        return jedis.dbSize();
    }

    /**
     * 判断某个键值是否存在
     * @param dbIndex
     * @param key
     * @return
     */
    public boolean exist(int dbIndex,String key) {
        jedis.select(dbIndex);
        return jedis.exists(key);
    }

}
