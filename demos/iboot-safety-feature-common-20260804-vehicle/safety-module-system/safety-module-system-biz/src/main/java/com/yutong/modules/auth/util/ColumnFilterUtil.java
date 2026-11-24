package com.yutong.modules.auth.util;

import cn.hutool.core.collection.CollUtil;
import org.jeecg.common.api.vo.ColumnPermVO;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ColumnFilterUtil {
    /**
     * 过滤对象/列表字段（隐藏列）
     */
    public static void filter(Object data, List<ColumnPermVO> perms) {
        if (data == null || CollUtil.isEmpty(perms)) {
            return;
        }
        Set<String> hideColumns = perms.stream()
                .filter(p -> "HIDE".equals(p.getPermType()))
                .map(ColumnPermVO::getColumnKey)
                .collect(Collectors.toSet());

        try {
            // 处理 IPage 类型，取 records 列表
            if(data instanceof List<?>){
                for (Object item : (List<?>) data) {
                    filterFields(item, hideColumns);
                }
            }else{
                Object target = getTarget(data);
                if (target instanceof List<?>) {
                    for (Object item : (List<?>) target) {
                        filterFields(item, hideColumns);
                    }
                } else {
                    filterFields(target, hideColumns);
                }
            }
        } catch (Exception e) {
            // 忽略异常
        }
    }

    /**
     * 如果是 IPage 类型，提取 records 列表
     */
    private static Object getTarget(Object data) {
        if (data == null) {
            return null;
        }
        Class<?> clazz = data.getClass();
        // 尝试获取 getRecords 方法（IPage 接口的方法）
        try {
            Method method = clazz.getMethod("getRecords");
            Object records = method.invoke(data);
            if (records != null) {
                return records;
            }
        } catch (Exception ignored) {
        }
        return data;
    }

    private static void filterFields(Object obj, Set<String> hideColumns) throws Exception {
        for (String field : hideColumns) {
            Field f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);
            // 关键：根据字段类型决定隐藏值
            Class<?> fieldType = f.getType();
            if (fieldType == String.class) {
                // 字符串类型：用 *** 隐藏
                f.set(obj, "***");
            } else {
                // 数字、日期、布尔等所有其他类型：统一置为 null
                f.set(obj, null);
            }
        }
    }
}
