package com.pop.spring.framework.util;

/**
 * @author Pop
 * @date 2019/2/12 21:07
 */
public class StringUtils {

    public static String lowerFirstCase(String resource,boolean isPackage){
        if(isPackage){
            return lowerFirstCase(resource.substring(resource.lastIndexOf("."+1)));
        }else{
            return lowerFirstCase(resource);
        }
    }

    private  static String lowerFirstCase(String resource){
        char[] cs = resource.toCharArray();
        cs[0]+=32;
        return String.valueOf(cs);
    }
}
