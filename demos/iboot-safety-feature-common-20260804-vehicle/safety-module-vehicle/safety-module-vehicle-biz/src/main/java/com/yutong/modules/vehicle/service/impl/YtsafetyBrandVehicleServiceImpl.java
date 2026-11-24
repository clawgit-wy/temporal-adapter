package com.yutong.modules.vehicle.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.vehicle.entity.YtsafetyBrandVehicle;
import com.yutong.modules.vehicle.mapper.YtsafetyBrandVehicleMapper;
import com.yutong.modules.vehicle.query.YtsafetyBrandVehicleQuery;
import com.yutong.modules.vehicle.service.IYtsafetyBrandVehicleService;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;
import java.util.List;

/**
 * @Description: 日航品牌库
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Service
public class YtsafetyBrandVehicleServiceImpl extends ServiceImpl<YtsafetyBrandVehicleMapper, YtsafetyBrandVehicle> implements IYtsafetyBrandVehicleService {

    @Autowired
    private YtsafetyBrandVehicleMapper ytsafetyBrandVehicleMapper;

    @Override
    public IPage<YtsafetyBrandVehicle> queryPageList(Page<YtsafetyBrandVehicle> page, YtsafetyBrandVehicleQuery query) {
        return ytsafetyBrandVehicleMapper.queryPageList(page, query);
    }

    @Override
    public List<YtsafetyBrandVehicle> queryList(YtsafetyBrandVehicleQuery query) {
        return ytsafetyBrandVehicleMapper.queryList(query);
    }

    @Override
    public int saveBrandVehicle(YtsafetyBrandVehicle brandVehicle){
        LoginUser loginUser = BusUtits.getLoginUser();
        setAddData(brandVehicle,loginUser);
        return ytsafetyBrandVehicleMapper.insert(brandVehicle);
    }

    @Override
    public int editBrandVehicle(YtsafetyBrandVehicle brandVehicle){
        LoginUser loginUser = BusUtits.getLoginUser();
        setUpdateData(brandVehicle,loginUser);
        return ytsafetyBrandVehicleMapper.updateById(brandVehicle);
    }
    /**
     * 封装实体新增字段
     * @param brandVehicle
     * @param loginUser
     */
    private void setAddData(YtsafetyBrandVehicle brandVehicle, LoginUser loginUser) {
        brandVehicle.setId(IdWorker.getId());
        brandVehicle.setCreateUser(loginUser.getEmpNo());
        brandVehicle.setCreateName(loginUser.getRealname());
        brandVehicle.setCreateTime(new Date());
        brandVehicle.setDelFlag(0);
        brandVehicle.setUpdateUser(loginUser.getEmpNo());
        brandVehicle.setUpdateTime(new Date());
        brandVehicle.setUpdateName(loginUser.getRealname());
    }
    /**
     * 封装修改字段
     * @param brandVehicle
     * @param loginUser
     */
    private void setUpdateData(YtsafetyBrandVehicle brandVehicle, LoginUser loginUser) {
        brandVehicle.setUpdateUser(loginUser.getEmpNo());
        brandVehicle.setUpdateTime(new Date());
        brandVehicle.setUpdateName(loginUser.getRealname());
    }
}
