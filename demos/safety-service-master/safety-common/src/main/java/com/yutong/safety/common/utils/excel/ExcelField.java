package com.yutong.safety.common.utils.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 任昊翔 on 2017/7/25.
 * excel文件操作，注解形式设置name",width
 */
public class ExcelField {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.TYPE})
    public @interface Excel{
        String name() default "";
        int width() default 50;
        boolean skip() default false;

    }
}
