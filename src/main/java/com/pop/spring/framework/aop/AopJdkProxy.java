package com.pop.spring.framework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Pop
 * @date 2019/2/16 22:04
 */
public class AopJdkProxy extends DefaultAopProxy implements InvocationHandler {


    public AopJdkProxy(Object target) {
        super(target);
    }

    protected <T> void doException(T t) {

    }

    protected <T> void processBefore(T t) throws Exception {

    }

    protected <T> void processAfter(T t) throws Exception {

    }


    public Object getProxy(Object instance){
        this.target = instance;
        Class<?> clazz  = instance.getClass();
        //如果这个不是一个接口，而是一个对象，并且无接口的话
        return Proxy.newProxyInstance(clazz.getClassLoader(),clazz.getInterfaces(),this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //针对这个问题，我们判断一下，这个
        /***\
         * 我认为，能进这个方法，说明这个被代理的类有方法被代理了
         * 所以我们就简单的判断名字是否存在
         */
        //method.getDeclaringClass()

        Method m = proxy.getClass().getMethod(method.getName(),method.getParameterTypes());
        if(config.contain(m)) {//有一个问题，这里接口的方法和实现类的方法不一致,可能会导致匹配不上
            AopConfig.AopAspect aspect = config.getAopAspect(m);
            aspect.getAdvices()[0].invoke(aspect.getAspect());//参数的问题
            //参数的问题需要扫描一下
        }

        Object object = null;
        try{
            processBefore(this.target);
            object = method.invoke(target,args);
            processAfter(this.target);
        }catch (Exception e){
            doException(e);
        }
        if(config.contain(m)) {
            AopConfig.AopAspect aspect = config.getAopAspect(m);
            aspect.getAdvices()[1].invoke(aspect.getAspect());//参数的问题
            //参数的问题需要扫描一下
        }

        return object;
    }
}
