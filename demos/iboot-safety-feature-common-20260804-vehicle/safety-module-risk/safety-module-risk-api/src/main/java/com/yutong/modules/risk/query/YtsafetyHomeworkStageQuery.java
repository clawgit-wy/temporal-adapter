package com.yutong.modules.risk.query;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class YtsafetyHomeworkStageQuery {


    /**作业环节名称*/
    @Excel(name = "作业环节名称")
    @ApiModelProperty(value = "作业环节名称")
    private String linkName;
    /**作业环节编码*/
    @Excel(name = "作业环节编码")
    @ApiModelProperty(value = "作业环节编码")
    private String linkCode;

    /**所属厂区id*/
    @Excel(name = "所属厂区id")
    @ApiModelProperty(value = "所属厂区id")
    private String factoryId;

    /**所属区域id(多选，逗号分割)*/
    @Excel(name = "所属区域id(多选，逗号分割)")
    @ApiModelProperty(value = "所属区域id(多选，逗号分割)")
    private String areaIds;

    /**所属位置id(多选，逗号分割)*/
    @Excel(name = "所属位置id(多选，逗号分割)")
    @ApiModelProperty(value = "所属位置id(多选，逗号分割)")
    private String locationName;

    /**所属组织id*/
    @Excel(name = "所属组织id")
    @ApiModelProperty(value = "所属组织id")
    private Long orgId;

    /**风险类别id*/
    @Excel(name = "风险类别id")
    @ApiModelProperty(value = "风险类别id")
    private Long riskTypeId;

    /**安全员*/
    @Excel(name = "安全员")
    @ApiModelProperty(value = "安全员")
    private String safetyStaff;
    /**安全主管*/
    @Excel(name = "安全主管")
    @ApiModelProperty(value = "安全主管")
    private String safetyManager;

    /**修改人姓名*/
    @Excel(name = "修改人姓名")
    @ApiModelProperty(value = "修改人姓名")
    private String updateName;


    @ApiModelProperty("台账ID列表（导出时使用，逗号分隔）")
    private String selections;
}
