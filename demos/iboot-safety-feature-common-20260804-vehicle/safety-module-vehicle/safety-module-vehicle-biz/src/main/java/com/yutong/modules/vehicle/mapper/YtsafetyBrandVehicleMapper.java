package com.yutong.modules.vehicle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.vehicle.entity.YtsafetyBrandVehicle;
import com.yutong.modules.vehicle.query.YtsafetyBrandVehicleQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 日韩品牌库
 * @Author: wxzhangmc
 * @Date:   2026-08-04
 * @Version: V1.0
 */
public interface YtsafetyBrandVehicleMapper extends BaseMapper<YtsafetyBrandVehicle> {

    /**
     * 分页列表查询
     * @param page 分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<YtsafetyBrandVehicle> queryPageList(Page<YtsafetyBrandVehicle> page, @Param("query") YtsafetyBrandVehicleQuery query);

    /**
     * 导出列表查询
     * @param query 查询条件
     * @return 列表数据
     */
    List<YtsafetyBrandVehicle> queryList(@Param("query") YtsafetyBrandVehicleQuery query);
}
