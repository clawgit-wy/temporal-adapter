package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 权限下发结果模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionIssueResult implements Serializable {
    /** 是否成功 */
    private boolean success;

    /** 结果消息 */
    private String message;

    /** 操作类型 */
    private String operationType;

    /** 校验结果: token是否存在 */
    private boolean tokenExists;

    /** 校验返回的人员信息(JSON字符串) */
    private String checkResult;

    /** 发行/改写返回结果(JSON字符串) */
    private String issueResult;

    /** 执行步骤列表 */
    private List<String> steps;

    /** 执行时长(毫秒) */
    private long duration;
}
