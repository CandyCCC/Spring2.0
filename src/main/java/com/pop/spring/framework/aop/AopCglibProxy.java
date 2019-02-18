package com.pop.spring.framework.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author Pop
 * @date 2019/2/16 22:04
 */
public class AopCglibProxy extends DefaultAopProxy implements MethodInterceptor {

    public AopCglibProxy(Object target) {
        super(target);
    }

    protected <T> void doException(T t) {
        // for subclass
    }

    protected <T> void processBefore(T t) throws Exception {

    }

    protected <T> void processAfter(T t) throws Exception {

    }


    @Override
    public Object getProxy(Object instance) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(instance.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {


        if(config.contain(method)) {
            AopConfig.AopAspect aspect = config.getAopAspect(method);
            aspect.getAdvices()[0].invoke(aspect.getAspect());//参数的问题
            //参数的问题需要扫描一下
        }
        Object object = null;
        try{
            processBefore(o);
            object= methodProxy.invokeSuper(o,objects);
            processAfter(o);
        }catch (Exception e){
            doException(e);
        }

        if(config.contain(method)) {
            AopConfig.AopAspect aspect = config.getAopAspect(method);
            aspect.getAdvices()[1].invoke(aspect.getAspect());//参数的问题
            //参数的问题需要扫描一下
        }

        return object;
    }
}
