package com.pop.spring.framework.beans;

/**
 * @author Pop
 * @date 2019/2/12 20:13
 */
public class BeanDefinition {
    //这个本来是一个接口

    public String getFactoryBeanName() {
        return FactoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        FactoryBeanName = factoryBeanName;
    }

    public String getBeanClass() {
        return BeanClass;
    }

    public void setBeanClass(String beanClass) {
        BeanClass = beanClass;
    }

    public boolean isLazy() {
        return isLazy;
    }

    public void setLazy(boolean lazy) {
        isLazy = lazy;
    }

    /**
     * beanDefinition是保存配置文件中的信息
     * 内存中的配置
     */
    private String FactoryBeanName;
    private String BeanClass;
    private boolean isLazy=false;
}
