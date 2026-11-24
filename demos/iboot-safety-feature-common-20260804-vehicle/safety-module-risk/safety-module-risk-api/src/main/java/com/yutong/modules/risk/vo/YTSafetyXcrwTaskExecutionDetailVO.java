package com.yutong.modules.risk.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yutong.modules.common.entity.YTSafetyFileInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class YTSafetyXcrwTaskExecutionDetailVO {

    /**任务名称*/
    @Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private String taskName;
    /**任务类型(巡查、专项、定点)*/
    @Excel(name = "任务类型", width = 15)
    @ApiModelProperty(value = "任务类型(巡查、专项、定点)")
    private String taskType;
    /**任务描述(任务要求)*/
    @Excel(name = "任务描述", width = 15)
    @ApiModelProperty(value = "任务描述(任务要求)")
    private String taskDescription;
    @Excel(name = "下发范围", width = 15)
    @ApiModelProperty(value = "下发范围")
    private String execScope;
    /**结束日期*/
    @Excel(name = "结束日期", width = 20, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期")
    private Date endDate;

    private Date taskStartTime;

    private Date taskCompleteTime;

    private String taskStatus;

    @Excel(name = "区域数量", width = 15)
    @ApiModelProperty(value = "区域数量")
    private String areaNum;

    @Excel(name = "位置点位数量", width = 15)
    @ApiModelProperty(value = "位置点位数量")
    private String locationNum;

    private List<scopeOfWork> scopeOfWorks;

    private List<areaData> areaDatas;

    private YTSafetyFileInfo attachmentImg;


    @Data
    public static class areaData {
        @Excel(name = "所属区域名称", width = 15)
        @ApiModelProperty(value = "所属区域名称")
        private String areaName;

        @Excel(name = "任务范围", width = 15)
        @ApiModelProperty(value = "任务范围")
        private List<scopeOfWork> scopeOfWorks;
    }

    @Data
    public static class scopeOfWork {

        @Excel(name = "所属厂区名称", width = 15)
        @ApiModelProperty(value = "所属厂区名称")
        private String factoryName;

        @Excel(name = "所属区域名称", width = 15)
        @ApiModelProperty(value = "所属区域名称")
        private String areaName;

        @Excel(name = "位置名称", width = 15)
        @ApiModelProperty(value = "位置名称")
        private String locationName;

        @Excel(name = "风险因素", width = 15)
        @ApiModelProperty(value = "风险因素")
        private String riskFactor;

        @Excel(name = "风险类别id", width = 15)
        @ApiModelProperty(value = "风险类别id")
        private Long riskCategoryId;

        @Excel(name = "风险类别名称", width = 15)
        @ApiModelProperty(value = "风险类别名称")
        private String riskCategoryName;

        @Excel(name = "风险识别维度", width = 15)
        @ApiModelProperty(value = "风险识别维度")
        private String riskIdentifyType;

        @Excel(name = "风险描述", width = 15)
        @ApiModelProperty(value = "风险描述")
        private String riskDescription;

        /**防范措施*/
        @Excel(name = "防范措施", width = 15)
        @ApiModelProperty(value = "防范措施")
        private String preventMeasure;

        /**防范措施*/
        @Excel(name = "风险等级", width = 15)
        @ApiModelProperty(value = "风险等级")
        private String riskLevel;

    }


}
