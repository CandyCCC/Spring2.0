package com.pop.spring.framework.beans;

/**
 * @author Pop
 * @date 2019/2/13 17:22
 */

/***
 * 用于做事件监听
 */
public class BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean,String beanName){
        return bean;
    }
    public Object postProcessAfterInitialization(Object bean,String beanName){
        return bean;
    }
}
