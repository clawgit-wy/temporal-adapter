package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 权限注销响应模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnregisterResponse implements Serializable {
    private int code;
    private String msg;
    private String serverTime;
    private Object result;
}
