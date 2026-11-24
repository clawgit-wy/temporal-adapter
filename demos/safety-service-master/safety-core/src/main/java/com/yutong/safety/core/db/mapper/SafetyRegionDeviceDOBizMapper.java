package com.yutong.safety.core.db.mapper;

import com.yutong.safety.core.db.entity.SafetyRegionDeviceDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyRegionDeviceDOBizMapper extends SafetyRegionDeviceDOMapper {
    /**
     * 批量添加
     * @param blackList
     */
    public void batchInsert(@Param("blackList") List<SafetyRegionDeviceDO> blackList);
}