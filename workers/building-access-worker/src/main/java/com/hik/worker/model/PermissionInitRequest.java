package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 权限全量初始化请求模型
 * 从第三方系统拉取所有员工门禁权限并初始化本地数据库
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionInitRequest implements Serializable {
    /** 初始化类型: XKP=新开普, CRK=富士, ALL=全部 */
    private String initType;
    /** 分页大小 */
    private int pageSize;
    /** 并行页数 */
    private int parallelPages;
    /** 是否去重 */
    private boolean deduplicate;
    /** 厂区编码列表（限定初始化范围） */
    private List<String> factoryCodes;
}
