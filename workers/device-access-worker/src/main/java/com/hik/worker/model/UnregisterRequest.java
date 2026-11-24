package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 权限注销请求模型
 * 对应富士 CRK 接口 /api/TokenService/Unregister
 * 用于注销人脸和卡权限，需对3个固定GID分别调用
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnregisterRequest implements Serializable {
    /**
     * 子系统列表，固定为 [2]
     */
    private List<Integer> SubSystems;
    /**
     * 令牌类型: 1=人脸, 0=卡
     */
    private int TokenType;
    /**
     * 令牌操作: 12=注销
     */
    private int TokenOper;
    /**
     * 令牌值: 人脸时为手机号, 卡时为卡号
     */
    private String Token;
    /**
     * 注销日期
     */
    private String Redate;
    /**
     * 项目GID
     */
    private String Gid;
    /**
     * 项目GID列表
     */
    private List<String> ProjectGids;
    /**
     * 备注
     */
    private String Remark;
}
