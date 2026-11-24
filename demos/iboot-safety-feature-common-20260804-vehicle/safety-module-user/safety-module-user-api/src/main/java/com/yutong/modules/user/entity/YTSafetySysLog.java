package com.yutong.modules.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
@TableName("ytsafety_sys_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_sys_log对象", description="日志")
public class YTSafetySysLog {


    /**日志主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "日志主键")
    private java.lang.String id;
    /**日志类型*/
    @Excel(name = "日志类型", width = 15)
    @ApiModelProperty(value = "日志类型")
    private java.lang.Integer logType;
    /**日志内容*/
    @Excel(name = "日志内容", width = 15)
    @ApiModelProperty(value = "日志内容")
    private java.lang.String logContent;
    /**方法名称*/
    @Excel(name = "方法名称", width = 15)
    @ApiModelProperty(value = "方法名称")
    private java.lang.String method;
    /**操作类型*/
    @Excel(name = "操作类型", width = 15)
    @ApiModelProperty(value = "操作类型")
    private java.lang.Integer operateType;
    /**请求URL*/
    @Excel(name = "请求URL", width = 15)
    @ApiModelProperty(value = "请求URL")
    private java.lang.String requestUrl;
    /**请求类型*/
    @Excel(name = "请求类型", width = 15)
    @ApiModelProperty(value = "请求类型")
    private java.lang.String requestType;
    /**请求参数*/
    @Excel(name = "请求参数", width = 15)
    @ApiModelProperty(value = "请求参数")
    private java.lang.Object requestParam;
    /**操作IP*/
    @Excel(name = "操作IP", width = 15)
    @ApiModelProperty(value = "操作IP")
    private java.lang.String ip;
    /**用户ID*/
    @Excel(name = "用户ID", width = 15)
    @ApiModelProperty(value = "用户ID")
    private java.lang.String userid;
    /**用户名*/
    @Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
    private java.lang.String username;
    /**耗时*/
    @Excel(name = "耗时", width = 15)
    @ApiModelProperty(value = "耗时")
    private java.lang.Integer costTime;
    /**创建时间*/
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**创建人*/
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    /**租户ID*/
    @Excel(name = "租户ID", width = 15)
    @ApiModelProperty(value = "租户ID")
    private java.lang.Integer tenantId;

    @ApiModelProperty(value = "返回值/异常信息")
    private java.lang.String result;
}
