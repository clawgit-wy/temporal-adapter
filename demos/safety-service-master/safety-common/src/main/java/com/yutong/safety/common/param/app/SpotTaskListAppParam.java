package com.yutong.safety.common.param.app;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SpotTaskListAppParam extends WebPageParam {
    @ApiModelProperty("日查询日期")
    private String dayDate;
    @ApiModelProperty("开始日期")
    private String beginDate;
    @ApiModelProperty("结束日期")
    private String endDate;
    @ApiModelProperty("点检人员工号-后台使用")
    private String checkEmpNo;
}
