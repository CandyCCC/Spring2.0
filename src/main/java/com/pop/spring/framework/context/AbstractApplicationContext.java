package com.pop.spring.framework.context;

/**
 * @author Pop
 * @date 2019/2/16 17:12
 */
public abstract class AbstractApplicationContext {

    /**
     * 提供给子类重写的
     */
    protected void onRefresh(){

    }

    protected  abstract void refreshBeanFactory();

}
