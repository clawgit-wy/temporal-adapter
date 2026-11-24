package com.yutong.safety.core.handler.park;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerHistoryDO;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeePowerListParam;
import com.yutong.safety.core.db.entity.park.vo.ParkEmpPowerHistoryVO;
import com.yutong.safety.core.db.entity.park.vo.ParkEmployeePowerVO;
import com.yutong.safety.core.service.park.SafetyParkEmpPowerHistoryService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class SafetyParkEmpPowerHistoryHanlder {

    @Resource
    private SafetyParkEmpPowerHistoryService safetyParkEmpPowerHistoryService;

    public Object selectPageList(ParkEmployeePowerListParam listParam){
        return safetyParkEmpPowerHistoryService.selectPageList(listParam);
    }

    public Object selectList(ParkEmployeePowerListParam listParam){
        return safetyParkEmpPowerHistoryService.selectList(listParam);
    }

    public ParkEmpPowerHistoryVO viewDetail(String empNo) {
        ParkEmployeePowerListParam listParam = new ParkEmployeePowerListParam();
        listParam.setEmpNo(empNo);
        List<ParkEmpPowerHistoryVO> list = safetyParkEmpPowerHistoryService.selectByExampleWithBLOBs(listParam);
        if(CollectionUtils.isEmpty(list)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"未找到员工"+empNo+"的对应权限");
        }
        ParkEmpPowerHistoryVO powerVO = list.get(0);
        String details = powerVO.getDetailsStr();
        if(StringUtils.isNotBlank(details)){
            JSONArray jsonArray = JSONArray.parseArray(details);
            powerVO.setDetailArray(jsonArray);
        }
        return powerVO;
    }

    public Object batchUpdate(SafetyParkEmployeePowerHistoryDO record){
        if(ObjectUtils.isEmpty(record)){
            return null;
        }
        List<SafetyParkEmployeePowerHistoryDO> recordList = new ArrayList<>();
        recordList.add(record);
        safetyParkEmpPowerHistoryService.batchUpdate(recordList);
        return null;
    }
}
