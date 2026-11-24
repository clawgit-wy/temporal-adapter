package com.yutong.safety.core.db.mapper;

import com.yutong.safety.core.db.entity.SafetyUserMessageDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyUserMessageDOBizMapper extends SafetyUserMessageDOMapper {
    /**
     * 批量添加
     * @param blackList
     */
    public void batchInsert(@Param("blackList") List<SafetyUserMessageDO> blackList);
}