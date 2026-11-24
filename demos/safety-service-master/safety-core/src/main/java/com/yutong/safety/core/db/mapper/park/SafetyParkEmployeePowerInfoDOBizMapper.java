package com.yutong.safety.core.db.mapper.park;

import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDO;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeePowerListParam;
import com.yutong.safety.core.dto.ParamDTO;
import com.yutong.safety.core.dto.park.SafetyParkEmployeePowerInfoDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyParkEmployeePowerInfoDOBizMapper extends SafetyParkEmployeePowerInfoDOMapper {
    void saveOrUpdate(@Param("list")List<SafetyParkEmployeePowerInfoDO> successList);

    void saveOrUpdateTemp(@Param("list")List<SafetyParkEmployeePowerInfoDO> successList);

    List<SafetyParkEmployeePowerInfoDO> getList(SafetyParkEmployeePowerInfoDO param);
    List<ParamDTO> getUserList(@Param("list")List<String> list);

    List<SafetyParkEmployeePowerInfoDO> getDetailByCon(ParkEmployeePowerListParam en);

    List<SafetyParkEmployeePowerInfoDTO> selectListByEmpNo(@Param("empNo")String empNo);
}