package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 权限全量初始化结果模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionInitResult implements Serializable {
    /** 初始化类型 */
    private String initType;
    /** 总员工数 */
    private int totalEmployees;
    /** 成功初始化数 */
    private int successCount;
    /** 失败数 */
    private int failCount;
    /** 去重删除数 */
    private int deduplicateCount;
    /** 状态 */
    private String status;
    /** 错误信息 */
    private String errorMessage;
}
