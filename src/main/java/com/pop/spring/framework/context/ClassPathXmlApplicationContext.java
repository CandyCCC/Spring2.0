package com.pop.spring.framework.context;

import com.pop.spring.framework.annotation.Autowried;
import com.pop.spring.framework.annotation.Controller;
import com.pop.spring.framework.annotation.Service;
import com.pop.spring.framework.annotation.Transaction;
import com.pop.spring.framework.aop.AopConfig;
import com.pop.spring.framework.aop.ProxyUtils;
import com.pop.spring.framework.beans.BeanDefinition;
import com.pop.spring.framework.beans.BeanPostProcessor;
import com.pop.spring.framework.beans.BeanWrapper;
import com.pop.spring.framework.core.BeanFactory;
import com.pop.spring.framework.support.BeanDefinitionReader;
import com.pop.spring.framework.util.StringUtils;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author Pop
 * @date 2019/2/12 18:32
 */
public class ClassPathXmlApplicationContext extends  DefaultLisableBeanFactory implements BeanFactory {

    private String[] configLocations;

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
            if(bdf.getValue().isLazy()){
                Object obj=getBean(beanName);
                //CGLIB$CALLBACK_0
                Class<?> clazz = obj.getClass();
//                if(Enhancer.isEnhanced(clazz)){
//                    for(Field field:clazz.getDeclaredFields()){
//                        System.out.println(field.getType()+" "+field.getName());
//                    }
//                }
            }else{

            }
        }

    }

    public void populateBean(String beanName,Object proxy,Object instance) throws Exception{

        //Class<?> clazz1 = instance.getClass();
        Class<?> clazz = instance.getClass();
        //在添加aop版本后，我们所使用的代理对象，并没有注入成功


        //如果不满足注解要求，我们不认为他是一个Controller
        //不能够被注入的对象，所以不处理
        if(!(clazz.isAnnotationPresent(Controller.class)||
                clazz.isAnnotationPresent(Service.class))){return;}
        Field[] fields=clazz.getDeclaredFields();
        for(Field field : fields){
            if(!field.isAnnotationPresent(Autowried.class)){continue;}
            Autowried autowried=field.getAnnotation(Autowried.class);

            //正在开始字段的注入
            String autoBeanName = autowried.value().trim();
            if("".equals(autoBeanName)){
                autoBeanName = StringUtils.lowerFirstCase(field.getType().getName(),true);//会得到全名
            }
            //这里处理，所拥有的依赖还没有初始化的问题
            if(!this.beanWrapperMap.containsKey(autoBeanName)){
                BeanPostProcessor beanPostProcessor = new BeanPostProcessor();
                BeanDefinition bd = this.beanDefinitionMap.get(autoBeanName);
                Object obj = instantionBean(bd);
                BeanWrapper wrapper = new BeanWrapper(obj);
                wrapper.setAopConfig(instantionAopConfig(bd,
                        wrapper.getWrapperInstance()));
                wrapper.setBeanPostProcessor(beanPostProcessor);
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
                field.set(proxy,this.beanWrapperMap.get(autoBeanName).getWrapperInstance());
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

        //把信息取出来
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beaName);
        String clazzName = beanDefinition.getBeanClass();

        try {
            //生成通知的事件
            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();

            //考虑是否是单例的问题，将信息转成实例
            Object instance = instantionBean(beanDefinition);
            if(null==instance){return null;}

            //在实例初始化之前传入一次 事件
            beanPostProcessor.postProcessBeforeInitialization(instance,beaName);

            //原始被保留，出来的是拥有更多内容的代理
            //这里需要做一个初始化动作,主要是为了初始化有关aop的config
            BeanWrapper beanWrapper = new BeanWrapper(instance);
            //调用的时候，这个beanDefinition中定义的是原来的，而不是我们
            //代理出去的，所以在调用的时候，是暂未配值的
            beanWrapper.setAopConfig(instantionAopConfig(beanDefinition,
                    beanWrapper.getWrapperInstance()));
            beanWrapper.setBeanPostProcessor(beanPostProcessor);
            if(!this.beanWrapperMap.containsKey(beaName)){
                this.beanWrapperMap.put(beaName,beanWrapper);
            }


            //后再传入一次
            beanPostProcessor.postProcessAfterInitialization(instance,beaName);

            //以上，我们已经将对象实例化出来，并且原始信息以及保存
            //在wrapper中返回所代理的对象
            BeanWrapper bw = this.beanWrapperMap.get(beaName);
            Object proxy = bw.getWrapperInstance();
            populateBean(beaName,proxy,instance);//开始注入
            //通过这样，这样就可以保证返回出去的是代理过的非原始的对象
            return proxy;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private AopConfig instantionAopConfig(BeanDefinition beanDefinition, Object proxy) throws  Exception {

        //这个方法主要是为了从配置文件中取出有关aop的配置信息，
        //用于增强他们之间的联系
        AopConfig aopConfig = new AopConfig();

        Properties config = this.reader.getConfig();
        String express = config.getProperty("pointCut");//这里匹配到的需要增强的正则表达式
        String[] before = config.getProperty("aspectBefore").split("\\s");
        String[] after = config.getProperty("aspectAfter").split("\\s");

        String beanClass=beanDefinition.getBeanClass();
        Class<?> aspect = Class.forName(before[0]);
        Pattern pattern = Pattern.compile(express);
        Class<?> targetClass = Class.forName(beanClass);
        Class<?> proxyClass = proxy.getClass();
        //这里解释一下，应该使用被代理后的对象，进行方法
        for(Method method:targetClass.getMethods()){
            if(pattern.matcher(method.toString()).matches()){
                //如果匹配成功，我们需要取出代理对象中的相对应的方法，进行存储
                //2019/2/18 单个方法认为可以支持事务，而不是支持整个类拥有失去
                if(method.isAnnotationPresent(Transaction.class)){}//进行注册...
                Method proxyMethod = proxyClass.getMethod(method.getName(),method.getParameterTypes());
                aopConfig.put(proxyMethod,aspect.newInstance(),new Method[]{aspect.getMethod(before[1]),
                aspect.getMethod(after[1])});
            }
        }
        return aopConfig;
    }

    /***
     * 返回一个实例
     * @param beanDefinition
     * @return
     */
    private Object instantionBean(BeanDefinition beanDefinition){
        Object instance = null;
        //这里用了缓存来存储已经实例化的，这里默认是单例
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
