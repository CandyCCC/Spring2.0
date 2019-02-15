package com.pop.spring.framework.webmvc.servlet;

import com.pop.spring.demo.DemoAction;
import com.pop.spring.framework.annotation.Controller;
import com.pop.spring.framework.annotation.RequestMapping;
import com.pop.spring.framework.annotation.RequestParam;
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
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Pop
 * @date 2019/2/12 18:20
 */
public class DispatchServlet extends HttpServlet {
    private final String CONFIG = "configLocation";

    //重点设计，含有controller，method，pattern
    private List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>();

    private Map<HandlerMapping,HandlerAdapter> handlerAdapters = new HashMap<HandlerMapping, HandlerAdapter>();

    private List<ViewResolver> viewResolvers = new ArrayList<ViewResolver>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

//        String url = req.getRequestURI();
//        String context = req.getContextPath();
//        url = url.replace(context,"").replaceAll("/+","/");
//
//

        //请求的处理

        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception,Details:\r\n"+
                    Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]","")
            .replaceAll("\\s","\r\n"));
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{

        //根据用户请求的url获得一个handler
        HandlerMapping handler = getHandler(req);
        if(handler==null){
            resp.getWriter().write("404 Not Found \r\n by Pop");
            return;
        }

        HandlerAdapter adapter = getHandlerAdapter(handler);

        ModelAndView mv=adapter.handle(req,resp,handler);

        processDispatchResult(resp,mv);

    }

    private void processDispatchResult(HttpServletResponse resp, ModelAndView mv)throws Exception{
        if(null==mv){return;}
        if(this.viewResolvers.isEmpty()){return;}
        for(ViewResolver resolver:this.viewResolvers){
            //这里，文件名，和路径名保持一致，完成输出
            if(!resolver.getViewName().equals(mv.getViewName())){continue;}
            //这里，就可以拿到文件了
            String out = resolver.viewResolver(mv);
            if(out!=null){
                resp.getWriter().write(out);
                break;
            }
        }
    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if(!this.handlerAdapters.containsKey(handler)){return null;}
        return this.handlerAdapters.get(handler);
    }

    private HandlerMapping getHandler(HttpServletRequest req) {
        if(this.handlerMappings.isEmpty()){return null;}
        String url=req.getRequestURI();
        String contextPath=req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");
        for(HandlerMapping handler:this.handlerMappings){
            Matcher matcher=handler.getPattern().matcher(url);
            if(!matcher.matches()){continue;}
            return handler;
        }
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
        //方法的动态配置
        //命名参数，和非命名参数
        //在初始化阶段，我们可以讲参数的位置和类型按一定的记录下来
        //后面使用反射调用的时候，传的参数是一个数组
        //按照位置index，开始对参数赋值
        for(HandlerMapping mapping:this.handlerMappings){
            //每个方法有一个形式参数列表，类型，个数
            Map<String,Integer> paramMapping = new HashMap<String,Integer>();
            Annotation[][] pa = mapping.getMethod().getParameterAnnotations();
            for(int i=0;i<pa.length;i++){//第一层数据表示参数的位置
                for (Annotation a:pa[i]) {
                    if(a instanceof RequestParam){
                        String paramName = ((RequestParam) a).value();
                        if(!"".equals(paramName.trim())){//没有命名的参数
                            paramMapping.put(paramName,i);
                            /**
                             * 这里处理的是有形式参数的，
                             * 就是requestMapping表示的，如果没有写的话
                             * 我们就暂时不处理，但是要是要记录下具体有多少参数
                             * 这些参数在参数列表索引位置，这样，才可以
                             * 正确的调用到响应的方法
                             */
                        }
                    }
                }
            }
            //接下来，我们处理非命名参数
            //只助理Request和response
            Class<?>[] paramTypes = mapping.getMethod().getParameterTypes();
            for(int i=0;i<paramTypes.length;i++){//参数的位置
                Class<?> type = paramTypes[i];
                if(type==HttpServletRequest.class||
                    type==HttpServletResponse.class){
                    paramMapping.put(type.getName(),i);
                }
            }

            this.handlerAdapters.put(mapping,new HandlerAdapter(paramMapping));
        }
    }

    private void initHandlerMappings(ClassPathXmlApplicationContext context) {
        //需要获得context中存储的map的内容
        String[] beanNames = context.getBeanDefinitionNames();
        for(String name :beanNames){
            Object controller = context.getBean(name);
            //拿到后，得到所有的字段和注释，完成controller与menthod
            Class<?> clazz = controller.getClass();
            if(!clazz.isAnnotationPresent(Controller.class)){continue;}
            String beanUrl="";
            if(clazz.isAnnotationPresent(RequestMapping.class)){
                beanUrl = clazz.getAnnotation(RequestMapping.class).value();
            }

            //然后再遍历他的方法
            for(Method method:clazz.getMethods()){
                if(!method.isAnnotationPresent(RequestMapping.class)){continue;}
                String url = method.getAnnotation(RequestMapping.class).value();
                String regex = (beanUrl+url).replaceAll("/+","/");
                Pattern pattern = Pattern.compile(regex);
                this.handlerMappings.add(new HandlerMapping(pattern,controller,method));
                System.out.println("Mapping "+regex+","+method);
            }
        }
    }

    private void initViewResolvers(ClassPathXmlApplicationContext context) {
        //写一个页面的url的时候 http://localhost/firt.html
        //解决页面名字和模版名字关联的问题
        String templateRoot=context.getConfig().getProperty("templateRoot");
        URL url=this.getClass().getClassLoader().getResource(templateRoot);
        File templateRootDir = new File(url.getFile());
        for(File template:templateRootDir.listFiles()){
            if(template.isDirectory()){continue;}
            this.viewResolvers.add(new ViewResolver(template.getName(),template));
        }
    }


}
