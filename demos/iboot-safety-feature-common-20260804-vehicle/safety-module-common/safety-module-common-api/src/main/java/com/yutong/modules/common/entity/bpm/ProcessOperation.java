package com.yutong.modules.common.entity.bpm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class ProcessOperation {

    /**
     * 任务类型
     */
    private String activityType;

    /**
     * 任务Id
     */
    private String taskId;

    /**
     * 当前节点
     */
    private String nodeId;

    /**
     * 任务预计处理人姓名
     */
    private String expectedName;

    /**
     * 操作列表
     */
    private List<ProcessOperationDetails> operations;
}
