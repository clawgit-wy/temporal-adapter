package com.yutong.safety.common.param.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * 公用导入参数
 * create by gwcuiyx on 2023.07.17
 */
@Data
public class CommonImportParam implements Check {
    private MultipartFile file;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(file), ResponseEnum.PARAM_ERROR.getCode(), "导入文件不能为空");
    }
}
