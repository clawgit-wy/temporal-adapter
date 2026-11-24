package com.hik.mokapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 校验响应（模拟海康接口响应体）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyResponse implements Serializable {

    /** 是否校验通过 */
    private boolean passed;

    /** 错误信息（校验失败时） */
    private String errorMsg;

    /** 人员ID */
    private String personId;

    /** 模拟的相似度分数 */
    private Double similarity;
}
