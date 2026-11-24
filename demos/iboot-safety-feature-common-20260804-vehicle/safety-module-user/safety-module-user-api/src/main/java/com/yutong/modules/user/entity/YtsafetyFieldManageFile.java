package com.yutong.modules.user.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 字段扩展表
 * @Author: jeecg-boot
 * @Date:   2026-06-29
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_field_manage_file")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_field_manage_file对象", description="字段扩展表")
public class YtsafetyFieldManageFile {

    /**主键ID*/
    @ApiModelProperty(value = "主键ID")
    private Long id;
    /**文件路径*/
    @ApiModelProperty(value = "文件路径")
    private String fileUrl;
    /**文件id*/
    @ApiModelProperty(value = "文件id")
    private String fileId;

    /**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateUser;
    /**修改时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

}
