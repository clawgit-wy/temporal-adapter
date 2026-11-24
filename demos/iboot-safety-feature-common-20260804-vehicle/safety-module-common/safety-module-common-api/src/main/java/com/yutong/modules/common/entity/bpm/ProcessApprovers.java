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
public class ProcessApprovers {

    /**
     * 审批人记录
     */
    private List<ProcessApprover> approverRecord;

    /**
     * 当前审批人
     */
    private List<ProcessApprover> curApprover;
}
