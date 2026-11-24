package com.yutong.safety.core.db.entity.entry.param;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.core.db.entity.entry.vo.EntryBlackProcessVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants.ENTRY_BPM_TYPE_CREAT;
import static com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants.ENTRY_BPM_TYPE_REVIEW;

@Data
public class SafetyEntryBlankProcessDetailAppParams extends EntryBlackProcessVO implements Check {

    @ApiModelProperty("bpm 流程参数， 审核时必填")
    private String fdParameterJson;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(getCreateEmpNo()), ResponseEnum.PARAM_ERROR.getCode(), "提交人员工号 createEmpNo 不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(getCreateEmpName()), ResponseEnum.PARAM_ERROR.getCode(), "提交人员工姓名 createEmpName 不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(getReason()), ResponseEnum.PARAM_ERROR.getCode(), "进厂原因 reason 不能为空！");
    }
}
