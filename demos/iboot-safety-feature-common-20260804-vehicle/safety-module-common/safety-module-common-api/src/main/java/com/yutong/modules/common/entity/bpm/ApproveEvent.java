package com.yutong.modules.common.entity.bpm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class ApproveEvent {

    private boolean isCreateFlow;

    private Long formInstanceId;

    private String formId;

    private String processId;

    private String curNodeId;

    public ApproveEvent(Long formInstanceId, String formId, String processId, String curNodeId,boolean isCreateFlow) {
        this.formInstanceId = formInstanceId;
        this.formId = formId;
        this.processId = processId;
        this.curNodeId = curNodeId;
        this.isCreateFlow = isCreateFlow;
    }
}
