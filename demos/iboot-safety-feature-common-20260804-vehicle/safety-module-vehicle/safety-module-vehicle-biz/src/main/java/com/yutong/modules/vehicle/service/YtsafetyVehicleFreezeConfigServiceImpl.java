package com.yutong.modules.vehicle.service;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.vehicle.api.IYtsafetyVehicleFreezeConfigInfoService;
import com.yutong.modules.vehicle.api.IYtsafetyVehicleFreezeConfigService;
import com.yutong.modules.vehicle.entity.YtsafetyVehicleFreezeConfig;
import com.yutong.modules.vehicle.entity.YtsafetyVehicleFreezeConfigInfo;
import com.yutong.modules.vehicle.mapper.YtsafetyVehicleFreezeConfigMapper;
import com.yutong.modules.vehicle.param.YtsafetyVehicleFreezeConfigAddParam;
import com.yutong.modules.vehicle.param.YtsafetyVehicleFreezeConfigEditParam;
import com.yutong.modules.vehicle.param.YtsafetyVehicleFreezeConfigQueryParam;
import com.yutong.modules.vehicle.vo.YtsafetyVehicleFreezeConfigInfoV0;
import com.yutong.modules.vehicle.vo.YtsafetyVehicleFreezeConfigVo;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 车辆冻结配
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Service
public class YtsafetyVehicleFreezeConfigServiceImpl extends ServiceImpl<YtsafetyVehicleFreezeConfigMapper, YtsafetyVehicleFreezeConfig> implements IYtsafetyVehicleFreezeConfigService {

    @Autowired
    private IYtsafetyVehicleFreezeConfigInfoService ytsafetyVehicleFreezeConfigInfoService;

