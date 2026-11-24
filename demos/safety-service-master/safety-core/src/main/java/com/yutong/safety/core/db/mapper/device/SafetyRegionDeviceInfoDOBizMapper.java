package com.yutong.safety.core.db.mapper.device;

import com.yutong.safety.core.db.entity.device.SafetyRegionDeviceInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyRegionDeviceInfoDOBizMapper extends SafetyRegionDeviceInfoDOMapper {
    /**
     * 批量插入更新数据，正常更新数据时使用
     */
    void batchSaveOrUpdate(@Param("list") List<SafetyRegionDeviceInfoDO> safetyRegionDeviceInfoDOs);

    /**
     * 批量插入更新数据，出入口数据同步时使用（因为不能更新掉设备类别字段）
     */
    void batchSaveOrUpdateTwo(@Param("list") List<SafetyRegionDeviceInfoDO> safetyRegionDeviceInfoDOs);
}
