package com.yutong.safety.core.service.device;

import com.yutong.safety.core.db.entity.device.SafetyRegionDeviceInfoDO;
import com.yutong.safety.core.db.entity.device.SafetyRegionDeviceInfoDOExample;
import com.yutong.safety.core.db.entity.device.param.SafetyRegionDeviceInfoQueryParams;
import com.yutong.safety.core.db.mapper.device.SafetyRegionDeviceInfoDOBizMapper;
import com.yutong.safety.core.dto.device.SafetyRegionDeviceInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SafetyRegionDeviceInfoService {
    @Autowired
    SafetyRegionDeviceInfoDOBizMapper safetyRegionDeviceInfoDOBizMapper;

    public void delete(){
        SafetyRegionDeviceInfoDOExample example = new SafetyRegionDeviceInfoDOExample();
        safetyRegionDeviceInfoDOBizMapper.deleteByExample(example);
    }

    public void batchSaveOrUpdate(List<SafetyRegionDeviceInfoDO> list){
        safetyRegionDeviceInfoDOBizMapper.batchSaveOrUpdate(list);
    }

    public void batchSaveOrUpdateTwo(List<SafetyRegionDeviceInfoDO> list){
        safetyRegionDeviceInfoDOBizMapper.batchSaveOrUpdateTwo(list);
    }

    private SafetyRegionDeviceInfoDOExample getExample(SafetyRegionDeviceInfoDTO param){
        SafetyRegionDeviceInfoDOExample example = new SafetyRegionDeviceInfoDOExample();
        SafetyRegionDeviceInfoDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFlagEqualToZero();
        if(!CollectionUtils.isEmpty(param.getDevNoList())){
            criteria.andDevNoIn(param.getDevNoList());
        }
        if(!StringUtils.isEmpty(param.getDevTypeCode())){
            criteria.andDevTypeCodeEqualTo(param.getDevTypeCode());
        }
        if(!StringUtils.isEmpty(param.getGid())){
            criteria.andGidEqualTo(param.getGid());
        }
        if(!CollectionUtils.isEmpty(param.getGidList())){
            criteria.andGidIn(param.getGidList());
        }
        return example;
    }

    public List<SafetyRegionDeviceInfoDO> getList(SafetyRegionDeviceInfoDTO param){
        SafetyRegionDeviceInfoDOExample example = getExample(param);
        return safetyRegionDeviceInfoDOBizMapper.selectByExample(example);
    }


    public List<SafetyRegionDeviceInfoDO> selectAllList(List<Integer> ids) {
        SafetyRegionDeviceInfoDOExample example = new SafetyRegionDeviceInfoDOExample();
        SafetyRegionDeviceInfoDOExample.Criteria criteria = example.createCriteria();
        if (!CollectionUtils.isEmpty(ids)) {
            criteria.andIdIn(ids);
        }
        return safetyRegionDeviceInfoDOBizMapper.selectByExample(example);
    }

    public List<SafetyRegionDeviceInfoDO> selectFactoryIdIsNullList() {
        SafetyRegionDeviceInfoDOExample example = new SafetyRegionDeviceInfoDOExample();
        SafetyRegionDeviceInfoDOExample.Criteria criteria = example.createCriteria();
        criteria.andFactoryIdIsNull();
        return safetyRegionDeviceInfoDOBizMapper.selectByExample(example);
    }


    public List<SafetyRegionDeviceInfoDO> selectList(SafetyRegionDeviceInfoQueryParams param, Boolean pageCheck) {
        SafetyRegionDeviceInfoDOExample example = new SafetyRegionDeviceInfoDOExample();
        SafetyRegionDeviceInfoDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeviceCategoryIsNull();
        criteria.andDeleteFlagEqualToZero();
        if (pageCheck) {
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
        return safetyRegionDeviceInfoDOBizMapper.selectByExample(example);
    }

    public long selectCount() {
        SafetyRegionDeviceInfoDOExample example = new SafetyRegionDeviceInfoDOExample();
        SafetyRegionDeviceInfoDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeviceCategoryIsNull();
        criteria.andDeleteFlagEqualToZero();
        return safetyRegionDeviceInfoDOBizMapper.countByExample(example);
    }

    public void batchUpdateDeviceType(List<Integer> ids, String deviceCategory) {
        if (!CollectionUtils.isEmpty(ids)) {
            safetyRegionDeviceInfoDOBizMapper.batchUpdateDeviceType(ids, deviceCategory);
        }
    }
}
