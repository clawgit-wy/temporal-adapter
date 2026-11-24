package com.yutong.modules.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yutong.modules.user.entity.SafetyFieldManage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class SafetyDictItemDTO {

    /**bigint*/
    @Excel(name = "bigint", width = 15)
    @ApiModelProperty(value = "bigint")
    @TableId(type = IdType.AUTO)
    private Long id;
    /**字典id*/
    @Excel(name = "字典id", width = 15)
    @ApiModelProperty(value = "字典id")
//    @NotNull(message = "字典id不能为空")
    private Long supperId;
    /**字典编码*/
    @Excel(name = "字典编码", width = 15)
    @ApiModelProperty(value = "字典编码")
//    @NotBlank(message = "字典编码不能为空")
    private String supperCode;
    /**字典子项目名称*/
    @Excel(name = "字典子项目名称", width = 15)
    @ApiModelProperty(value = "字典子项目名称")
    @NotBlank(message = "字典子项目名称不能为空")
    private String itemName;
    /**字典子项目编码*/
    @Excel(name = "字典子项目编码", width = 15)
    @ApiModelProperty(value = "字典子项目编码")
//    @NotBlank(message = "字典子项目编码不能为空")
    private String itemCode;
    /**排序*/
    @Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private Integer itemOrder;

    @ApiModelProperty(value = "父级id")
    private Long pid;

    /**类型*/
    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "字段类型")
    private String fieldType;

    @ApiModelProperty(value = "文件路径")
    private String fileUrl;

    @ApiModelProperty(value = "文件id")
    private String fileId;

}
