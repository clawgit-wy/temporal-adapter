package org.jeecg.common.desensitization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段注解 - 反射获取字段时获取标记此注解的字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPermissionField {

    String deCode() default "";
    String deName() default "";
    String[] enumMappings() default {};
}
