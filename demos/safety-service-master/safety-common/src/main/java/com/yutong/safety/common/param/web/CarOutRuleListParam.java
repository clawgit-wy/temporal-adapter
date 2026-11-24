package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * @author gwxiexj
 * @since 2024-04-02 09:55
 */
@Data
public class CarOutRuleListParam extends WebPageParam {

    /**
     * 岗位
     */
    private String postName;

    /**
     * 类别
     */
    private String postCategory;

    /**
     * 标记
     */
    private String checkFlag;

    /**
      备注
     */
    private String remark;
}
