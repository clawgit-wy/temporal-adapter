package com.hik.adapter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 单人校验结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonResult implements Serializable {

    /** 人员ID */
    private String personId;

    /** 是否校验通过 */
    private boolean passed;

    /** 错误信息（校验失败时） */
    private String errorMsg;

    /** 海康返回的原始响应 */
    private String rawResponse;

    public static PersonResult success(String personId, String rawResponse) {
        return PersonResult.builder()
                .personId(personId)
                .passed(true)
                .rawResponse(rawResponse)
                .build();
    }

    public static PersonResult fail(String personId, String errorMsg) {
        return PersonResult.builder()
                .personId(personId)
                .passed(false)
                .errorMsg(errorMsg)
                .build();
    }
}
