package com.pop.spring.framework.aop;

/**
 * @author Pop
 * @date 2019/2/16 22:18
 */
public class AopProxyFactory {

    public static DefaultAopProxy create(Object target){

        //判断一下，这个对象是否有接口，没有的话，就生成cg
        Class<?> clazz = target.getClass();
        if(clazz.getInterfaces().length>0){
            return new AopJdkProxy(target);
        }else{
            return new AopCglibProxy(target);
        }

    }

}
