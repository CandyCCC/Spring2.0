package com.pop.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author Pop
 * @date 2019/2/11 23:52
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default "";
}
