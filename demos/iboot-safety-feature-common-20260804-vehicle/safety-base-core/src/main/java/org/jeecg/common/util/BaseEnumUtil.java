package org.jeecg.common.util;

import org.jeecg.common.api.BaseEnum;
import org.jeecg.common.api.vo.BaseKeyValueVO;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BaseEnumUtil {

    public static <T extends Enum<T> & BaseEnum> List<BaseKeyValueVO> buildInfo(Class<T> enumClass) {
        // 该方法内部实现构造逻辑
        return Arrays.stream(enumClass.getEnumConstants())
                .map(enumItem -> {
                    BaseKeyValueVO info = new BaseKeyValueVO();
                    info.setCode(String.valueOf(enumItem.getCode()));
                    info.setValue(enumItem.getValue());
                    return info;
                })
                .collect(Collectors.toList());
    }

}
