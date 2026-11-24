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
public class NodeInfo {

    /**
     * 任务主键
     */
    private String taskId;
    /**
     * 节点编号
     */
    private String nodeId;

    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 任务类型
     */
    private String activityType;
    /**
     * 节点开始时间
     */
    private String startDate;
    /**
     * 节点定义处理人登录名
     */
    private String handlers;

    /**
     * 节点定义处理人名称
     */
    private String handlerNames;
    /**
     * 审批类型
     */
    private String processType;

    /**
     * 说明
     */
    private String desc;

    /**
     * 节点流向
     */
    private List<Destinations> destinations;

    /**
     * 扩展属性
     */
    private NodeExtAttributes extAttributes;

    /**
     * 节点实例工作项信息
     */
    private List<WorkItems>  workitems;
}

