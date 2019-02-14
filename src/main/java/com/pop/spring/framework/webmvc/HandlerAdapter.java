package com.pop.spring.framework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Pop
 * @date 2019/2/14 15:09
 */

/**
 * 动态匹配的内容
 */
public class HandlerAdapter {

    /**
     *
     * @param req 根据参数去动态匹配
     * @param resp 只是为了传递
     * @param handler 含有Controller method pattern
     * @return
     */
    public ModelAndView handle(HttpServletRequest req, HttpServletResponse resp, HandlerMapping handler) {

        //只有当用户传过来的ModelAndView为空的时候，才会new一个默认的
        return null;
    }
}
