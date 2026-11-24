package com.yutong.safety.common.param.web;

import lombok.Data;


@Data
public class SafetyWebBaseParam extends WebPageParam {
    /**
     * 权限的条件
     */
    public String roleWorkNo;
}
