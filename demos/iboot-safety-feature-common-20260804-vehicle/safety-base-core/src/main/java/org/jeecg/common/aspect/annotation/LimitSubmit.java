package org.jeecg.common.aspect.annotation;


import java.lang.annotation.*;

/**
 * 限制重复提交
 * @Author kougy
 * @Date 2022年03月31日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LimitSubmit {
    String key() ;
    /**
     * 默认 10s
     */
    int limit() default 10;

    /**
     * 请求完成后 是否一直等待
     * true则等待
     * @return
     */
    boolean needAllWait() default true;
}
/**
使用：
@LimitSubmit(key = "testLimit:%s:#orderId",limit = 10,needAllWait = true)
	%S 代表当前登录人
            #参数 代表从参数中获取，支持多个参数
            生成的redis key： testLimit:e9ca23d68d884d4ebb19d07889727dae:order1123123


1.限制对某个接口的访问，针对所有人 ，则去除%S
2.限制某个人对某个接口的访问，则 %S
3.限制某个人对某个接口的业务参数的访问，则 %S:#参数1:#参数2
 **/