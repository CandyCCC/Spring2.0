package com.pop.spring.framework.aop;

import java.lang.reflect.Method;
import java.util.*;
/**
 * @author Pop
 * @date 2019/2/16 18:06
 */
//目标代理对象的一个方法要增强  有前置方法和后置方法
    //配置文件的目的，告诉spring，那些类和哪些方法需要增强，内容是什么
public class AopConfig {
    //如果这个BeandefinitionReader是对扫描包的封装，那么这个就是
    //对配置的表达式的封装，包括切入点的封装
    private Map<Method,AopAspect> points = new HashMap<Method, AopAspect>();

    public void put(Method target,Object aspect,Method[] advices){
        this.points.put(target,new AopAspect(aspect,advices));
    }

    public AopAspect getAopAspect(Method target){
        if(!this.points.containsKey(target)){return null;}
        return  this.points.get(target);
    }

    public boolean contain(Method target){
        return this.points.containsKey(target);
    }

    public class AopAspect{

        private Object aspect;
        private Method[] advices;

        public Object getAspect() {
            return aspect;
        }

        public void setAspect(Object aspect) {
            this.aspect = aspect;
        }

        public Method[] getAdvices() {
            return advices;
        }

        public void setAdvices(Method[] advices) {
            this.advices = advices;
        }

        public AopAspect(Object aspect, Method[] advices) {
            this.aspect = aspect;
            this.advices = advices;
        }
    }
}
