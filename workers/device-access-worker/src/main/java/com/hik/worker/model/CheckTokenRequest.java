package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 发行校验请求（接口1: CheckTokenState）
 * TokenType: 0=卡, 1=人脸
 * Token: TokenType=0时传卡号, TokenType=1时传staffNo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckTokenRequest implements Serializable {
    private int TokenType;
    private String Token;
}
