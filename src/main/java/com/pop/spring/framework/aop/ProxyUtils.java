package com.pop.spring.framework.aop;

import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @author Pop
 * @date 2019/2/16 22:58
 */
public class ProxyUtils {
    /***
     * 为了返回代理原来的对象
     * @param proxy
     * @return
     * @throws Exception
     */
    public static Object getTargetObject(Object proxy) throws Exception{
        //判断一下，是哪个代理的
        Class<?> clazz = proxy.getClass();
        ProxyType type = findType(clazz);
        Object object=null;
        if(null==type){return  object;}
        return getOriginalObj(proxy,type);
    }

    private static Object getOriginalObj(Object proxy ,ProxyType type) throws  Exception{

        Class<?> clazz = proxy.getClass();
        Object oringinal = null;
        DefaultAopProxy p = null;
        Field proxyField = null;
        Field targetField = null;
        switch (type){
            case JDK:
                proxyField=clazz.getSuperclass().getDeclaredField("h");
                proxyField.setAccessible(true);
                p = (DefaultAopProxy)proxyField.get(proxy);
                //这个target就是我们的原有的AopProxy对象 也就是
                targetField = p.getClass().getSuperclass().getDeclaredField("target");
                targetField.setAccessible(true);
                oringinal = targetField.get(p);
                break;
            case CGLIB:
                proxyField=clazz.getDeclaredField("CGLIB$CALLBACK_0");
                proxyField.setAccessible(true);
                 p = (DefaultAopProxy)proxyField.get(proxy);
                //这个target就是我们的原有的AopProxy对象 也就是
                targetField=p.getClass().getSuperclass().getDeclaredField("target");
                targetField.setAccessible(true);
                oringinal = targetField.get(p);
                break;
            default: break;
        }
        return oringinal;
    }

    private static ProxyType findType(Class clazz){
        if(Proxy.isProxyClass(clazz)){
            return ProxyType.JDK;
        }else if(Enhancer.isEnhanced(clazz)){
            return ProxyType.CGLIB;
        }else{
            return null;
        }
    }
    enum ProxyType{
        CGLIB,JDK
    }
}
