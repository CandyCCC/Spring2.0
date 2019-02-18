package com.pop.spring.framework.transaction;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Pop
 * @date 2019/2/18 14:55
 */
public abstract class DataSourceWrapper implements  DataSource {

    private Driver driver;
    private String jdbcURL;
    private String userName;
    private String password;
    private String driverClassName;

    private Properties config;

    public DataSourceWrapper(Properties config) throws Exception{
        this.config = config;
        this.jdbcURL = config.getProperty("jdbcURL");
        this.userName= config.getProperty("userName");
        this.password=config.getProperty("password");
        this.driverClassName=config.getProperty("driverClassName");
        getDriver();
    }

    private void getDriver() throws  Exception{
        //get the classloader
        ClassLoader cl;
        SecurityManager sm = System.getSecurityManager();
        if (sm == null) {
            cl = Thread.currentThread().getContextClassLoader();
        } else {
            cl = java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction<ClassLoader>() {
                        public ClassLoader run() {
                            return Thread.currentThread().getContextClassLoader();
                        }
                    });
        }
        //done getting classloader

        Object instance = Class.forName(driverClassName, true, cl).newInstance();
        if (instance instanceof Driver) {
            driver = (Driver) instance;
        }
    }

    public Connection getConnection() throws SQLException {
        Connection connection = null;
        if(driver!=null){
            connection = driver.connect(jdbcURL,config);
        }
        if(connection==null){
            if(userName!=null){
                connection = DriverManager.getConnection(jdbcURL,userName,password);
            }else{
                connection = DriverManager.getConnection(jdbcURL);
            }
        }
        return connection;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        if(this.userName==null){
            this.userName = username;
        }
        if(this.password==null){
            this.password =password;
        }
        return getConnection();
    }

}
