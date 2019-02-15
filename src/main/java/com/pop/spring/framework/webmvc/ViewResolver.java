package com.pop.spring.framework.webmvc;

/**
 * @author Pop
 * @date 2019/2/14 15:10
 */

import java.io.File;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 1，讲一个静态的文件转换成一个动态的文件
 * 2，根据用户的传送参数不同，产生不同的结果
 * 最终输出字符串，交给resp输出
 */
public class ViewResolver {
    private String viewName;
    private File template;
    //文件名和莫办文件
    public ViewResolver(String viewName, File template) {
        this.template=template;
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public File getTemplate() {
        return template;
    }

    public void setTemplate(File template) {
        this.template = template;
    }

    /**
     * 这方法，将会讲mv变成view
     * @param mv
     * @return
     */
    public String viewResolver(ModelAndView mv)throws  Exception{
        //也就是将文件的变成字符串，进行标签内容的填充
        //修改，最后输出
        StringBuilder result = new StringBuilder();

        RandomAccessFile ra = new RandomAccessFile(this.template,"r");

        String line = null;
        while (null!=(line=ra.readLine())){
            //我们需要定义一下，匹配到标签内容的方法
            Matcher m = matcher(line);
            while (m.find()){
                for(int i=1;i<=m.groupCount();i++){
                    String paramName = m.group(i);//匹配的找出来
                    //然后从保存的mode map取出正确值
                    String replace = "";
                    if(mv.getModel().containsKey(paramName)){
                        //如果没有参数的话，那么替换掉这里面的内容
                        replace = mv.getModel().get(paramName).toString();
                    }
                    //这里注意一下，要接受一下返回的值
                    line=line.replace("${"+paramName+"}",replace);
                }
            }
            result.append(line);
        }
        ra.close();
        return result.toString();
    }

    private Matcher matcher(String str){
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(str);
        return  matcher;
    }
}
