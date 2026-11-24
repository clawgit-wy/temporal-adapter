package com.yutong.safety.core.db.entity.dict;

import com.yutong.safety.common.param.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("字典子项")
public class SafetyDictItemDO extends Page implements Serializable {
    private static final long serialVersionUID =  1692355157594001868L;

    /**
     * 记录ID
     */
    @ApiModelProperty("记录ID")
    private Long id;

    /**
     * 字典子项目编码
     */
    @ApiModelProperty(value = "字典子项目编码(保存更新时候不能为空)")
    private String itemCode;

    /**
     * 字典子项目名称
     */
    @ApiModelProperty("字典子项目名称(保存更新时候不能为空)")
    private String itemName;

    /**
     * 数据字典ID
     */
    @ApiModelProperty("数据字典ID(保存更新时候不能为空)")
    private Long supperId;

    /**
     * 数据字段编码
     */
    @ApiModelProperty("数据字段编码(保存更新时候不能为空)")
    private String supperCode;

    /**
     * 最后更新人ID
     */
    @ApiModelProperty("最后更新人ID")
    private String updateUser;

    /**
     * 最后更新时间
     */
    @ApiModelProperty("最后更新时间")
    private Date updateTime;

    /**
     * 状态：0编辑状态1使用状态2注销状态
     */
    @ApiModelProperty("状态：0编辑状态1使用状态2注销状态(保存更新时候不能为空)")
    private String isUse;

    /**
     * 记录排序
     */
    @ApiModelProperty("记录排序")
    private Long itemOrder;

    @ApiModelProperty("展示标记-用于supperCode值一样，但是需要分开展示")
    private String showFlag;
}