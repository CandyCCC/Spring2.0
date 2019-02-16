package com.pop.spring;

import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Pop
 * @date 2019/2/15 21:09
 */
public class PatternTest {

    public interface inter{
        String getString();
    }
    public class interImp implements  inter{

        public String getString() {
            return "111";
        }
    }

    public static void main(String[] args) throws Exception {
        //t();
//        Method[] methods = PatternTest.class.getMethods();
//
//        for(Method method:methods){
//            System.out.println(method.toString());
//        }

        Class<?> interClazz = inter.class;
        Class<?> interImpClazz = interImp.class;
        Method method = interClazz.getMethod("getString");
        Method method1 = interImpClazz.getMethod("getString");
        System.out.println(method==method1);

    }

    private static void t() {
        String line ="my nam1 is ￥{name}";
        String line1="<center>my name is ${name}</center>";
        Matcher matcher = matcher(line1);
        while(matcher.find()){
            for(int i=1;i<=matcher.groupCount();i++){
                System.out.println(matcher.group(i));
                line1=line1.replaceAll("\\$\\{"+matcher.group(i)+"\\}","Pop");

            }
        }
        System.out.println(line1);
    }

    private static Matcher matcher(String str){
        Pattern pattern = Pattern.compile("\\$\\{(.+)\\}");
        Matcher matcher = pattern.matcher(str);
        return  matcher;
    }
}
