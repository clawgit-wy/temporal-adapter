package com.yutong.safety.core.db.entity.build.vo;

import com.yutong.safety.core.db.entity.build.SafetyBuildProcessCloseDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildProcessDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildProcessOpenDO;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BuildProcessVO extends SafetyBuildProcessDO {

    /**
     * 部门批量开通门禁集合
     */
    private List<SafetyBuildProcessOpenDO> openList;

    /**
     * 部门批量关闭门禁集合
     */
    private List<SafetyBuildProcessCloseDO> closeList;
}
