package com.yutong.modules.common.entity.bpm;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class FlowLog {

    private String factNodeId;

    private String factNodeName;

    private String actionName;

    private String actionInfo;

    private String createTime;

    private String nodeId;

    private String workItemId;

    private String expecter;

    private String authorizer;

    private String authorizeType;

    private String handler;

    private String handlerName;

    private String notifyType;
}
