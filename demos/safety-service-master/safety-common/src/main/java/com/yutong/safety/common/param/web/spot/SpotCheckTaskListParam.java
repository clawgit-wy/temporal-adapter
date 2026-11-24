package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author gwxiexj
 * @since 2024-05-10
 */
@Data
public class SpotCheckTaskListParam extends WebPageParam {

    private Integer planId;
    @ApiModelProperty("任务ID拼接串")
    private String planIdStr;
    @ApiModelProperty("任务名称")
    private String planName;
    @ApiModelProperty("点检日期-开始")
    private String taskBeginDate;
    @ApiModelProperty("点检日期-结束")
    private String taskEndDate;
    @ApiModelProperty("状态")
    private Integer status;
    @ApiModelProperty("工厂ID")
    private String factoryId;
    @ApiModelProperty("类型ID")
    private Integer deviceTypeId;
    @ApiModelProperty("类型")
    private String deviceType;
    @ApiModelProperty("项目ID")
    private Integer teamId;
    @ApiModelProperty("项目")
    private String teamName;
    @ApiModelProperty("点检周期")
    private Integer cycle;
    @ApiModelProperty("分页标记-后台使用 true-分页  false-不分页")
    private Boolean pageFlag;
    @ApiModelProperty("工厂ID拼接")
    private String factoryIdStr;
}
