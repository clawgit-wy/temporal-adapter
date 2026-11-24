package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class SpotCheckPlanListParam extends WebPageParam {
    @ApiModelProperty("任务名称")
    private String planName;
    @ApiModelProperty("工厂ID")
    private Integer factoryId;
    @ApiModelProperty("工厂编码")
    private String factoryCode;
    @ApiModelProperty("项目ID")
    private Integer teamId;
    @ApiModelProperty("项目名称")
    private String teamName;
    @ApiModelProperty("开始日期")
    private String beginDate;
    @ApiModelProperty("截止日期")
    private String endDate;
    @ApiModelProperty("任务状态 0-正常 1-休眠")
    private Integer status;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("定时任务标记-后台使用")
    private Integer taskFlag;
    @ApiModelProperty("分页标记-后台使用")
    private Integer begin;
    @ApiModelProperty("工厂ID拼接-后台使用")
    private String idStr;
}
