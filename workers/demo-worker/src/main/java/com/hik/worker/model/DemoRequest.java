package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * Demo 工作流请求模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoRequest implements Serializable {
    /** 工作流ID */
    private String workflowId;
    /** 人员ID */
    private String personId;
    /** 姓名 */
    private String name;
    /** 电话 */
    private String phone;
}
