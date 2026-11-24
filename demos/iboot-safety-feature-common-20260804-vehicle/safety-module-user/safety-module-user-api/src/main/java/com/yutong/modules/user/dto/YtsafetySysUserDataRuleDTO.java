package com.yutong.modules.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 用户菜单数据规则表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="用户菜单数据规则表DTO", description="用户菜单数据规则表")
public class YtsafetySysUserDataRuleDTO {
    /**id*/
    @ApiModelProperty(value = "id")
    private Long id;
    /**用户主键*/
    @Excel(name = "用户主键", width = 15)
    @ApiModelProperty(value = "用户主键")
    private Long userId;
    /**菜单主键*/
    @ApiModelProperty(value = "菜单主键")
    @NotNull(message = "菜单id不能为空")
    private Long permId;
    /**fieldCode*/
    @ApiModelProperty(value = "fieldCode")
    @NotBlank(message = "字段编码不能为空")
    private String fieldCode;
    /**字段名称*/
    @ApiModelProperty(value = "字段名称")
    @NotBlank(message = "字段名称不能为空")
    private String fieldName;
    /**关系: 0 或 1 且*/
    @ApiModelProperty(value = "关系: 0 或 1 且")
    @NotBlank(message = "关系不能为空")
    private String relation;
    /**rule conditions*/
    @ApiModelProperty(value = ">,=,!=,like")
    @NotBlank(message = "判断条件不能为空")
    private String ruleConditions;
    /**ruleValue*/
    @NotBlank(message = "值不能为空")
    @ApiModelProperty(value = "ruleValue")
    private String ruleValue;
    /**排序*/
    @Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "菜单编码")
    @NotBlank(message = "菜单编码不能为空")
    private String permCode;
}
