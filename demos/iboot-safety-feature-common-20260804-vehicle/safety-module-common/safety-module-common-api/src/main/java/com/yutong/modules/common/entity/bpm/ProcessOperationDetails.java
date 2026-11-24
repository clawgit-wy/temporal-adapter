package com.yutong.modules.common.entity.bpm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class ProcessOperationDetails {

    /**
     *  操作标识
     */
    private String operationType;

    /**
     * 操作名称
     */
    private String operationName;

    /**
     * 操作类型（drafter, handler, admin, historyHandler）
     */
    private String operationHandlerType;

    private ProcessOperationParam params;

}
