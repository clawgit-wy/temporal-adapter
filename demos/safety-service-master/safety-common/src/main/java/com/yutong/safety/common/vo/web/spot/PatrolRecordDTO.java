package com.yutong.safety.common.vo.web.spot;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PatrolRecordDTO {
    @ApiModelProperty("巡检类型名称")
    private String typeName;
    @ApiModelProperty("报表集合")
    List<PatrolRecordVO> subList;
}
