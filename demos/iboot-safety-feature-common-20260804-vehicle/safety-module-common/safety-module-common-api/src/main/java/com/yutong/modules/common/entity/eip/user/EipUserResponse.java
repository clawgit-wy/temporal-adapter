package com.yutong.modules.common.entity.eip.user;

import lombok.Data;

/**
 * EIP 用户中心-通用返回
 *
 * @param <T> result 结果类型，例如 {@link EipUserResult}
 */
@Data
public class EipUserResponse<T> {

    /**是否成功*/
    private boolean success;
    /**返回消息*/
    private String message;
    /**返回码*/
    private Integer code;
    /**返回结果*/
    private T result;
}
