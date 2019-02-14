package com.pop.spring.framework.webmvc.servlet;

import com.pop.spring.demo.DemoAction;
import com.pop.spring.framework.annotation.Controller;
import com.pop.spring.framework.annotation.RequestMapping;
import com.pop.spring.framework.context.ClassPathXmlApplicationContext;
import com.pop.spring.framework.webmvc.HandlerAdapter;
import com.pop.spring.framework.webmvc.HandlerMapping;
import com.pop.spring.framework.webmvc.ModelAndView;
import com.pop.spring.framework.webmvc.ViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Pop
 * @date 2019/2/12 18:20
 */
public class DispatchServlet extends HttpServlet {
    private final String CONFIG = "configLocation";

    //重点设计，含有controller，method，pattern
    private List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>();

    private List<HandlerAdapter> handlerAdapters = new ArrayList<HandlerAdapter>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String url = req.getRequestURI();
        String context = req.getContextPath();
        url = url.replace(context,"").replaceAll("/+","/");



        //请求的处理
        //doDispatch(req,resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        HandlerMapping handler = getHandler(req);

        HandlerAdapter adapter = getHandlerAdapter(handler);

        ModelAndView mv=adapter.handle(req,resp,handler);

        processDispatchResult(resp,mv);

    }

    private void processDispatchResult(HttpServletResponse resp, ModelAndView mv) {
        //调用viewResolver方法 获得视图的字符串
        ViewResolver viewResolver = new ViewResolver("");
        viewResolver.viewResolver(mv);
    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        return null;
    }

    private HandlerMapping getHandler(HttpServletRequest req) {
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext(config.getInitParameter(CONFIG));
        DemoAction a = (DemoAction) context.getBean("demoAction");
        a.query(null,null,"Pop");
        initStrategies(context);
       // System.out.println("11");
    }

    private void initStrategies(ClassPathXmlApplicationContext context) {
        //文件上传解析，如果请求类型是multipart将通过MultipartResolver进行解析
        initMultipartResolver(context);
        //本地化解析
        initLocaleResolver(context);
        //主题解析
        initThemeResolver(context);
        //用于保存Controller中配置的requestMapping和method的对应关系
        initHandlerMappings(context);
        //动态匹配method参数，包括类转换，动态赋值
        initHandlerAdapters(context);
        //异常处理
        initHandlerExceptionResolvers(context);
        //解析获得视图名
        initRequestToViewNameTranslator(context);
        //通过viewResolvers解析逻辑视图获得具体视图
        //静态文件-》动态资源
        initViewResolvers(context);
        //flash映射管理器
        initFlashMapManager(context);


    }

    private void initFlashMapManager(ClassPathXmlApplicationContext context) { }
    private void initThemeResolver(ClassPathXmlApplicationContext context) { }
    private void initLocaleResolver(ClassPathXmlApplicationContext context) { }
    private void initMultipartResolver(ClassPathXmlApplicationContext context) { }
    private void initRequestToViewNameTranslator(ClassPathXmlApplicationContext context) { }
    private void initHandlerExceptionResolvers(ClassPathXmlApplicationContext context) { }


    private void initHandlerAdapters(ClassPathXmlApplicationContext context) {
    }

    private void initHandlerMappings(ClassPathXmlApplicationContext context) {
        //需要获得context中存储的map的内容
        String[] beanNames = context.getBeanDefinitionNames();
        for(String name :beanNames){
            Object instance = context.getBean(name);
            //拿到后，得到所有的字段和注释，完成controller与menthod
            Class<?> clazz = instance.getClass();
            if(!clazz.isAnnotationPresent(Controller.class)){continue;}
            String beanUrl="";
            if(clazz.isAnnotationPresent(RequestMapping.class)){
                beanUrl = clazz.getAnnotation(RequestMapping.class).value();
            }

            //然后再遍历他的方法
            for(Method method:clazz.getMethods()){
                if(!method.isAnnotationPresent(RequestMapping.class)){continue;}
                String url = method.getAnnotation(RequestMapping.class).value();
                String regex = "/"+beanUrl+url.replaceAll("/+","/");
                Pattern pattern = Pattern.compile(regex);
                this.handlerMappings.add(new HandlerMapping(pattern,instance,method));
            }
        }
    }

    private void initViewResolvers(ClassPathXmlApplicationContext context) {
    }


}
