package com.yutong.modules.common.mapper;

import java.util.List;

import com.yutong.modules.common.entity.YtsafetyApiRouteConfig;
import com.yutong.modules.common.entity.YtsafetySysUserDataRuleBO;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 统一接口路由表
 * @Author: lvss
 * @Date:   2026-05-25
 * @Version: V1.0
 */
public interface YtsafetyApiRouteConfigMapper extends BaseMapper<YtsafetyApiRouteConfig> {

    @Select("select * from ytsafety_api_route_config where del_flag=0 and route_code=#{routeCode} ")
    public YtsafetyApiRouteConfig queryApiRouteConfig(@Param("routeCode") String routeCode);

    /**
     * 根据用户id查询数据权限规则
     * @param userId
     * @return
     */
    public List<YtsafetySysUserDataRuleBO> getListByUserId(@Param("userId") Long userId);
}
