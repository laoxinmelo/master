package com.raul.bupt.db;

import java.util.Set;

/**
 * Created by Administrator on 2016/11/8.
 */
public interface RedisTool {

    /**
     * redis查询功能
     * @param key
     * @return
     */
    public String getValue(int dbIndex,String key);

    /**
     * redis设置值的功能
     * @param key
     * @param value
     */
    public void setValue(int dbIndex,String key,Object value);

    /**
     * 获取所有的键值
     * @param dbIndex
     */
    public Set getKeys(int dbIndex);

    /**
     * 获取数据库的存储数
     * @param dbIndex
     */
    public Long getSize(int dbIndex);

    /**
     * 判断某个键值是否存在
     * @param key
     * @return
     */
    public boolean exist(int deIndex,String key);

    /**
     * 删除某个键值及其对应的value
     * @param deIndex
     * @param key
     */
    public void deleteKey(int deIndex,String key);

}
