package com.yutong.safety.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 私家车出厂规则界面西汉时VO
 * @author gwxiexj
 * @since 2024-04-07 11:28
 */
@Data
public class SafetyCarOutRuleVO implements Serializable {

    private Integer id;

    /**
     * 岗位
     */
    private String postName;

    /**
     * 类型
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
     * 优先级
     */
    private Integer priority;

    /**
     * 检查
     */
    private String checkFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 组织编码路径
     */
    private String orgCodePath;

    /**
     * 组织名称路径
     */
    private String orgNamePath;
}
