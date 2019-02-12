package com.pop.spring.framework.webmvc.servlet;

import com.pop.spring.framework.context.ClassPathXmlApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Pop
 * @date 2019/2/12 18:20
 */
public class DispatchServlet extends HttpServlet {
    private final String CONFIG = "configLocation";
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req,resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext(config.getInitParameter(CONFIG));
    }
}
