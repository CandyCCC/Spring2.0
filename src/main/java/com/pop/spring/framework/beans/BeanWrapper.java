package com.pop.spring.framework.beans;

import com.pop.spring.framework.annotation.Transaction;
import com.pop.spring.framework.aop.AopConfig;
import com.pop.spring.framework.aop.AopProxy;
import com.pop.spring.framework.aop.AopProxyFactory;
import com.pop.spring.framework.aop.DefaultAopProxy;

/**
 * @author Pop
 * @date 2019/2/12 22:27
 */
public class BeanWrapper implements FactoryBean{

    /**
     * 方便用于以后的aop，这里还有使用观察者模式
     * 在spring中，会有事件响应机制，也会有一个监听
     */
    private BeanPostProcessor beanPostProcessor;

    private DefaultAopProxy aopProxy ;

    public void setAopConfig(AopConfig config){
        aopProxy.setConfig(config);
    }

    public AopProxy getAopProxy() {
        return aopProxy;
    }

    public BeanPostProcessor getBeanPostProcessor() {
        return beanPostProcessor;
    }

    public void setBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessor = beanPostProcessor;
    }

    /**
     * Bean的另一层包装
     */
    private Object wrapperInstance;

    //原生对象，原始的通过反射new出来，存下来
    private Object originalInstance;

    public BeanWrapper(Object instance) {
        //我认为，这地方要判断一下，是否应该用事务去代理，判断一下transaction注解
        aopProxy = isTransactionProxy(instance);
        this.wrapperInstance = aopProxy.getProxy(instance);
        this.originalInstance = instance;
    }

    private DefaultAopProxy isTransactionProxy(Object instance){

        //扫描注解，了解是否拥有这个事务注解，让后注册到某个类中去
        //spring支持这个整个类型都可以事务，而有些只有单个方法支持事务
        //com.pop.spring.framework.context.ClassPathXmlApplicationContext.instantionAopConfig
        Class<?> clazz = instance.getClass();
        if(clazz.isAnnotationPresent(Transaction.class)){
            //这里暂时不考虑，类上设置了事务标签，而方法上也设置了事务标签的问题
            return AopProxyFactory.createTransactionProxy(instance);
        }else{
            return AopProxyFactory.create(instance);
        }

    }

    public Object getWrapperInstance(){
        return wrapperInstance;
    }

    /**
     * 以后就是返回代理后的class
     * 类似$Proxy0
     * @return
     */
    public Class<?> getWrappedClass(){
        return this.wrapperInstance.getClass();
    }
}
