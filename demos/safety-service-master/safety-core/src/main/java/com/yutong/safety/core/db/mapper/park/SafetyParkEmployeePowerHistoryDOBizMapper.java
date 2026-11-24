package com.yutong.safety.core.db.mapper.park;

import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerHistoryDO;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeePowerListParam;
import com.yutong.safety.core.db.entity.park.vo.ParkEmployeePowerVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyParkEmployeePowerHistoryDOBizMapper extends SafetyParkEmployeePowerHistoryDOMapper{

    List<ParkEmployeePowerVO> selectList(ParkEmployeePowerListParam listParam);

    Long selectCount(ParkEmployeePowerListParam listParam);

    void batchUpdate(@Param("list") List<SafetyParkEmployeePowerHistoryDO> list);
}
