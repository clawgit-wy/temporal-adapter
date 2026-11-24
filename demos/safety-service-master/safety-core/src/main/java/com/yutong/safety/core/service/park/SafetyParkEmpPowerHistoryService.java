package com.yutong.safety.core.service.park;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerHistoryDO;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerHistoryDOExample;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeePowerListParam;
import com.yutong.safety.core.db.entity.park.vo.ParkEmpPowerHistoryVO;
import com.yutong.safety.core.db.entity.park.vo.ParkEmployeePowerVO;
import com.yutong.safety.core.db.mapper.park.SafetyParkEmployeePowerHistoryDOBizMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SafetyParkEmpPowerHistoryService {

    @Autowired
    private SafetyParkEmployeePowerHistoryDOBizMapper safetyParkEmployeePowerHistoryDOMapper;

    public PageVO selectPageList(ParkEmployeePowerListParam listParam){
        listParam.setPageFlag(true);
        List<ParkEmployeePowerVO> list = safetyParkEmployeePowerHistoryDOMapper.selectList(listParam);
        if(!CollectionUtils.isEmpty(list)){
            list.forEach(e->{
                String details = e.getDetailsStr();
                if(StringUtils.isNotBlank(details)){
                    e.setDetailArray(JSONArray.parseArray(details));
                }
            });
        }
        Long total = safetyParkEmployeePowerHistoryDOMapper.selectCount(listParam);
        return new PageVO<>(total,list);
    }

    public List<ParkEmployeePowerVO> selectList(ParkEmployeePowerListParam listParam){
        return safetyParkEmployeePowerHistoryDOMapper.selectList(listParam);
    }

    public void batchUpdate(List<SafetyParkEmployeePowerHistoryDO> list){
        safetyParkEmployeePowerHistoryDOMapper.batchUpdate(list);
    }

    public List<ParkEmpPowerHistoryVO> selectByExampleWithBLOBs(ParkEmployeePowerListParam listParam){
        SafetyParkEmployeePowerHistoryDOExample example = formatExample(listParam);
        if(listParam.getPageFlag() != null && listParam.getPageFlag()){
            example.setLimit(listParam.getLimit());
            example.setOffset(listParam.getOffset());
        }
        return safetyParkEmployeePowerHistoryDOMapper.selectByExampleWithBLOBs(example);
    }

    private SafetyParkEmployeePowerHistoryDOExample formatExample(ParkEmployeePowerListParam listParam){
        SafetyParkEmployeePowerHistoryDOExample example = new SafetyParkEmployeePowerHistoryDOExample();
        SafetyParkEmployeePowerHistoryDOExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(listParam.getEmpNo())){
            criteria.andEmployeeCodeEqualTo(listParam.getEmpNo());
        }
        if(StringUtils.isNotBlank(listParam.getEmpName())){
            criteria.andEmployeeNameEqualTo(listParam.getEmpName());
        }
        if(!CollectionUtils.isEmpty(listParam.getEmpNoList())){
            criteria.andEmployeeCodeIn(listParam.getEmpNoList());
        }
        return example;
    }
}
