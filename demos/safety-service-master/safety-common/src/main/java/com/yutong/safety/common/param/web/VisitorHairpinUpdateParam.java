package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * create by gwcuiyx on 2023.07.17
 */
@Data
public class VisitorHairpinUpdateParam implements Check {
    private Integer id;
    private String hairpinNo;
    private String recoveryNo;
    private String visitedName;
    private String visitedPhone;
    private String visitorName;
    private String visitorPhone;
    private String visitorIdCard;
    private String vendorUnit;
    private String vendorMatter;
    private String cardNo;
    private String startTime;
    private String endTime;
    private String photo;
    private Integer secretState;
    private String secretRegion;
    private Integer vendorState;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(id), ResponseEnum.PARAM_ERROR.getCode(), "访客id不能为空");
    }

}
