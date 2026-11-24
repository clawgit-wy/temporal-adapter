package com.yutong.modules.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;

@Data
@TableName("ytsafety_file_info")
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ytsafety_file_info对象", description = "文件信息管理表")
public class YTSafetyFileInfo {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    @Excel(name = "第三方文件ID", width = 15)
    @ApiModelProperty(value = "第三方文件ID")
    private String fileId;

    @Excel(name = "原始文件名", width = 15)
    @ApiModelProperty(value = "原始文件名")
    private String fileName;

    @Excel(name = "文件路径", width = 15)
    @ApiModelProperty(value = "文件路径")
    private String filePath;

    @Excel(name = "文件后缀", width = 15)
    @ApiModelProperty(value = "文件后缀")
    private String fileType;

    @Excel(name = "文件大小", width = 15)
    @ApiModelProperty(value = "文件大小(字节)")
    private Long fileSize;

    @Excel(name = "功能分类", width = 15)
    @ApiModelProperty(value = "功能分类")
    private String tag;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "版本号")
    private String version;


    @TableLogic(value = "0", delval = "1")
    @ApiModelProperty(value = "删除标识: 0-未删除, 1-已删除")
    private Integer delFlag;

    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private String createUser;

    @Excel(name = "创建人姓名", width = 15)
    @ApiModelProperty(value = "创建人姓名")
    private String createName;

    @ApiModelProperty(value = "更新人")
    private String updateUser;

    @ApiModelProperty(value = "更新人姓名")
    private String updateName;

    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
