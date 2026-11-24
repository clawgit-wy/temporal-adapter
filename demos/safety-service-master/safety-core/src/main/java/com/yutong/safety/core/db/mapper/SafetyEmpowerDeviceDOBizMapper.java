package com.yutong.safety.core.db.mapper;


import com.yutong.safety.core.db.entity.SafetyEmpowerDeviceDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyEmpowerDeviceDOBizMapper extends SafetyEmpowerDeviceDOMapper {

    /**
     * 批量添加
     * @param blackList
     */
    public void batchInsert(@Param("blackList") List<SafetyEmpowerDeviceDO> blackList);

}