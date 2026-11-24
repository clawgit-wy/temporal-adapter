package com.yutong.modules.vehicle.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.vehicle.entity.YtsafetyBrandVehicle;
import com.yutong.modules.vehicle.query.YtsafetyBrandVehicleQuery;

import java.util.List;

/**
 * @Description: 日航品牌库
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
public interface IYtsafetyBrandVehicleService extends IService<YtsafetyBrandVehicle> {

    /**
     * 分页列表查询
     * @param page 分页对象
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<YtsafetyBrandVehicle> queryPageList(Page<YtsafetyBrandVehicle> page, YtsafetyBrandVehicleQuery query);

    /**
     * 新增日韩品牌库
     * @param brandVehicle
     * @return
     */
    int saveBrandVehicle(YtsafetyBrandVehicle brandVehicle);

    /**
     * 编辑日韩品牌库
     * @param brandVehicle
     * @return
     */
    int editBrandVehicle(YtsafetyBrandVehicle brandVehicle);

    /**
     * 导出列表查询
     * @param query 查询条件
     * @return 列表数据
     */
    List<YtsafetyBrandVehicle> queryList(YtsafetyBrandVehicleQuery query);
}
