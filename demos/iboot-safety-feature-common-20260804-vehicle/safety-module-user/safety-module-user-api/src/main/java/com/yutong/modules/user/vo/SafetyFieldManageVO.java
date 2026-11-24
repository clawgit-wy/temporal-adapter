package com.yutong.modules.user.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 厂区表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SafetyFieldManageVO {
    
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
	private Long id;
    /**名称*/
    @Excel(name = "厂区编码", width = 15)
    @ApiModelProperty(value = "厂区编码")
    private String fieldCode;
	/**名称*/
	@Excel(name = "厂区名称", width = 15)
    @ApiModelProperty(value = "厂区名称")
	private String fieldName;

    /**图片信息*/
    @Excel(name = "图片信息", width = 15)
    @ApiModelProperty(value = "图片信息")
    private String fileUrl;
}
