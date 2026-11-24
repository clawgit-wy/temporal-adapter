package com.yutong.safety.common.param.web.sys;

import com.yutong.safety.common.param.web.WebPageParam;
import lombok.Data;

@Data
public class SysRoleListParam extends WebPageParam {
    private String roleName;
}
