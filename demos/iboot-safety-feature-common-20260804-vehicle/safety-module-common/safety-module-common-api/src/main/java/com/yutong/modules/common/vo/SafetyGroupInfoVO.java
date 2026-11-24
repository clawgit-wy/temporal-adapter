package com.yutong.modules.common.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 部门表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
@TableName("safety_group_info")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_group_info对象", description="部门表")
public class SafetyGroupInfoVO {

    @ApiModelProperty(value = "id")
	private Long id;
    @ApiModelProperty(value = "编码")
	private String code;
    @ApiModelProperty(value = "名称")
	private String name;
    @ApiModelProperty(value = "上级组织code")
	private String parentCode;
    @ApiModelProperty(value = "status")
	private String status;
    @ApiModelProperty(value = "层级编码")
	private String levelCode;
    @ApiModelProperty(value = "层级名称")
	private String levelName;
    @ApiModelProperty(value = "上级组织路径名称")
	private String orgCodePath;
    @ApiModelProperty(value = "上级组织路径名称")
	private String orgNamePath;
    @ApiModelProperty(value = "排序编码")
	private Integer deptShort;
}
