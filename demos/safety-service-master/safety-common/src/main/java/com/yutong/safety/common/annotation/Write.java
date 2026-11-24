package com.yutong.safety.common.annotation;

import com.yutong.safety.common.exception.BizException;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * Created by gwcuiyx on 23-07-06.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Transactional(rollbackFor = {Exception.class, BizException.class})
public @interface Write {
}
