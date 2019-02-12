package com.pop.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author Pop
 * @date 2019/2/11 23:51
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowried {
    String value() default "";
}
