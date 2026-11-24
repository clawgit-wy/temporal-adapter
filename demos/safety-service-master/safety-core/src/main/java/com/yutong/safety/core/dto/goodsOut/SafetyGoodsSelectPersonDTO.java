package com.yutong.safety.core.dto.goodsOut;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author caomg
 * @date 2024-9-24 9:28
 * @description SafetyGoodsSelectPersonDTO
 */
@Data
@ApiModel
public class SafetyGoodsSelectPersonDTO extends WebPageParam {
    @ApiModelProperty("个人员工号(web端必传)")
    private String ownerEmpNo;

    @ApiModelProperty("出厂厂区")
    private String outFactory;

    @ApiModelProperty("出门类型")
    private String outType;

    @ApiModelProperty("搜索内容")
    private String searchContent;

    @ApiModelProperty("登陆人手机号(个人员工号和登陆人手机号二者不能同时为空)")
    private String phoneNum;

    @ApiModelProperty("触发标识 1-EIP 2-掌上宇通 3-掌宇小程序")
    private String systemFlag;
}
