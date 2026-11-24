package org.jeecg.common.util;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.domain.BaseDO;

public class MybatisPlusUtil {

    public static <T extends BaseDO> LambdaQueryWrapper<T> lambdaWrapper(Class<T> clazz) {
        // 防止泛型化子类找不到字段
        return new LambdaQueryWrapper<T>().setEntityClass(clazz)
                .eq(BaseDO::getDelFlag, 0)
                .orderByDesc(T::getId);
    }

    public static <T extends BaseDO> QueryWrapper<T> wrapper(Class<T> clazz) {
        // 防止泛型化子类找不到字段
        return new QueryWrapper<T>().setEntityClass(clazz)
                .eq("del_flag", 0)
                .orderByDesc("id");
    }

    public static <T extends BaseDO> LambdaQueryWrapper<T> lambdaWrapperNoOrder(Class<T> clazz) {
        return new LambdaQueryWrapper<T>().setEntityClass(clazz)
                .eq(BaseDO::getDelFlag, 0);
    }


    public static <T extends BaseDO> QueryWrapper<T> wrapperNoOrder(Class<T> clazz) {
        // 防止泛型化子类找不到字段
        return new QueryWrapper<T>().setEntityClass(clazz)
                .eq("del_flag", 0);
    }

}
