package com.yutong.safety.core.db.entity.visitor.vo;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class SafetyVisitorIdCardVO implements Check {
    @ApiModelProperty("人脸照片")
    private String facePhoto;

    @ApiModelProperty("身份证照片")
    private String idCardPhoto;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(facePhoto), ResponseEnum.FAIL.getCode(), "人脸照片为空");
        PreconditonsEx.checkArgument(StringUtils.isNotBlank(idCardPhoto), ResponseEnum.FAIL.getCode(), "身份证照片为空");
    }
}
