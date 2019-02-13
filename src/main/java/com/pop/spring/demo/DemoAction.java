package com.pop.spring.demo;


import com.pop.spring.framework.annotation.Autowried;
import com.pop.spring.framework.annotation.Controller;
import com.pop.spring.framework.annotation.RequestMapping;
import com.pop.spring.framework.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Pop
 * @date 2019/2/12 0:01
 */
@Controller
@RequestMapping("/demo")
public class DemoAction {

    @Autowried
    private IDemoService demoService;

    @RequestMapping("/query.json")
    public void query(HttpServletRequest req, HttpServletResponse res,
                      @RequestParam("name") String name){
        String result = demoService.get(name);
        System.out.println(result);
    }

    @RequestMapping("/edit.json")
    public void edit(HttpServletRequest req, HttpServletResponse res,
                     Integer id){

    }
}
