package com.yutong.safety.core.db.mapper;


import com.yutong.safety.core.dto.SafetyPersonCarOutRuleDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyPersonCarOutRuleDOBizMapper extends SafetyPersonCarOutRuleDOMapper {
    /**
     * 分页查询
     * @param checkFlag
     * @param empNo
     * @param startRowNum
     * @param endRowNum
     * @return
     */
    List<SafetyPersonCarOutRuleDTO> pageList(@Param("checkFlag") String checkFlag, @Param("empNo") String empNo, @Param("empName") String empName, @Param("positionName") String positionName, @Param("startRowNum") Integer startRowNum, @Param("endRowNum") Integer endRowNum);

    /**
     * 获取总条数
     * @param checkFlag
     * @param empNo
     * @return
     */
    long pageListCount(@Param("checkFlag") String checkFlag, @Param("empNo") String empNo, @Param("empName") String empName, @Param("positionName") String positionName);
}