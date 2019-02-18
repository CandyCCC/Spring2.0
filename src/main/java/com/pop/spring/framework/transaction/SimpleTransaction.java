package com.pop.spring.framework.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Pop
 * @date 2019/2/18 15:20
 */
public class SimpleTransaction extends DataSourceWrapper implements Transaction{

    Connection connection;
    public SimpleTransaction(Properties config) throws Exception {
        super(config);
        connection = getConnection();
    }


    public void startTransaction() throws SQLException {
        autoCommit(false);
    }

    public void setReadOnly(boolean isReadOnly) throws SQLException {
        this.connection.setReadOnly(isReadOnly);
    }

    public void autoCommit(boolean isCommit) throws SQLException {
        this.connection.setAutoCommit(isCommit);
    }

    public void rollback() throws SQLException {
        this.connection.rollback();
    }

    public void commit() throws SQLException {
        this.connection.commit();
    }

    public void close() throws SQLException {
        if(this.connection!=null&&!(this.connection.isClosed())){
            this.connection.close();
        }
    }
}
