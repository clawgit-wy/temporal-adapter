package com.yutong.safety.core.db.mapper;

import com.yutong.safety.core.db.entity.SafetyGroupInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author gwxiexj
 *
 */
public interface SafetyGroupManageDOMapper extends SafetyGroupInfoDOMapper{

    /**
     * 批量添加
     * @param blackList
     */
    public void batchInsert(@Param("groupList") List<SafetyGroupInfoDO> blackList);
}
