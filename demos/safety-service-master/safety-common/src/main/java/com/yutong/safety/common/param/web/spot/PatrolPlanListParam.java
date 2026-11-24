package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author gwxiexj
 * @since 2024-05-24 09:47
 */
@Data
public class PatrolPlanListParam extends WebPageParam {
    @ApiModelProperty("主键ID")
    private Integer id;
    @ApiModelProperty("任务名称")
    private String planName;
    @ApiModelProperty("任务类型 1-治安巡查 2-业务验证")
    private Integer planType;
    @ApiModelProperty("巡查类型ID")
    private Integer typeId;
    @ApiModelProperty("巡查类型")
    private String typeName;
    @ApiModelProperty("项目ID")
    private Integer teamId;
    @ApiModelProperty("项目")
    private String teamName;
    @ApiModelProperty("任务开始时间")
    private String beginDate;
    @ApiModelProperty("任务结束时间")
    private String endDate;
    @ApiModelProperty("任务状态 0-正常 1-暂停")
    private Integer status;
    @ApiModelProperty("定时任务标记-后台使用")
    private Integer taskFlag;
    @ApiModelProperty("分页标记-后台使用")
    private Integer begin;
    @ApiModelProperty("工厂ID拼接-后台使用")
    private String idStr;
    @ApiModelProperty("工厂ID")
    private Integer factoryId;
}
