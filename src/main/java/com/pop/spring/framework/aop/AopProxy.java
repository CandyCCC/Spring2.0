package com.pop.spring.framework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Pop
 * @date 2019/2/16 17:26
 */
//默认用jdk动态代理
public interface AopProxy {

     Object getProxy(Object instance);

}
