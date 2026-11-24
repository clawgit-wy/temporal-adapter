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
public class SafetyDictSupperVO {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Long id;
    /**字典名称*/
    @Excel(name = "字典名称", width = 15)
    @ApiModelProperty(value = "字典名称")
    private String supperName;
    /**字典编码*/
    @Excel(name = "字典编码", width = 15)
    @ApiModelProperty(value = "字典编码")
    private String supperCode;
    /**描述*/
    @Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String supperDesc;

    /**更新人*/
    @Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
    private String updateUser;
    /**更新时间*/
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @Excel(name = "类型", width = 15)
    private String type;
/*
    *//**字典类型0为string,1为number*//*
    @Excel(name = "字典类型0为string,1为number", width = 15)
    @ApiModelProperty(value = "字典类型0为string,1为number")
    private Integer type;*/

//    private List<SafetyDictItemVO> dictItemVOList;
}
