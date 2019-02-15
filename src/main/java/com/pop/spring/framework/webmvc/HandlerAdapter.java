package com.pop.spring.framework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Pop
 * @date 2019/2/14 15:09
 */

/**
 * 动态匹配的内容
 */
public class HandlerAdapter {

    Map<String,Integer> paramMapping;
    public HandlerAdapter( Map<String,Integer> paramMapping) {
        this.paramMapping=paramMapping;
    }

    /**
     *
     * @param req 根据参数去动态匹配
     * @param resp 只是为了传递
     * @param handler 含有Controller method pattern
     * @return
     */
    public ModelAndView handle(HttpServletRequest req, HttpServletResponse resp, HandlerMapping handler)
    throws  Exception{

        //根据用户请求的参数信息，和method的参数机械能匹配

        //只有当用户传过来的ModelAndView为空的时候，才会new一个默认的
        //这个方法会最后调用我们和路径映射的那个方法
        //参数的个数，类型，顺序，方法名
        //1,准备好这个方法的形参列表
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();
        //2 拿到自定义的命名参数的位置
        //用户用url得到的
        Map<String,String[]> reqParameterMap = req.getParameterMap();
        //3 构造实参列表,将来要按照这个顺序填充具体数值
        Object[] paramValues = new Object[parameterTypes.length];
        for(Map.Entry<String,String[]> paramMap:reqParameterMap.entrySet()){
            String value = Arrays.toString(paramMap.getValue()).replaceAll("\\[|\\]","");
            if(!this.paramMapping.containsKey(paramMap.getKey())){continue;}
            int index =this.paramMapping.get(paramMap.getKey());
            //存在类型的转换问题，因为页面上的都是String，实际上参数有基本类型和引用类型
            paramValues[index] = caseStringValue(value,parameterTypes[index]);
        }
        if(this.paramMapping.containsKey(HttpServletRequest.class)){
            int reqIndex = this.paramMapping.get(HttpServletRequest.class);
            paramValues[reqIndex]=req;
        }
        if(this.paramMapping.containsKey(HttpServletResponse.class)){
            int reqsIndex = this.paramMapping.get(HttpServletResponse.class);
            paramValues[reqsIndex]=resp;
        }
        //4 从handler取出controller方法，调用methdo
        Object result = handler.getMethod().invoke(handler.getController(),paramValues);
        if(result==null){return null;}//如果没有返回，也是notFound的问题
       //这个result有两种情况，第一种是modelAndView，还有一个是String
        boolean isMV=handler.getMethod().getReturnType()==ModelAndView.class;
        if(isMV){
            return (ModelAndView) result;
        }else {
            return null;
        }
    }

    private Object caseStringValue(String value,Class<?> clazz){
        if(clazz==String.class){
            return value;
        }else if(clazz==Integer.class){
            return Integer.valueOf(value);
        }else{
            return null;
        }
    }
}
