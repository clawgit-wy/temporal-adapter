package com.yutong.safety.common.param.app;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-10-10 16:19
 */
@Data
public class PersonStateAddParam implements Check  {
    private Long id;
    private Integer state;
    private Integer continueType;
    private String startDate;
    private String endDate;
    private Integer replyState;
    private String replyContent;
    private String username;
    @Override
    public void check() {
        PreconditonsEx.checkArgument(state >= 0, ResponseEnum.PARAM_ERROR.getCode(), "状态类型不能为空");
        PreconditonsEx.checkArgument(Objects.nonNull(continueType), ResponseEnum.PARAM_ERROR.getCode(), "持续类型不能为空");
        PreconditonsEx.checkArgument(replyState >= 0, ResponseEnum.PARAM_ERROR.getCode(), "回复状态不能为空");
        if(replyState > 0) {
            PreconditonsEx.checkArgument(StringUtils.isNotEmpty(replyContent), ResponseEnum.PARAM_ERROR.getCode(), "自动回复内容不能为空");
        }
    }
}
