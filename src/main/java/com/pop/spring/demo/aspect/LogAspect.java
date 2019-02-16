package com.pop.spring.demo.aspect;

/**
 * @author Pop
 * @date 2019/2/16 17:45
 */
public class LogAspect {

    //在调用某个方法之前，调用这个方法
    public void before(){
        //这里是可以写自己，逻辑的方法
        System.out.println("-----before----");
    }

    public void after(){
        System.out.println("-----after----");
    }

}
