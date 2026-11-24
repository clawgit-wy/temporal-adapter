package com.yutong.safety.core.db.entity.build.param;

import com.yutong.safety.common.param.web.WebPageParam;
import lombok.Data;

@Data
public class BuildAreaListParam extends WebPageParam {

    private Integer areaId;

    private Boolean pageFlag;
}
