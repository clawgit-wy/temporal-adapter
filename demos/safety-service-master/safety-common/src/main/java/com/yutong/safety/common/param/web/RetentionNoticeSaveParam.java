package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * 滞留通知管理更新-入参
 * @author gwxiexj
 */
@Data
public class RetentionNoticeSaveParam implements Check {

    private Integer id;
    private Integer retentionHour;
    /**
     * 通知人员类型    ("员工车辆":"员工车辆",...) 【员工车辆、外协车辆等】其他类型找永盛确认
     */
    private String carType;
    private String content;
    /**
     * 提醒规则 （"等于":"等于","大于":"大于","大于等于":"大于等于","小于":"小于","小于等于":"小于等于"）
     */
    private String remindRule;
    private Integer remindNum;
    private Integer sendNum;
    private Integer remindGap;
    private String remindType;
    private Integer isUsed;
    private String updateBy;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(retentionHour), ResponseEnum.PARAM_ERROR.getCode(), "滞留通知时间不能为空");
        PreconditonsEx.checkArgument(Objects.nonNull(content), ResponseEnum.PARAM_ERROR.getCode(), "通知内容不能为空");
        PreconditonsEx.checkArgument(Objects.nonNull(sendNum), ResponseEnum.PARAM_ERROR.getCode(), "通知次数不能为空");
        PreconditonsEx.checkArgument(Objects.nonNull(remindGap), ResponseEnum.PARAM_ERROR.getCode(), "通知间隔不能为空");
        PreconditonsEx.checkArgument(Objects.nonNull(isUsed), ResponseEnum.PARAM_ERROR.getCode(), "是否启用不能为空");
    }
}
