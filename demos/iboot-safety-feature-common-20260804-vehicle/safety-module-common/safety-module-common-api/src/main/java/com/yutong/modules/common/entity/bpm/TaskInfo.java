package com.yutong.modules.common.entity.bpm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class TaskInfo {
    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务类型
     */
    private String activityType;

    /**
     * 节点名（处理人身份名）
     */
    private String roleName;

}
