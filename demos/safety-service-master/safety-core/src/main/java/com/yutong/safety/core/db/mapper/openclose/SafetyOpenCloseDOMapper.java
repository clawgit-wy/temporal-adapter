package com.yutong.safety.core.db.mapper.openclose;

import com.yutong.safety.core.db.entity.openclose.SafetyOpenCloseDO;
import org.springframework.stereotype.Repository;

/**
 * @author caomg
 * @date 2024-11-21 10:57
 * @description SafetyOpenCloseDOMapper
 */
@Repository
public interface SafetyOpenCloseDOMapper {

    void setOpenStatus(SafetyOpenCloseDO en);
    SafetyOpenCloseDO getOpenInfo(SafetyOpenCloseDO en);
}
