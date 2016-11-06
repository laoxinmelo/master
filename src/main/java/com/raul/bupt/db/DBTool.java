package com.raul.bupt.db;

import java.sql.ResultSet;

/**
 * Created by Administrator on 2016/11/5.
 */
public interface DBTool {

    /**
     * 数据库查询...
     * @param sql
     * @return
     */
    public ResultSet query(String sql);

    /**
     * 数据库中表的更新、插入和删除...
     * @param sql
     */
    public void execute(String sql);

}
