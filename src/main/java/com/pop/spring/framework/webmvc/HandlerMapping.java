package com.pop.spring.framework.webmvc;

/**
 * @author Pop
 * @date 2019/2/14 15:08
 */

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * url 与 method的映射
 */
public class HandlerMapping {
    private Object controller;
    private Method method;
    private Pattern pattern;

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public HandlerMapping(Pattern pattern, Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }
}
