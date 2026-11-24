package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * @author gwcuiyx
 * @date 2024-4-3 15:29
 */
@Data
public class VisitorSetListParam extends WebPageParam {
    /**
     * 类别
     */
    private String positionType;
    /**
     * 是否可当被访人
     */
    private Integer visitorState;
    /**
     * 备注
     */
    private String remark;
    /**
     * 岗位
     */
    private String positionName;
}
