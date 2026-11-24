package com.yutong.modules.vehicle.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @Description: 车辆人员变更流程表
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@ApiModel(value="车辆人员变更流程表VO", description="车辆人员变更流程表VO")
public class YTSafetyCarPersonnelChangeProcessVO {
    
	/**主键*/
    @ApiModelProperty(value = "主键")
	private String changeProcessId;
	/**车辆人员变更code*/
	@Excel(name = "车辆人员变更code", width = 15)
    @ApiModelProperty(value = "车辆人员变更code")
	private String carPersonnelChangeCode;
	/**人员变更类型*/
	@Excel(name = "人员变更类型", width = 15)
    @ApiModelProperty(value = "人员变更类型")
	private String personnelChangeType;
	/**是否更换人员*/
	@Excel(name = "是否更换人员", width = 15)
    @ApiModelProperty(value = "是否更换人员:1-是,0-否")
	private String isReplacePersonnel;
	/**状态：0-草稿，1-调整中，2-审核中，9-起草，30-废弃*/
	@Excel(name = "状态：0-草稿，1-调整中，2-审核中，9-起草，30-废弃", width = 15)
    @ApiModelProperty(value = "状态：0-草稿，1-调整中，2-审核中，9-起草，30-废弃")
	private Integer status;
	/**调整类型：adjustment-调整；uncorrected-未调整*/
	@Excel(name = "调整类型：adjustment-调整；uncorrected-未调整", width = 15)
    @ApiModelProperty(value = "调整类型：adjustment-调整；uncorrected-未调整")
	private String handlerType;
    @ApiModelProperty(value = "车辆人员变更信息列表")
    List<YTSafetyCarPersonnelChangeInfoVO> carPersonnelChangeInfoList;
}
