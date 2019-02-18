package com.pop.spring.framework.transaction;

/**
 * @author Pop
 * @date 2019/2/18 15:19
 */

import java.sql.SQLException;

/**
 * 事务的接口，完成事务的一些封装
 */
public interface Transaction {


    void startTransaction() throws SQLException;
    void setReadOnly(boolean isReadOnly) throws SQLException;
    void autoCommit(boolean isCommit) throws SQLException;
    void rollback() throws SQLException;
    void commit() throws SQLException;
}
