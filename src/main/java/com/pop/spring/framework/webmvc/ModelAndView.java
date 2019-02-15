package com.pop.spring.framework.webmvc;

import com.sun.istack.internal.Nullable;

import java.util.Map;

/**
 * @author Pop
 * @date 2019/2/14 15:05
 */
public class ModelAndView {

    private String viewName;
    private Map<String,?> model;

    public Map<String, ?> getModel() {
        return model;
    }

    public String getViewName() {
        return viewName;
    }

    //仿造spring中的写法
    public ModelAndView(String viewName, @Nullable Map<String,?> model) {
        this.viewName = viewName;
        this.model=model;
    }
}
