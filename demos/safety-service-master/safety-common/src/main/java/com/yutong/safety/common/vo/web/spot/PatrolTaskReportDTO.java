package com.yutong.safety.common.vo.web.spot;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PatrolTaskReportDTO{

    @ApiModelProperty("项目名称")
    private String teamName;
    @ApiModelProperty("完成率")
    private String rate;
    @ApiModelProperty("报表集合")
    List<PatrolTaskReportVO> subList;
}
