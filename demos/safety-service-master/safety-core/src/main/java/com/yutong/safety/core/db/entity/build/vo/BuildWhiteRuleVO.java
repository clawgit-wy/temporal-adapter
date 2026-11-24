package com.yutong.safety.core.db.entity.build.vo;

import com.yutong.safety.core.db.entity.build.SafetyBuildWhiteRuleDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BuildWhiteRuleVO extends SafetyBuildWhiteRuleDO {
    @ApiModelProperty("依据规则已生产白名单数")
    private Integer whiteCount;
}
