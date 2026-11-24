package com.yutong.modules.common.entity.bpm.flowinfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class FlowApprover {


    /**
     * msg
     */
    private String msg;


    /**
     * 流程信息
     */
    private String data;
}
