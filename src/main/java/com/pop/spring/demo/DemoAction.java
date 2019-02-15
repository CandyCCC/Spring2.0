package com.pop.spring.demo;


import com.pop.spring.framework.annotation.Autowried;
import com.pop.spring.framework.annotation.Controller;
import com.pop.spring.framework.annotation.RequestMapping;
import com.pop.spring.framework.annotation.RequestParam;
import com.pop.spring.framework.webmvc.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pop
 * @date 2019/2/12 0:01
 */
@Controller
@RequestMapping("/demo")
public class DemoAction {

    @Autowried
    private IDemoService demoService;

    @RequestMapping("/first.html")
    public ModelAndView goFirstPage(HttpServletRequest req, HttpServletResponse res,
                                    @RequestParam("name") String name){
        Map<String,Object> map = new HashMap();
        map.put("name",name);
        return new ModelAndView("first.html",map);
    }

    @RequestMapping("/query.json")
    public ModelAndView query(HttpServletRequest req, HttpServletResponse res,
                      @RequestParam("name") String name){
        String result = demoService.get(name);
        System.out.println(result);
        return null;
    }

    @RequestMapping("/edit.json")
    public ModelAndView edit(HttpServletRequest req, HttpServletResponse res,
                             Integer id){
        return null;
    }
}
