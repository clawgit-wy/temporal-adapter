package org.jeecg.common.util;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Data
public class PicParam implements Check {
    private MultipartFile file;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(file), ResponseEnum.PARAM_ERROR.getCode(), "文件不能为空！");
    }
}
