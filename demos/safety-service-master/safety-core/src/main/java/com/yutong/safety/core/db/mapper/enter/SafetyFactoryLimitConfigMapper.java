package com.yutong.safety.core.db.mapper.enter;

import com.yutong.safety.core.db.entity.enter.SafetyFactoryLimitConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author caomg
 * @date 2024-9-10 11:10
 * @description SafetyFactoryLimitConfigMapper
 */
@Mapper
public interface SafetyFactoryLimitConfigMapper {
    void save(SafetyFactoryLimitConfigEntity en);

    void update(SafetyFactoryLimitConfigEntity en);

    SafetyFactoryLimitConfigEntity getData();

    //获取n天前 审批通过的车辆信息
    List<Map<String,Object>> getLastDaysData(@Param("limitNumber")Integer limitNumber);

    //根据车辆牌照查询这些车在规定时间内入场的记录
    List<String> getCarNoListInLimitNumber(@Param("limitNumber")Integer limitNumber);

    //更新权限截止时间
    void cancelCarAccess(@Param("flowList")List<String> flowList);
}
