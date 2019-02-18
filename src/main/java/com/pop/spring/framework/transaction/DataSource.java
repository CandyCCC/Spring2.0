package com.pop.spring.framework.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Pop
 * @date 2019/2/18 14:06
 */
public interface DataSource {
    /**
     * DataSource可以认为是对Connection的封装
     * 支持切换数据库，还有事务的统一化的管理
     * 而Connection底层是对Socket封装
     */
    Connection getConnection() throws SQLException;

    Connection getConnection(String username,String password) throws  SQLException;

    void close() throws SQLException;
}
