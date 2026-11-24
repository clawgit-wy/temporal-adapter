package com.yutong.safety.core.db.entity.goodsOut.param;

import com.yutong.safety.common.param.Check;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsOutAuthAddParam implements Serializable {

    private Integer id;
    @ApiModelProperty("审核人员工号")
    private String empNo;
    @ApiModelProperty("审核人姓名")
    private String empName;
    @ApiModelProperty("审核人EIP账号")
    private String empEip;
    @ApiModelProperty("审核部门")
    private String empDept;
    @ApiModelProperty("厂区")
    private String powerFactoryId;
    @ApiModelProperty("物资出厂权限描述")
    private String powerDesc;
    @ApiModelProperty("出门类型")
    private String outType;
    @ApiModelProperty("物资类别")
    private String goodsType;
    @ApiModelProperty("更新人")
    private String modifyBy;
}
