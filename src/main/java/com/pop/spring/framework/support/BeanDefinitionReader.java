package com.pop.spring.framework.support;

import com.pop.spring.framework.beans.BeanDefinition;
import com.pop.spring.framework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Pop
 * @date 2019/2/12 20:11
 */
public class BeanDefinitionReader {

    private Properties config = new Properties();

    private List<String> registerBeanClasses = new ArrayList<String>();

    private final String SCANNER_PACKAGE = "scanPackage";

    //读取配置文件的类
    public BeanDefinitionReader(String... locations) {

        //类似我们1.0中的读取内容
        if(!(locations.length>0)){return;}
        ClassLoader loader = this.getClass().getClassLoader();
        InputStream is = null;
        for(String s:locations){
            is = loader.getResourceAsStream(s.replace("classpath:",""));
            try {
                config.load(is);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try{if(null!=is){is.close();}}catch (Exception e){e.printStackTrace();}
            }
        }
        //配置文件中需要扫描的
        doScanner(config.getProperty(SCANNER_PACKAGE));
    }

    private void doScanner(String packageName){
        String path = "/"+packageName.replaceAll("\\.","/");
        URL url = this.getClass().getClassLoader().getResource(path);
        if(null==url){ return;}
        File classDir = new File(url.getFile());
        for(File file:classDir.listFiles()){
            if(file.isDirectory()){
                doScanner(packageName+"."+file.getName());
            }else{
                registerBeanClasses.add(packageName+"."+file.getName().replace(".class",""));
            }
        }
    }

    //返回扫描包下面所有包名 doScanner
    public List<String> loadBeanDefinitions(){
        //这里会调用需要扫描包的关键字
        return this.registerBeanClasses;
    }

    /***
     * 向容器注册
     * @param className
     * @return
     */
    public BeanDefinition registerBean(String className){
        if(this.registerBeanClasses.contains(className)){
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClass(className);
            beanDefinition.setFactoryBeanName(StringUtils.lowerFirstCase(className,true));
            return beanDefinition;
        }
        return null;
    }

    private String lowerFirstCase(String resource){
        char[] c = resource.toCharArray();
        c[0]+=32;
        return String.valueOf(c);
    }

    public Properties getConfig() {
        return config;
    }
}
