package com.pop.spring.framework.context;

import com.pop.spring.framework.annotation.Autowried;
import com.pop.spring.framework.annotation.Controller;
import com.pop.spring.framework.annotation.Service;
import com.pop.spring.framework.beans.BeanDefinition;
import com.pop.spring.framework.beans.BeanPostProcessor;
import com.pop.spring.framework.beans.BeanWrapper;
import com.pop.spring.framework.core.BeanFactory;
import com.pop.spring.framework.support.BeanDefinitionReader;
import com.pop.spring.framework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Pop
 * @date 2019/2/12 18:32
 */
public class ClassPathXmlApplicationContext implements BeanFactory {

    private String[] configLocations;

    //用于保存配置信息
    private Map<String, BeanDefinition> beanDefinitionMap
            = new ConcurrentHashMap<String, BeanDefinition>();

    //保存单例bean的Map,用来保证注册式单例的容器
    private Map<String,Object> beanCacheMap = new HashMap<String, Object>();

    //用于存储所有被代理过的对象
    private Map<String,BeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, BeanWrapper>();

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
        doAutowirted();
    }

    /**
     * 开始依赖注入
     */
    private void doAutowirted() {

        for(Map.Entry<String,BeanDefinition> bdf:this.beanDefinitionMap.entrySet()){
            String beanName = bdf.getKey();
            if(!bdf.getValue().isLazy()){
                getBean(beanName);
            }else{

            }
        }

    }

    public void populateBean(String beanName,Object instance){

        Class<?> clazz = instance.getClass();

        if(!(clazz.isAnnotationPresent(Controller.class)||
                clazz.isAnnotationPresent(Service.class))){return;}
        Field[] fields=clazz.getDeclaredFields();
        for(Field field : fields){
            if(!field.isAnnotationPresent(Autowried.class)){continue;}
            Autowried autowried=field.getAnnotation(Autowried.class);
            String autoBeanName = autowried.value().trim();
            if("".equals(autoBeanName)){
                autoBeanName = StringUtils.lowerFirstCase(field.getType().getName(),true);//会得到全名
            }
            //这里处理，所拥有的依赖还没有初始化的问题
            if(!this.beanWrapperMap.containsKey(autoBeanName)){
                BeanDefinition bd = this.beanDefinitionMap.get(autoBeanName);
                Object obj = instantionBean(bd);
                BeanWrapper wrapper = new BeanWrapper(obj);
                this.beanWrapperMap.put(autoBeanName,wrapper);
            }
            field.setAccessible(true);
            try {
                /**
                 * 这里存在一个问题，就是，当我get一个action的时候
                 * 这个action所拥有的依赖还没有实例化，这样的话，使用相关
                 * 的方法，会报空指针
                 * 1，这个autoBeanname的名字问题
                 * 2，这个bean还没有实例化的问题
                 */
                field.set(instance,this.beanWrapperMap.get(autoBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
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
            //生成通知的事件
            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();

            //考虑是否是单例的问题
            Object instance = instantionBean(beanDefinition);
            if(null==instance){return null;}

            //在实例初始化之前传入一次 事件
            beanPostProcessor.postProcessBeforeInitialization(instance,beaName);

            //原始被保留，出来的是拥有更多内容的代理
            BeanWrapper beanWrapper = new BeanWrapper(instance);
            beanWrapper.setBeanPostProcessor(beanPostProcessor);
            if(!this.beanWrapperMap.containsKey(beaName)){
                this.beanWrapperMap.put(beaName,beanWrapper);
            }


            //后再传入一次
            beanPostProcessor.postProcessAfterInitialization(instance,beaName);

            populateBean(beaName,instance);//开始注入

            //通过这样，这样就可以保证返回出去的是代理过的非原始的对象
            return this.beanWrapperMap.get(beaName).getWrapperInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /***
     * 返回一个实例
     * @param beanDefinition
     * @return
     */
    private Object instantionBean(BeanDefinition beanDefinition){
        Object instance = null;
        String className = beanDefinition.getBeanClass();
        try {
            //如果是一个单例,因为根据class才能确定一个类是否有实例
            if(this.beanCacheMap.containsKey(className)){
                //如果有，就注册，没有就创建
                instance = this.beanCacheMap.get(className);
            }else{
                Class<?> clazz  = Class.forName(className);
                instance = clazz.newInstance();
                this.beanCacheMap.put(className,instance);
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }

    /**
     * 获得ioc的内容
     * @return
     */
    public String[] getBeanDefinitionNames(){
        return this.beanDefinitionMap.keySet().
                toArray(new String[getBeanDefinitionCount()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }
}
