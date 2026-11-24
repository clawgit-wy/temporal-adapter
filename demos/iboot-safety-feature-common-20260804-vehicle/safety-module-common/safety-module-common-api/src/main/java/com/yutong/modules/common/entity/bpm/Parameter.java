package com.yutong.modules.common.entity.bpm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class Parameter {

    /**
     * 任务主键
     */
    private String taskId;

    /**
     * 任务类型
     */
    private String activityType;

    /**
     * 操作参数
     */
    private String operationType;

    /**
     * 操作参数
     */
    private Operation param;
}
