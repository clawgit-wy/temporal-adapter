package com.yutong.safety.core.db.mapper.park;

import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.dto.ParamDTO;
import com.yutong.safety.core.dto.park.SafetyParkPowerProcessDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyParkPowerProcessDOBizMapper extends SafetyParkPowerProcessDOMapper {
    List<SafetyParkPowerProcessDTO> selectList(SafetyParkPowerProcessDTO param);
    Integer selectListCount(SafetyParkPowerProcessDTO param);

    List<SafetyEmployeePrivateCarDO> getListByUserCode(@Param("list") List<String> userCodeList);

    List<ParamDTO> getUserList(@Param("processId") String processId);

    List<ParamDTO> getCarList(@Param("list") List<ParamDTO> userList);

    List<ParamDTO> getCarListForEmpNos(@Param("list") List<String> empNoList);

    List<ParamDTO> getUserListByEmpNo(@Param("empNos") String empNos,@Param("userType") String userType);
}