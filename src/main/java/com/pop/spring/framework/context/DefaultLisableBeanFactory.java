package com.pop.spring.framework.context;

import com.pop.spring.framework.beans.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Pop
 * @date 2019/2/16 17:15
 */
public class DefaultLisableBeanFactory extends AbstractApplicationContext {

    //用于保存配置信息
    protected Map<String, BeanDefinition> beanDefinitionMap
            = new ConcurrentHashMap<String, BeanDefinition>();

    @Override
    protected void onRefresh() {
        super.onRefresh();
    }

    protected void refreshBeanFactory() {

    }
}
