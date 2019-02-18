package com.pop.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author Pop
 * @date 2019/2/18 16:10
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transaction {
    String value() default "";
}
