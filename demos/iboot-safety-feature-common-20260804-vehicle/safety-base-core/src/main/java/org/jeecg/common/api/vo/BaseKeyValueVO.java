package org.jeecg.common.api.vo;

import lombok.Data;

@Data
public class BaseKeyValueVO {
    /**
     * key值
     */
    private String code;

    /**
     * value值
     */
    private String value;
}
