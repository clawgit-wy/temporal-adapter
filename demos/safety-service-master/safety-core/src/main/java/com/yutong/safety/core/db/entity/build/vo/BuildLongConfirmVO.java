package com.yutong.safety.core.db.entity.build.vo;

import com.yutong.safety.core.db.entity.build.SafetyBuildLongConfirmDO;
import lombok.Data;

@Data
public class BuildLongConfirmVO extends SafetyBuildLongConfirmDO {
    private String factoryId;
    private String factoryName;
}
