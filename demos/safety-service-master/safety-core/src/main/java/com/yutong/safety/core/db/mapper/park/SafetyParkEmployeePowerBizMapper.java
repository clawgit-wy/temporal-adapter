package com.yutong.safety.core.db.mapper.park;

import com.yutong.safety.core.db.entity.park.param.ParkEmployeePowerListParam;
import com.yutong.safety.core.db.entity.park.vo.ParkEmployeePowerVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyParkEmployeePowerBizMapper {

    List<ParkEmployeePowerVO> selectList(ParkEmployeePowerListParam param);

    long selectCount(ParkEmployeePowerListParam param);

    List<ParkEmployeePowerVO> selectListNew(ParkEmployeePowerListParam param);

    long selectCountNew(ParkEmployeePowerListParam param);

    List<String> getUnSyncCarList();

    int modifyDownMark(@Param("list") List<String> empNoList);

    int batchDelEmployeePower(@Param("list") List<String> empNoList);
}