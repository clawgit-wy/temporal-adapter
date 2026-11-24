package com.yutong.safety.web.config;

import com.alibaba.fastjson.JSON;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.SafetyBuildEmpPowerVO;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerLog;
import com.yutong.safety.core.service.build.SafetyEmployeePowerLogService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
public class MapperDbLogAop {
    private final String UPDATE_METHOD_NAME = "updateByExampleSelective";
    private final List<String> SAVE_METHOD_NAME = Arrays.asList("insertBatch", "insertBatchForInit", "updateBatchForCrkInit");

    @Resource
    private SafetyEmployeePowerLogService safetyEmployeePowerLogService;

    // 切点：DbLog
    @Pointcut("@annotation(com.yutong.safety.common.annotation.DbLog)")
    public void mapperPointCut(){}

    @Around("mapperPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // Service 方法-操作数据库的方法
            String serviceMethod = joinPoint.getSignature().toShortString();
            // 【核心改动】获取顶层Controller方法
            String topControllerMethod = getTopControllerCaller();

            // service-入参
            Object[] args = joinPoint.getArgs();
            long start = System.currentTimeMillis();
            Object result = joinPoint.proceed(args);
            long cost = System.currentTimeMillis() - start;
            LogUtils.info("【AOP-DB日志】调用方:{} | Mapper:{} | 参数:{} | 返回:{} | 耗时:{}ms",
                    topControllerMethod, serviceMethod, args, result, cost);
            if (!serviceMethod.contains(UPDATE_METHOD_NAME)) {
                Object arg = args[0];
                String paramJson = JSON.toJSONString(arg);
                List<SafetyBuildEmpPowerVO> list = JsonUtils.parseList(paramJson, SafetyBuildEmpPowerVO.class);
                List<SafetyBuildEmployeePowerLog> saveLog = list.stream().map(li -> {
                    SafetyBuildEmployeePowerLog info = new SafetyBuildEmployeePowerLog();
                    info.setControllerMethod(topControllerMethod);
                    info.setServiceMethod(serviceMethod);
                    info.setEmpNo(li.getEmployeeCode());
                    info.setDeviceCode(li.getDeviceCode());
                    return info;
                }).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(saveLog)) {
                    safetyEmployeePowerLogService.batchInsert(saveLog);
                }
            } else {
                SafetyBuildEmployeePowerLog info = new SafetyBuildEmployeePowerLog();
                info.setControllerMethod(topControllerMethod);
                info.setServiceMethod(serviceMethod);
                info.setRequestParam(Arrays.toString(args));
                safetyEmployeePowerLogService.batchInsert(Collections.singletonList(info));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error("员工权限表操作日志记录失败");
            return "";
        }
    }

    // 同上面MyBatis拦截器的堆栈解析方法
    private String getCallerMethod() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        boolean hitMapper = false;
        for (StackTraceElement el : stack) {
            String cls = el.getClassName();
            if (cls.contains("org.aspectj") || cls.contains("spring")) continue;
            if (hitMapper) return cls + "#" + el.getMethodName();
            if (cls.contains("Mapper")) hitMapper = true;
        }
        return "未知调用";
    }

    /**
     * 遍历完整调用栈，追溯到最顶层Controller方法
     * 规则：包含Controller的类即为接口入口，无Controller则返回最外层Service
     */
    private String getTopControllerCaller() {
        StackTraceElement[] stackArr = Thread.currentThread().getStackTrace();
        String lastController = null;
        String lastService = null;

        // 过滤框架包名
        String[] filterPackages = {
                "org.aspectj", "org.springframework.aop", "org.apache.ibatis",
                "java.lang.Thread", "com.xxx.mapper", "org.mybatis"
        };

        for (StackTraceElement stack : stackArr) {
            String clazzName = stack.getClassName();
            String methodName = stack.getMethodName();

            // 跳过框架、mapper类
            boolean skip = false;
            for (String filter : filterPackages) {
                if (clazzName.contains(filter)) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                continue;
            }

            String classMethod = clazzName + "#" + methodName;
            // 命中Controller，更新顶层controller
            if (clazzName.contains("Controller")) {
                lastController = classMethod;
            }
            // 记录业务Service兜底
            else if (clazzName.contains("Service")) {
                lastService = classMethod;
            }
        }

        // 优先返回Controller，没有则返回最外层Service，都没有则未知
        if (StringUtils.isNotBlank(lastController)) {
            return lastController;
        } else if (StringUtils.isNotBlank(lastService)) {
            return "无Controller入口，顶层Service：" + lastService;
        } else {
            return "未知顶层调用入口";
        }
    }

    /**
     * 字符串超长截断工具
     */
    private String truncateStr(String str, int maxLen) {
        if (StringUtils.isBlank(str) || str.length() <= maxLen) {
            return str;
        }
        return str.substring(0, maxLen) + "...【内容超长截断】";
    }
}
