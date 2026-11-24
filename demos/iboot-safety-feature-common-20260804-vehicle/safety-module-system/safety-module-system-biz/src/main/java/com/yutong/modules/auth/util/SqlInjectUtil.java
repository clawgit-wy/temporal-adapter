package com.yutong.modules.auth.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlInjectUtil {
    /**
     * 注入权限条件到SQL
     */
    public static String injectSql(String originalSql, String permSql,String orderColumn,String orderRule) {
        log.debug("==数据查询原sql语句：",originalSql);
        if (StrUtil.isBlank(permSql)){
            return originalSql;
        }
        if(StrUtil.isNotEmpty(orderColumn) && StrUtil.isNotEmpty(orderRule)){
            String sql = "select * from ("+originalSql+") tb"+" where 1=1 "+permSql+" ORDER BY " + orderColumn + " " + orderRule;
            log.debug("==数据权限拼接后的sql语句：",sql);
            return sql;
        }else{
            String sql ="select * from ("+originalSql+") tb"+" where 1=1 "+permSql;
            log.debug("==数据权限拼接后的sql语句：",sql);
            return sql;
        }
    }
}
