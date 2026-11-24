package org.jeecg.common.aspect.annotation;

import java.lang.annotation.*;

/**
 * 数据权限组注解，用于标记数据权限配置数组
 *
 * @author zm
 * @version 3.5.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {
    /**菜单code*/
    String permCode() default "";
    /**排序列*/
    String[] orderColumn() default "";
    /**排序规则*/
    String orderRule() default "";
}
