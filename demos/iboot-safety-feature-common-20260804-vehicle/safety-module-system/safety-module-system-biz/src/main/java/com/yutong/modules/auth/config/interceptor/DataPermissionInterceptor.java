package com.yutong.modules.auth.config.interceptor;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.yutong.modules.auth.api.UserAuthDataApi;

import com.yutong.modules.auth.util.SqlInjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.jeecg.common.aspect.annotation.DataPermission;

import java.lang.reflect.Method;
import java.sql.SQLException;

@Slf4j
public class DataPermissionInterceptor implements InnerInterceptor {

    private final UserAuthDataApi ruleService;

    public DataPermissionInterceptor(UserAuthDataApi ruleService) {
        this.ruleService = ruleService;
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        DataPermission anno = getAnnotation(ms);
        //DataPermission anno = DataPermissionContext.get();
        if (anno == null) {
            return;
        }
        long time1=System.currentTimeMillis();
        String ruleCodeSql = ruleService.queryUserDataRule(anno.permCode());
        long time2=System.currentTimeMillis();
        log.info("获取ruleCodeSql耗时："+(time2-time1)+"ms");
        if(StringUtils.isEmpty(ruleCodeSql)){
            return;
        }
        long time3=System.currentTimeMillis();
        String[] orderColumnArr = anno.orderColumn();
        String orderColumn = orderColumnArr.length > 0 ? String.join(",", orderColumnArr) : "";
        String orderRule = anno.orderRule();
        String newSql = SqlInjectUtil.injectSql(boundSql.getSql(), ruleCodeSql,orderColumn,orderRule);
        long time4=System.currentTimeMillis();
        log.info("封装sql耗时："+(time4-time3)+"ms");
        SystemMetaObject.forObject(boundSql).setValue("sql", newSql);
    }

    /**
     * 获取方法上的@DataPermission注解
     */
    private DataPermission getAnnotation(MappedStatement ms) {
        try {
            String id = ms.getId();
            String className = id.substring(0, id.lastIndexOf("."));
            String methodName = id.substring(id.lastIndexOf(".") + 1);
            Class<?> clazz = Class.forName(className);
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    return method.getAnnotation(DataPermission.class);
                }
            }
            return clazz.getAnnotation(DataPermission.class);
        } catch (Exception e) {
            return null;
        }
    }
}
