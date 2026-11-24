package com.yutong.safety.core.db.entity.goodsOut.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 出门证app段检索参数封装
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsOutAppListParam extends WebPageParam {

    @ApiModelProperty("流程编号")
    private String flowNo;

    @ApiModelProperty("出门人-姓名/员工号/联系电话")
    private String goOutEmp;

    @ApiModelProperty("当前登录人-EIP用户名")
    private String curPersonLogin;

    @ApiModelProperty("检索类型 1-待放行 2-放行记录 3-放行异常 ")
    private String searchType;

    @ApiModelProperty("流程节点限制-后端使用")
    private String curNodeId;

}
