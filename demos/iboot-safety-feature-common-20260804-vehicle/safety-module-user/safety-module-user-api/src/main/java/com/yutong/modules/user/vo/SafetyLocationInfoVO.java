package com.yutong.modules.user.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 位置信息
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SafetyLocationInfoVO {

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private Long id;
    /**名称*/
    @Excel(name = "位置编码", width = 15)
    @ApiModelProperty(value = "位置编码")
    private String locationCode;
	/**名称*/
	@Excel(name = "位置名称", width = 15)
    @ApiModelProperty(value = "位置名称")
	private String locationName;

    /**名称*/
    @Excel(name = "组织名称", width = 15)
    @ApiModelProperty(value = "组织名称")
    private String orgName;

    /**编码*/
    @Excel(name = "组织编码", width = 15)
    @ApiModelProperty(value = "组织编码")
    private String orgCode;

    @ApiModelProperty(value = "组织主键")
    private Long orgId;

    @ApiModelProperty(value = "厂区主键")
    private Long factoryId;

    @ApiModelProperty(value = "厂区编码")
    private String factoryCode;

    @ApiModelProperty(value = "厂区名称")
    private String factoryName;

    @ApiModelProperty(value = "区域主键")
    private Long areaId;

    @ApiModelProperty(value = "区域编码")
    private String areaCode;

    @ApiModelProperty(value = "区域名称")
    private String areaName;
}
