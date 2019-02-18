package com.pop.spring.framework.aop;

import com.pop.spring.framework.transaction.SimpleTransaction;

import java.sql.SQLException;

/**
 * @author Pop
 * @date 2019/2/18 15:40
 */
public class DefaultTranCglibProxy extends AopCglibProxy{

    private SimpleTransaction transaction;

    public DefaultTranCglibProxy(Object target) {
        super(target);
    }

    @Override
    protected <T> void doException(T t) {
        if(!(t instanceof Exception)){ return;}
        //从配置文件中取出，相关的traction配置，主要包括哪些异常需要回滚的问题
        //这里假设判断是sqlException我们才回滚
        if(t.getClass()== SQLException.class){
            try {
                transaction.rollback();
                transaction.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected <T> void processBefore(T t) throws Exception{
        transaction.startTransaction();
    }

    @Override
    protected <T> void processAfter(T t) throws Exception{
        transaction.commit();
    }
}
