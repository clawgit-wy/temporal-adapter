package com.yutong.safety.common.annotation;

import java.lang.annotation.*;

/**
 * Controller统一拦截切面，排除
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoPointCut {
}
