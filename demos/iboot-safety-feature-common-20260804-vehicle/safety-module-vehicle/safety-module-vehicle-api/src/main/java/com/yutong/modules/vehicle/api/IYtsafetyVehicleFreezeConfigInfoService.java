package com.yutong.modules.vehicle.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.vehicle.entity.YtsafetyVehicleFreezeConfigInfo;
import com.yutong.modules.vehicle.param.YtsafetyVehicleFreezeConfigInfoAddParam;
import com.yutong.modules.vehicle.vo.YtsafetyVehicleFreezeConfigInfoV0;

import java.util.List;

/**
 * @Description: 车辆冻结配
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
public interface IYtsafetyVehicleFreezeConfigInfoService extends IService<YtsafetyVehicleFreezeConfigInfo> {


    /**
     * 违规车辆冻结时长配置新增
     * @param ytsafetyVehicleFreezeConfigInfoList
     * @return
     */
    void saveVehicleFreezeConfig(List<YtsafetyVehicleFreezeConfigInfoAddParam> ytsafetyVehicleFreezeConfigInfoList , Long vehicleFreezeConfigId);

    /**
     * 根据批量id获取信息
     * @param idList
     * @return
     */
    List<YtsafetyVehicleFreezeConfigInfoV0> getConfigInfoByidList(List<Long> idList);


    /**
     * 根据id+违规次数获取违规配置信息
     * @param vehicleFreezeConfigId
     * @return
     */
    YtsafetyVehicleFreezeConfigInfo getVehicleFreezeConfig(int vehicleViolationsNumber, Long vehicleFreezeConfigId);
}
