package com.yutong.safety.core.db.entity.dict;

import com.yutong.safety.common.param.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("字典父项")
public class SafetyDictSupperDO extends Page implements Serializable {
    private static final long serialVersionUID =  3667734560207079259L;

    /**
     * 记录ID
     */
    @ApiModelProperty("记录ID")
    private Long id;

    /**
     * 数据字典编号
     */
    @ApiModelProperty("数据字典编号(保存更新时候不能为空)")
    private String supperCode;

    /**
     * 数据字典名称
     */
    @ApiModelProperty("数据字典名称(保存更新时候不能为空)")
    private String supperName;

    /**
     * 数据字典描述
     */
    @ApiModelProperty("数据字典描述")
    private String supperDesc;

    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    private String updateUser;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private Date updateTime;

    /**
     * 删除状态：0正常1删除
     */
    @ApiModelProperty("删除状态：0正常1删除(保存更新时候不能为空)")
    private String del;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer supperOrder;
}