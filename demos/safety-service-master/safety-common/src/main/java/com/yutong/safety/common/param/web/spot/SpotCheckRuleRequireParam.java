package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;

import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-5-10 11:29
 */
@Data
public class SpotCheckRuleRequireParam implements Check {
    private Integer id;

    /**
     * 正常图片上传-是否必填
     */
    private Boolean normalImgRequire;
    /**
     * 异常上传图片-是否必填
     */
    private Boolean exceptionImgRequire;
    /**
     * 整改上传图片-是否必填 0-否 1-是
     */
    private Boolean reformImgRequire;
    /**
     * 验证上传图片-是否必填 0-否 1-是
     */
    private Boolean validateImgRequire;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(id), ResponseEnum.PARAM_ERROR.getCode(), "id不能为空！");
    }
}
