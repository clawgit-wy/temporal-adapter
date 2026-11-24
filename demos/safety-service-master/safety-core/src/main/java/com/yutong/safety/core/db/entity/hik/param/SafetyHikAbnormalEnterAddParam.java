package com.yutong.safety.core.db.entity.hik.param;

import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.param.web.SafetyWebBaseParam;
import com.yutong.safety.core.db.entity.hik.SafetyHikAbnormalEnterDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SafetyHikAbnormalEnterAddParam extends SafetyHikAbnormalEnterDO implements Check {


    @Override
    public void check() {

    }
}
