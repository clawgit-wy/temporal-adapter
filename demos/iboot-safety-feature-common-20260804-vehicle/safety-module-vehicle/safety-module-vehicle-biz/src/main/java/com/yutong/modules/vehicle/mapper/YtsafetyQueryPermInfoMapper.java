package com.yutong.modules.vehicle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.vehicle.entity.YtsafetyQueryPermInfo;
import com.yutong.modules.vehicle.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface YtsafetyQueryPermInfoMapper extends BaseMapper<YtsafetyQueryPermInfo> {

    List<CarInfoVO> getEmployeeCarList(@Param("empNo") String empNo,@Param("phoneNo") String phoneNo);

    List<String> getUserRoleCode(@Param("userId") String userId);

    SafetyEmployeeMessageVO getEmployeeInfo(@Param("empNo") String empNo, @Param("phoneNo") String phoneNo);

    List<PersonnelAccessVO> getPersonnelAccess(@Param("empNo") String empNo);

    List<CarAccessVO> getCarAccessList(@Param("carNo") String carNo);
}
