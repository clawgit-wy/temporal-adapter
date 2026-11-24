package com.yutong.modules.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ApiModel(value="ytsafety_sys_log对象", description="日志")
public class YTSafetySysLogDTO {

    /**日志类型*/
    @Excel(name = "日志类型", width = 15)
    @ApiModelProperty(value = "日志类型 （1登录日志，2操作日志）")
    private Integer logType;
    /**日志内容*/
    @Excel(name = "日志内容", width = 15)
    @ApiModelProperty(value = "日志内容")
    private String logContent;

    /**操作类型*/
    @Excel(name = "操作类型", width = 15)
    @ApiModelProperty(value = "操作类型  （1查询，2添加，3修改，4删除,5导入，6导出）")
    private Integer operateType;

    /**请求类型*/
    @Excel(name = "请求类型", width = 15)
    @ApiModelProperty(value = "请求类型")
    private String requestType;

    /**用户名*/
    @Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
    private String username;

    /**创建时间*/
    @Excel(name = "开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间 yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @Excel(name = "结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间 yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty(value = "分页参数")
    private Integer pageNo = 0;

    @ApiModelProperty(value = "分页参数")
    private Integer pageSize = 10;


}
