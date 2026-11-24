package com.yutong.safety.core.service.goodsOut;

import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailVehicleDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailVehicleDOExample;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutDetailVehicleListParam;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsOutDetailVehicleDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SafetyGoodsOutDetailVehicleService {

    @Autowired
    private SafetyGoodsOutDetailVehicleDOMapper safetyGoodsOutDetailVehicleDOMapper;

    public int save(SafetyGoodsOutDetailVehicleDO safetyGoodsOutDetailVehicleDO) {
        return safetyGoodsOutDetailVehicleDOMapper.insert(safetyGoodsOutDetailVehicleDO);
    }

    public int update(SafetyGoodsOutDetailVehicleDO safetyGoodsOutDetailVehicleDO) {
        return safetyGoodsOutDetailVehicleDOMapper.updateByPrimaryKeySelective(safetyGoodsOutDetailVehicleDO);
    }

    public List<SafetyGoodsOutDetailVehicleDO> selectList(GoodsOutDetailVehicleListParam vehicleListParam){
        SafetyGoodsOutDetailVehicleDOExample example = formatReceiveDOExample(vehicleListParam);
        return safetyGoodsOutDetailVehicleDOMapper.selectByExample(example);
    }

    public long selectCount(GoodsOutDetailVehicleListParam vehicleListParam){
        SafetyGoodsOutDetailVehicleDOExample example = formatReceiveDOExample(vehicleListParam);
        return safetyGoodsOutDetailVehicleDOMapper.countByExample(example);
    }

    private SafetyGoodsOutDetailVehicleDOExample formatReceiveDOExample(GoodsOutDetailVehicleListParam vehicleListParam){
        SafetyGoodsOutDetailVehicleDOExample example = new SafetyGoodsOutDetailVehicleDOExample();
        SafetyGoodsOutDetailVehicleDOExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isBlank(vehicleListParam.getProcessId())){
            criteria.andProcessIdEqualTo(vehicleListParam.getProcessId());
        }
        if(!CollectionUtils.isEmpty(vehicleListParam.getProcessIdList())){
            criteria.andProcessIdIn(vehicleListParam.getProcessIdList());
        }
        if(StringUtils.isNotBlank(vehicleListParam.getVehicleNo())){
            criteria.andVehicleNoEqualTo(vehicleListParam.getVehicleNo());
        }
        if(StringUtils.isNotBlank(vehicleListParam.getVinNo())){
            criteria.andVinNoEqualTo(vehicleListParam.getVinNo());
        }
        if(vehicleListParam.getBegin() != null){
            example.setLimit(vehicleListParam.getLimit());
            example.setOffset(vehicleListParam.getOffset());
            example.setOrderByClause(vehicleListParam.getOrderByClause());
        }

        return example;
    }
}
