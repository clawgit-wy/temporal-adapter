package com.yutong.safety.common.vo;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * fdParameterJson 参数
 * 数据不全， 可以进行补充
 */
@Data
public class BpmOperationVO {
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 流程实例ID
     */
    private String processId;
    /**
     * 任务类型
     */
    private String activityType;
    /**
     * 操作标识
     * drafter_submit:提交操作
     * handler_pass:通过操作
     * handler_refuse：驳回操作
     * handler_abandon:废弃操作
     */
    private String operationType;
    /**
     * 其他参数
     */
    private Param param;

    @Data
    public static class Param {
        /**
         * 操作中文名称
         * 通过、驳回、废弃
         */
        private String operationName;
        /**
         * 通知方式, 此字段暂时没用，需要额外的进行解析，所以去掉
         * 如果要用，需要加上 replace("\"notifyType\":\"{\"", "\"notifyType\":{\"").replace("\"}\",", "\"},")
         */
//        private NotifyType notifyType;
        /**
         * true/false是否流程结束后通知我
         */
        private Boolean notifyOnFinish;
        private Boolean notifyForFollow;
        /**
         * 审批意见（中文长度限制2000）
         */
        private String auditNote;
        private String auditNoteFdId;
        /**
         * 驳回目标的节点编号
         */
        private String jumpToNodeId;
        /**
         * 下一节点为人工决策分支必填
         */
        private String futureNodeId;
        /**
         * 驳回通过后是否直接返回本节点
         */
        private Boolean refusePassedToThisNode;

        private String jumpToNodeInstanceId;
        private Boolean refusePassedToThisNodeOnNode;
        private Boolean refusePassedToTheNode;
        private String identityId;
        private String dayOfNotifyDrafter;
        private String hourOfNotifyDrafter;
        private String minuteOfNotifyDrafter;


    }
    @Data
    public static class NotifyType {

        private String N2;

        private String N4;
    }

    public static void main(String[] args) {
        String s = "{\n" +
                "\"taskId\":\"19a476a93d2486b488b3c214a0cb5484\",\n" +
                "\"processId\":\"19a472a63739bc33106c8a741cc8e36a\",\n" +
                "\"activityType\":\"draftWorkitem\",\n" +
                "\"operationType\":\"drafter_submit\",\n" +
                "\"param\":{\n" +
                "\t\"operationName\":\"提交文档\",\n" +
                "\t\"notifyType\":\"{\"N4\":\"todo;email\"}\",\n" +
                "\t\"notifyForFollow\":false,\n" +
                "\t\"auditNote\":\"\"\n" +
                "\n" +
                "}\n" +
                "}";
        s = s.replace("\"notifyType\":\"{\"", "\"notifyType\":{\"").replace("\"}\",", "\"},");
        BpmOperationVO bpmOperationVO = JSON.parseObject(s, BpmOperationVO.class);
        System.out.println(bpmOperationVO);
    }
}
