package com.yutong.safety.core.db.entity.goodsOut.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GoodsOutAuthListParam extends WebPageParam {

    @ApiModelProperty("姓名/员工号/eip账号")
    private String empStr;
    @ApiModelProperty("审核部门")
    private String empDept;
    @ApiModelProperty("出门类型")
    private String outType;

}
