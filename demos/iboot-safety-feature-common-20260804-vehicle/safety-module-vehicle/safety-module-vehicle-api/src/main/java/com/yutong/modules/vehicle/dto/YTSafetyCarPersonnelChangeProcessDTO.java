package com.yutong.modules.vehicle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 车辆人员变更流程表
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@ApiModel(value="车辆人员变更流程表DTO", description="车辆人员变更流程表DTO")
public class YTSafetyCarPersonnelChangeProcessDTO {

    @ApiModelProperty(value = "主键")
    private Integer id;
    @ApiModelProperty(value = "车辆人员变更信息列表")
    List<YTSafetyCarPersonnelChangeInfoDTO> carPersonnelChangeInfoList;
}
