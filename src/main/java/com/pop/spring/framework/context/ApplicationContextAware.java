package com.pop.spring.framework.context;

/**
 * @author Pop
 * @date 2019/2/16 17:19
 */
public interface ApplicationContextAware {
    //一个自动调用的方法
    void setApplicationContext(ClassPathXmlApplicationContext context);

}
