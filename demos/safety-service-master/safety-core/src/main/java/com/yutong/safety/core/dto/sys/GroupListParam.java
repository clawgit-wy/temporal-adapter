package com.yutong.safety.core.dto.sys;

import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.param.web.WebPageParam;
import lombok.Data;

@Data
public class GroupListParam extends WebPageParam {

    private String updateTimeBegin;

    private String updateTimeEnd;

    private String status;

    private Boolean pageFlag;
}
