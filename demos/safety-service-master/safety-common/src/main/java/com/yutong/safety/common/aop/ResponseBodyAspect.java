package com.yutong.safety.common.aop;

import com.alibaba.fastjson.JSON;
import com.yutong.monitor.Monitor;
import com.yutong.monitor.entity.MonitorInfo;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gwcuiyx on 23-07-06.
 */
@Aspect
@Component
public class ResponseBodyAspect {
    private static final int TOKEN_LENGTH = 8 ;
    private static final int RES_LENGTH = 300 ;

    private static final String SWAGGER_CLASS="springfox.documentation.swagger";
    private static final String SWAGGER_OAS_CLASS="springfox.documentation.oas";

    private static final String SWAGGER_METHOD_CLASS = "export";

    @Pointcut("(@within(org.springframework.web.bind.annotation.RestController) && @annotation(org.springframework.web.bind.annotation.RequestMapping)) || @annotation(org.springframework.web.bind.annotation.ResponseBody) || @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void responseBody() {
    }

    @Around("responseBody()")
    public Object around(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object result = null;
        long start = System.currentTimeMillis() ;
        String token = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);

        //增加数据采集
        MonitorInfo monitorInfo = new MonitorInfo();
        monitorInfo.setMetric(joinPoint.getTarget().getClass().getSimpleName());
        monitorInfo.setName(signature.getName());

        if(SWAGGER_METHOD_CLASS.indexOf(signature.getMethod().getName()) > -1) {
            try {
                result = joinPoint.proceed(args);
            } catch (BizException e) {
                result = new VO(e.getCode(), e.getMsg());
            } catch (Throwable e) {
                monitorInfo.setSuccess(false);
                LogUtils.error(ResponseEnum.EXCEPTION.getName(), e);
            }finally {
                monitorInfo.setTime(System.currentTimeMillis()-start);
                Monitor.submit(monitorInfo);
            }

            return result;
        }
        String sm = signature.getMethod().getName();
        if(sm.indexOf("exportToExcel") > -1) {
            try {
                result = joinPoint.proceed(args);
            } catch (BizException e) {
                result = new VO(e.getCode(), e.getMsg());
            } catch (Throwable e) {
                LogUtils.error(ResponseEnum.EXCEPTION.getName(), e);
            }
            return result;
        }

        if (signature.getDeclaringType().getName().startsWith(SWAGGER_CLASS)
                || signature.getDeclaringType().getName().startsWith(SWAGGER_OAS_CLASS)) {

            try {
                result = joinPoint.proceed(args);
            } catch (Throwable e) {
                LogUtils.error(ResponseEnum.EXCEPTION.getName(), e);
            }
            return result;
        }

        try {
            printRequestParam(token, signature, args) ;

            if(args.length > 0) {
                for(Object arg : args) {
                    if(arg instanceof Check) {
                        ((Check) arg).check();
                    }
                }
            }
            result = joinPoint.proceed(args);
            if(!(result instanceof VO)) {
                result = new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), result) ;
            }
        } catch (BizException e) {
            result = new VO(e.getCode(), e.getMsg()) ;
        } catch (Throwable e) {
            monitorInfo.setSuccess(false);
            LogUtils.error(ResponseEnum.EXCEPTION.getName(), e);
            result = new VO(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getName()) ;
        } finally {
            monitorInfo.setTime(System.currentTimeMillis()-start);
            Monitor.submit(monitorInfo);
            LogUtils.info("[{}] - 耗时：{}，请求应答：{}", token, System.currentTimeMillis()-start, substring(JsonUtils.toString(result), RES_LENGTH));
        }
        return result;
    }

    private void printRequestParam(String token, MethodSignature signature, Object[] args) {
        List<Object> argList = new ArrayList<>() ;
        for(Object arg : args) {
            if(arg instanceof ServletRequest || arg instanceof ServletResponse) {
                continue;
            } else if(arg instanceof Check) {
                ((Check) arg).check();
            }
            argList.add(arg) ;
        }
        LogUtils.info("[{}] - {}.{} 请求参数：{}", token, signature.getDeclaringType().getSimpleName(), signature.getMethod().getName(), argList.size() > 0 ? JsonUtils.toString(argList) : "");
    }

    private String substring(String str, int length) {
        if(StringUtils.length(str) > length) {
            return str.substring(0, length) ;
        }
        return str ;
    }
}
