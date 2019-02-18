package com.pop.spring.framework.aop;

/**
 * @author Pop
 * @date 2019/2/16 22:05
 */
public abstract class DefaultAopProxy implements AopProxy {

    protected Object target;

    protected AopConfig config ;

    public DefaultAopProxy(Object target) {
        this.target = target;
    }

    protected  abstract <T> void doException(T t) throws Exception;
    protected abstract <T> void processBefore(T t) throws Exception;
    protected abstract <T> void processAfter(T t) throws Exception;

    public void setConfig(AopConfig config) {
        this.config = config;
    }

    public Object getProxy(Object instance) {
        // for subclass todo
        return null;
    }
}
