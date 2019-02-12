package com.pop.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author Pop
 * @date 2019/2/11 23:52
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default "";
}
