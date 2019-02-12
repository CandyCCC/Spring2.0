package com.pop.spring.framework.context;

import com.pop.spring.framework.beans.BeanDefinition;
import com.pop.spring.framework.core.BeanFactory;
import com.pop.spring.framework.support.BeanDefinitionReader;
import com.pop.spring.framework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Pop
 * @date 2019/2/12 18:32
 */
public class ClassPathXmlApplicationContext implements BeanFactory {

    private String[] configLocations;
    private Map<String, BeanDefinition> beanDefinitionMap
            = new ConcurrentHashMap<String, BeanDefinition>();
    private BeanDefinitionReader reader;

    public ClassPathXmlApplicationContext(String ...location) {
            this.configLocations = location;
            refresh();
    }

    //实现了refresh
    public void refresh(){
        //定位 reader
        this.reader = new BeanDefinitionReader(configLocations);
        //加载
        List<String> beanDefinitions=reader.loadBeanDefinitions();
        //注册
        doRegister(beanDefinitions);

        //依赖注入（lazy-init=false）
    }

    //注册到map中去
    private void doRegister(List<String> beanDefinitions) {
        if(beanDefinitions.isEmpty()){return;}
        for(String className:beanDefinitions){

            //beanName有三种情况
            /**
             * 1.默认是首字母小写
             * 2.自定义
             * 3.接口注入
             *
             * 这个是指的是注入时候的名字的规范
             */
            try {
                Class<?> clazz = Class.forName(className);

                //如果是一个接口，是不能实例的，用实现类实例
                if(clazz.isInterface()){continue;}
                BeanDefinition beanDefinition = reader.registerBean(className);
                if(null!=beanDefinition){
                    this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
                }

                Class<?>[] interfaces  = clazz.getInterfaces();
                for(Class i:interfaces){
                    this.beanDefinitionMap.put(StringUtils.lowerFirstCase(i.getName(),true),beanDefinition);
                }
                //到此容器初始化完毕
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /***
     * 通过读取BeanDefinition中的信息
     * 通过反射实例返回
     * Spring做法是，不会把最原始的对象放出去，会使用BeanWarpper进行一次包装
     * 装饰器模式
     * 1，保留原来的oop关系
     * 2.方便拓展
     * 也是依赖注入的进入（如果bean没有配置lazyinit）
     * @param beaName
     * @return
     */
    public Object getBean(String beaName) {

        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beaName);
        String clazzName = beanDefinition.getBeanClass();

        try {

            //考虑是否是单例的问题

            Class<?> clazz = Class.forName(clazzName);



        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
