package com.yutong.modules.user.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ApiModel(value="ytsafety_sys_log对象", description="日志")
public class YTSafetySysLogVO {

    /**日志类型*/
    /**
     * 日志类型（1登录日志，2操作日志）
     */
    @Excel(name = "日志类型", width = 15)
    @ApiModelProperty(value = "（1登录日志，2操作日志 3变更日志）")
    private Integer logType;
    /**日志内容*/
    @Excel(name = "日志内容", width = 15)
    @ApiModelProperty(value = "日志内容")
    private String logContent;
    /**方法名称*/
    @Excel(name = "方法名称", width = 15)
    @ApiModelProperty(value = "方法名称")
    private String method;
    /**操作类型*/
    /**
     * 操作类型（1查询，2添加，3修改，4删除,5导入，6导出）
     */
    @Excel(name = "操作类型", width = 15)
    @ApiModelProperty(value = "操作类型 （1查询，2添加，3修改，4删除,5导入，6导出）")
    private Integer operateType;
    /**请求URL*/
    @Excel(name = "请求URL", width = 15)
    @ApiModelProperty(value = "请求URL")
    private String requestUrl;
    /**请求类型*/
    @Excel(name = "请求类型", width = 15)
    @ApiModelProperty(value = "请求类型")
    private String requestType;
    /**请求参数*/
    @Excel(name = "请求参数", width = 15)
    @ApiModelProperty(value = "请求参数")
    private Object requestParam;
    /**操作IP*/
    @Excel(name = "操作IP", width = 15)
    @ApiModelProperty(value = "操作IP")
    private String ip;
    /**用户ID*/
    @Excel(name = "用户ID", width = 15)
    @ApiModelProperty(value = "用户ID")
    private String userid;
    /**用户名*/
    @Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
    private String username;
    /**耗时*/
    @Excel(name = "耗时", width = 15)
    @ApiModelProperty(value = "耗时")
    private Integer costTime;
    /**创建时间*/
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**创建人*/
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**租户ID*/
    @Excel(name = "租户ID", width = 15)
    @ApiModelProperty(value = "租户ID")
    private Integer tenantId;
}
