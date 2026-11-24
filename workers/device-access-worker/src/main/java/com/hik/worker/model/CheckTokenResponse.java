package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 发行校验响应
 * code: 200=存在发行信息, 500=不存在
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckTokenResponse implements Serializable {
    private int code;
    private String msg;
    private String serverTime;
    private Object result;
}
