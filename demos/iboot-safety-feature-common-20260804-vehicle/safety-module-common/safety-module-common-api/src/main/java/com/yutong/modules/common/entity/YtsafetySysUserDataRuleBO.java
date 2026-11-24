package com.yutong.modules.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 用户菜单数据规则BO对象
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class YtsafetySysUserDataRuleBO {
    /**id*/
    private Long id;
    /**用户主键*/
    private Long userId;
    /**菜单主键*/
    private Long permId;
    /**fieldCode*/
    private String fieldCode;
    /**字段名称*/
    private String fieldName;
    /**关系: 0 或 1 且*/
    private Integer relation;
    /**rule conditions*/
    private String ruleConditions;
    /**ruleValue*/
    private String ruleValue;
    /**修改版本*/
    private Integer version;
}
