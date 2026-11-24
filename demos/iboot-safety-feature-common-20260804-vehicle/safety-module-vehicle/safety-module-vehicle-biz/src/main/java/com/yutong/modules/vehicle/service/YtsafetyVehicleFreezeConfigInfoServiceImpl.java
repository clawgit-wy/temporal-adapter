package com.yutong.modules.vehicle.service;


import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.vehicle.api.IYtsafetyVehicleFreezeConfigInfoService;
import com.yutong.modules.vehicle.entity.YtsafetyVehicleFreezeConfigInfo;
import com.yutong.modules.vehicle.mapper.YtsafetyVehicleFreezeConfigInfoMapper;
import com.yutong.modules.vehicle.param.YtsafetyVehicleFreezeConfigInfoAddParam;
import com.yutong.modules.vehicle.vo.YtsafetyVehicleFreezeConfigInfoV0;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 车辆冻结配
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Service
public class YtsafetyVehicleFreezeConfigInfoServiceImpl extends ServiceImpl<YtsafetyVehicleFreezeConfigInfoMapper, YtsafetyVehicleFreezeConfigInfo> implements IYtsafetyVehicleFreezeConfigInfoService {


    public void saveVehicleFreezeConfig(List<YtsafetyVehicleFreezeConfigInfoAddParam> ytsafetyVehicleFreezeConfigInfoList, Long vehicleFreezeConfigId){
        LoginUser user = BusUtits.getLoginUser();
        List<YtsafetyVehicleFreezeConfigInfo> list=new ArrayList<>();
        for(YtsafetyVehicleFreezeConfigInfoAddParam ytsafetyVehicleFreezeConfigInfoAddParam: ytsafetyVehicleFreezeConfigInfoList){
            YtsafetyVehicleFreezeConfigInfo ytsafetyVehicleFreezeConfigInfo=new YtsafetyVehicleFreezeConfigInfo();
            ytsafetyVehicleFreezeConfigInfo.setId(IdWorker.getId());
            BeanUtils.copyProperties(ytsafetyVehicleFreezeConfigInfoAddParam, ytsafetyVehicleFreezeConfigInfo);
            ytsafetyVehicleFreezeConfigInfo.setCreateUser(user.getId());
            ytsafetyVehicleFreezeConfigInfo.setCreateName(user.getRealname());
            ytsafetyVehicleFreezeConfigInfo.setCreateTime(new DateTime());
            ytsafetyVehicleFreezeConfigInfo.setVehicleFreezeConfigId(vehicleFreezeConfigId);
            list.add(ytsafetyVehicleFreezeConfigInfo);
        }
        if(!list.isEmpty()){
            QueryWrapper<YtsafetyVehicleFreezeConfigInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("vehicle_freeze_config_id", vehicleFreezeConfigId);
            this.remove(queryWrapper);
            this.saveBatch(list);
        }
    }

    public List<YtsafetyVehicleFreezeConfigInfoV0> getConfigInfoByidList(List<Long> idList){
        QueryWrapper<YtsafetyVehicleFreezeConfigInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("vehicle_freeze_config_id", idList);
        // 查询全部匹配数据
        List<YtsafetyVehicleFreezeConfigInfo> list = this.baseMapper.selectList(queryWrapper);
        List<YtsafetyVehicleFreezeConfigInfoV0> voList = list.stream()
                .map(entity -> {
                    YtsafetyVehicleFreezeConfigInfoV0 vo = new YtsafetyVehicleFreezeConfigInfoV0();
                    BeanUtils.copyProperties(entity, vo);
                    // 其他字段继续set
                    return vo;
                })
                .collect(Collectors.toList());
        return voList;
    }

    public YtsafetyVehicleFreezeConfigInfo getVehicleFreezeConfig(int vehicleViolationsNumber, Long vehicleFreezeConfigId){
        LambdaQueryWrapper<YtsafetyVehicleFreezeConfigInfo> queryWrapper = new LambdaQueryWrapper<YtsafetyVehicleFreezeConfigInfo>();
        queryWrapper.eq( YtsafetyVehicleFreezeConfigInfo::getVehicleFreezeConfigId, vehicleFreezeConfigId);
        queryWrapper.eq(YtsafetyVehicleFreezeConfigInfo::getVehicleViolationsNumber, vehicleViolationsNumber);
        List<YtsafetyVehicleFreezeConfigInfo> list= this.baseMapper.selectList(queryWrapper);
        if(!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
}
