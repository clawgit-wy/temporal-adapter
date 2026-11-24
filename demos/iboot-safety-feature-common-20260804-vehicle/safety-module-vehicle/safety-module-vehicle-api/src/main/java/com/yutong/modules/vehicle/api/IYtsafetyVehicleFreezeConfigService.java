package com.yutong.modules.vehicle.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.vehicle.entity.YtsafetyVehicleFreezeConfig;
import com.yutong.modules.vehicle.param.YtsafetyVehicleFreezeConfigAddParam;
import com.yutong.modules.vehicle.param.YtsafetyVehicleFreezeConfigEditParam;
import com.yutong.modules.vehicle.param.YtsafetyVehicleFreezeConfigQueryParam;
import com.yutong.modules.vehicle.vo.YtsafetyVehicleFreezeConfigVo;

import java.util.List;

/**
 * @Description: 车辆冻结配
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
public interface IYtsafetyVehicleFreezeConfigService extends IService<YtsafetyVehicleFreezeConfig> {

    /**
     * 违规车辆冻结时长配置列表
     * @param page
     * @param queryWrapper
     * @return
     */
    public IPage<YtsafetyVehicleFreezeConfigVo> queryVehicleFreezeConfigList(Page<YtsafetyVehicleFreezeConfig> page, YtsafetyVehicleFreezeConfigQueryParam queryWrapper);


    /**
     * 违规车辆冻结时长配置新增
     * @param ytsafetyVehicleFreezeConfigAddParam
     * @return
     */
    public void saveFreezeConfig(YtsafetyVehicleFreezeConfigAddParam ytsafetyVehicleFreezeConfigAddParam);


    /**
     * 违规车辆冻结时长配置修改
     * @param ytsafetyVehicleFreezeConfigEditParam
     * @return
     */
    public void updateFreezeConfig(YtsafetyVehicleFreezeConfigEditParam ytsafetyVehicleFreezeConfigEditParam);

    /**
     * 违规车辆冻结时长配置删除
     * @param id
     * @return
     */
    public void removeFreezeConfigById(String id);

    /**
     * 违规车辆冻结时长配置批量删除
     * @param ids
     * @return
     */
    public void removeFreezeConfigByIds(List<String> ids);


    /**
     * 获取冷冻天数
     * @param violationsTypeCode
     * @return
     */
    public int getVehicleFreeze(int vehicleViolationsNumber,String violationsTypeCode);


}
