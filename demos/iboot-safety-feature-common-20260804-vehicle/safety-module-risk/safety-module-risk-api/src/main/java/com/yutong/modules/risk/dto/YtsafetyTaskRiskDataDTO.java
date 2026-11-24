package com.yutong.modules.risk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 风险数据表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
@Data
@ApiModel(value="ytsafety_risk_data对象", description="风险数据表")
public class YtsafetyTaskRiskDataDTO {

    @Excel(name = "风险类")
    @ApiModelProperty(value = "风险类型")
    private String riskTypeId;
	/**所属厂区id*/
	@Excel(name = "所属厂区id", width = 15)
    @ApiModelProperty(value = "所属厂区id")
	private String factoryId;
	/**所属区域id*/
	@Excel(name = "所属区域id", width = 15)
    @ApiModelProperty(value = "所属区域id")
	private String areaId;
	/**所属组织id*/
	@Excel(name = "所属组织id", width = 15)
    @ApiModelProperty(value = "所属组织id")
	private String orgId;

	/**风险等级*/
	@Excel(name = "风险等级", width = 15)
    @ApiModelProperty(value = "风险等级")
	private String riskLevel;

    /**页码*/
    @ApiModelProperty(value = "页码")
    private Integer pageNo = 1;

    /**每页条数*/
    @ApiModelProperty(value = "每页条数")
    private Integer pageSize = 10;

    /**安全员编码*/
    @Excel(name = "安全员编码", width = 15)
    @ApiModelProperty(value = "安全员编码")
    private String safetyOfficer;

}
