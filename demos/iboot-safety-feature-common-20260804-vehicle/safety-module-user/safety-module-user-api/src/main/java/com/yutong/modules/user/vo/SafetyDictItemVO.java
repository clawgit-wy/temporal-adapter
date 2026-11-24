package com.yutong.modules.user.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class SafetyDictItemVO {


    /**bigint*/
    @Excel(name = "bigint", width = 15)
    @ApiModelProperty(value = "bigint")
    @TableId(type = IdType.AUTO)
    private Long id;
    /**字典id*/
    @Excel(name = "字典id", width = 15)
    @ApiModelProperty(value = "字典id")
    private Long supperId;
    /**字典项文本*/
    @Excel(name = "字典子项目名称", width = 15)
    @ApiModelProperty(value = "字典子项目名称")
    private String itemName;
    /**字典项值*/
    @Excel(name = "字典子项目编码", width = 15)
    @ApiModelProperty(value = "字典子项目编码")
    private String itemCode;

    /**排序*/
    @Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private Integer itemOrder;

    private Long pid;

    private String type;
    private String fileUrl;

    private List<SafetyDictItemVO> children;

}