    /**
     * 违规车辆冻结时长配置列表
     * @param page
     * @param queryParam
     * @return
     */
    public IPage<YtsafetyVehicleFreezeConfigVo> queryVehicleFreezeConfigList(Page<YtsafetyVehicleFreezeConfig> page, YtsafetyVehicleFreezeConfigQueryParam queryParam){
        LambdaQueryWrapper<YtsafetyVehicleFreezeConfig> queryWrapper = new LambdaQueryWrapper<YtsafetyVehicleFreezeConfig>();
        queryWrapper.eq(StrUtil.isNotBlank(queryParam.getViolationsType()),YtsafetyVehicleFreezeConfig::getViolationsType,queryParam.getViolationsType());
        IPage<YtsafetyVehicleFreezeConfig> pageList = this.page(page, queryWrapper);
       // 新建VO分页对象
        Page<YtsafetyVehicleFreezeConfigVo> voPage = new Page<>();
        BeanUtils.copyProperties(pageList, voPage, "records");
        // pageList 为 Page<T> 对象
        List<Long> idList = pageList.getRecords().stream()
                .map(entity -> entity.getId())
                .collect(Collectors.toList());
        Map<Long, List<YtsafetyVehicleFreezeConfigInfoV0>> groupMap;
        if(!idList.isEmpty()) {
             groupMap = ytsafetyVehicleFreezeConfigInfoService.getConfigInfoByidList(idList).stream()
                    .collect(Collectors.groupingBy(YtsafetyVehicleFreezeConfigInfoV0::getVehicleFreezeConfigId));
        } else {
            groupMap = new HashMap<>();
        }
        List<YtsafetyVehicleFreezeConfigVo> voList = pageList.getRecords().stream()
                .map(entity -> {
                    YtsafetyVehicleFreezeConfigVo vo = new YtsafetyVehicleFreezeConfigVo();
                    BeanUtils.copyProperties(entity, vo);
                    if(groupMap.get(entity.getId())!=null){
                        vo.setVehicleFreezeConfigInfoList(groupMap.get(entity.getId()));
                    }
                    return vo;
                }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    public void saveFreezeConfig(YtsafetyVehicleFreezeConfigAddParam addParam){
        LoginUser user = BusUtits.getLoginUser();
        // 前置校验（优化用户体验）
        LambdaQueryWrapper<YtsafetyVehicleFreezeConfig> checkWrapper = Wrappers.lambdaQuery();
        checkWrapper.eq(YtsafetyVehicleFreezeConfig::getViolationsType, addParam.getViolationsType());
        long count = this.count(checkWrapper);
        if (count > 0) {
            throw new RuntimeException("该违规类型【" + addParam.getViolationsType() + "】已存在，请勿重复添加");
        }
        YtsafetyVehicleFreezeConfig  entity=new YtsafetyVehicleFreezeConfig();
        entity.setId(IdWorker.getId());
        BeanUtils.copyProperties(addParam, entity);
        entity.setCreateUser(user.getId());
        entity.setCreateName(user.getRealname());
        entity.setCreateTime(new DateTime());
        this.save(entity);
        ytsafetyVehicleFreezeConfigInfoService.saveVehicleFreezeConfig(addParam.getYtsafetyVehicleFreezeConfigInfoAddParam(),entity.getId());
    }





    /**
     * 违规车辆冻结时长配置修改
     * @param editParam
     * @return
     */
    public void updateFreezeConfig(YtsafetyVehicleFreezeConfigEditParam editParam){
        YtsafetyVehicleFreezeConfig  entity=getById(editParam.getId());
        LambdaQueryWrapper<YtsafetyVehicleFreezeConfig> checkWrapper = Wrappers.lambdaQuery();
        checkWrapper.eq(YtsafetyVehicleFreezeConfig::getViolationsType, editParam.getViolationsType())
                .ne(YtsafetyVehicleFreezeConfig::getId, editParam.getId()); // 排除当前自身
        long count = this.count(checkWrapper);
        if (count > 0) {
            throw new RuntimeException("违规类型已存在");
        }
        if(entity!=null){
            BeanUtils.copyProperties(editParam, entity);
        }
        ytsafetyVehicleFreezeConfigInfoService.saveVehicleFreezeConfig(editParam.getYtsafetyVehicleFreezeConfigInfoAddParam(),entity.getId());
        this.updateById(entity);
    }


    /**
     * 违规车辆冻结时长配置删除
     * @param id
     * @return
     */
    public void removeFreezeConfigById(String id){
        YtsafetyVehicleFreezeConfig  entity=getById(id);
        if(entity!=null){
            entity.setDelFlag(1);
        }
        this.updateById(entity);
    }

    /**
     * 违规车辆冻结时长配置删除
     * @param ids
     * @return
     */
    public void removeFreezeConfigByIds(List<String> ids){
        List<YtsafetyVehicleFreezeConfig> list=new ArrayList<>();
        for(String id:ids) {
            YtsafetyVehicleFreezeConfig  entity=getById(id);
            if (entity != null) {
                entity.setDelFlag(1);
                list.add(entity);
            }
        }
        if(!list.isEmpty()){
            this.updateBatchById(list);
        }
    }

    public int getVehicleFreeze(int vehicleViolationsNumber,String violationsTypeCode) {
        int a=0;
        LambdaQueryWrapper<YtsafetyVehicleFreezeConfig> queryWrapper = new LambdaQueryWrapper<YtsafetyVehicleFreezeConfig>();
        queryWrapper.eq(YtsafetyVehicleFreezeConfig::getViolationsCode, violationsTypeCode);
        queryWrapper.eq(YtsafetyVehicleFreezeConfig::getDelFlag, 0);
        List<YtsafetyVehicleFreezeConfig> list= this.baseMapper.selectList(queryWrapper);
        if(!list.isEmpty()){
            YtsafetyVehicleFreezeConfig ytsafetyVehicleFreezeConfig=list.get(0);
            YtsafetyVehicleFreezeConfigInfo vo=ytsafetyVehicleFreezeConfigInfoService.getVehicleFreezeConfig(vehicleViolationsNumber,ytsafetyVehicleFreezeConfig.getId());
            if(vo!=null){
                a=vo.getVehicleFreezeNumber();
            }else{
                throw new RuntimeException("该类型没有配置违规信息");
            }
        }
        return a;
    }


    // a="违规第"+vehicleViolationsNumber+"次，封禁"+vo.getVehicleFreezeNumber()+"天";
}
