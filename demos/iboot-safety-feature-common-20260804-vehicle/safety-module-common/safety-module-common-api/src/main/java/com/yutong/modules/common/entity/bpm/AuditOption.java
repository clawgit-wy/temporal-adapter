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
public class AuditOption {

    /**
     *
     */
    private String fdId;

    /**
     * 节点在流程图中唯一标识
     */
    private String factNodeId;

    /**
     * 节点名称
     */
    private String factNodeName;

    /**
     * 操作名
     */
    private String actionName;

    /**
     * 操作信息
     */
    private String actionInfo;

    /**
     * 审批意见
     */
    private String auditNote;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 节点在数据库存储的唯一标识
     */
    private String nodeId;

    /**
     * 所属工作项在数据库存储的唯一标识
     */
    private String workItemId;

    /**
     * 实际处理人登陆名
     */
    private String handler;

    /**
     * 实际处理人名称
     */
    private String handlerName;

    private List<NoteAttachments> attachments;
}
