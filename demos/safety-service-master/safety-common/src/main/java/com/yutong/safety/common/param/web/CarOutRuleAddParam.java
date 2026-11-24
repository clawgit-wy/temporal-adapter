package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * @author gwxiexj
 * @since 2024-04-01 17:15:00
 */
@Data
public class CarOutRuleAddParam  extends WebPageParam{

    /**
     * 主键ID
     */
    private Integer ruleId;

    /**
     * 岗位
     */
    private String postName;

    /**
     * 类别 直接-直接岗  间接-间接刚
     */
    private String postCategory;
    /**
     * 组织编码
     */
    private String organCode;

    /**
     * 组织名称
     */
    private String organName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 优先级 1-10
     */
    private String priority;

    /**
     * 标记
     */
    private String checkFlag;

    /**
     * 删除标记 0-正常 1-删除
     */
    private Integer isDelete;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(priority), ResponseEnum.PARAM_ERROR.getCode(), "私家车出场规则“优先级”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(checkFlag), ResponseEnum.PARAM_ERROR.getCode(), "私家车出场规则“标记”不能为空！");
    }
}
