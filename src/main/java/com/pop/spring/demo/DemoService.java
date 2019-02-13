package com.pop.spring.demo;

import com.pop.spring.framework.annotation.Service;

/**
 * @author Pop
 * @date 2019/2/11 23:56
 */
@Service
public class DemoService implements IDemoService {
    public String get(String name) {
        return "Pop Name is:"+name;
    }
}
