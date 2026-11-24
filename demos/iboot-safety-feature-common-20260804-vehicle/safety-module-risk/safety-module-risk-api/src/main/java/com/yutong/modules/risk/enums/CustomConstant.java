package com.yutong.modules.risk.enums;

/**
 * 自定义常量
 */
public class CustomConstant {

//  任务类型常量

    public static class TaskType {
        /** 巡查任务 */
        public static final String XCRW = "XCRW";

        /** 专项任务 */
        public static final String ZXRW = "ZXRW";

        /** 定点巡更任务 */
        public static final String DDXG = "DDXG";

        /** 业务验证 */
        public static final String YWYZ = "YWYZ";

        /** 岗位验证 */
        public static final String GWYZ = "GWYZ";

    }

    /**
     * 执行类型常量
     */
    public static class ExecType {
        /** 单次 */
        public static final String ONCE = "ONCE";

        /** 按天 */
        public static final String DAILY = "DAILY";

        /** 按周 */
        public static final String WEEKLY = "WEEKLY";

        /** 按月 */
        public static final String MONTHLY = "MONTHLY";

        /** 自定义 */
        public static final String CUSTOM = "CUSTOM";
    }

    /**
     * 任务层级常量
     */
    public static class TaskLevel {
        /** 公司 */
        public static final String COMPANY = "COMPANY";

        /** 部门 */
        public static final String DEPARTMENT = "DEPARTMENT";

        /** 单元 */
        public static final String UNIT = "UNIT";

        /** 工段 */
        public static final String SECTION = "SECTION";

        /** 班组 */
        public static final String TEAM = "TEAM";
    }

    public static class TaskStatus {

        /** 待执行 */
        public static final String PENDING = "PENDING";

        /** 执行中 */
        public static final String RUNNING = "RUNNING";

        /** 已过期 */
        public static final String EXPIRED = "EXPIRED";

        /** 已休眠 */
        public static final String DORMANT = "DORMANT";

        /** 审批中 */
        public static final String APPROVAL = "APPROVAL";

        /** 已驳回 */
        public static final String REJECT = "REJECT";

        /** 已废弃 */
        public static final String DISCARDED = "DISCARDED";

    }

    public static class TaskExecutionStatus {

        /** 已完成 */
        public static final String COMPLETED = "COMPLETED";

        /** 执行中 */
        public static final String RUNNING = "RUNNING";

        /** 超期完成 */
        public static final String OVERDUE_COMPLETED = "OVERDUE_COMPLETED";

        /** 超期未完成 */
        public static final String OVERDUE_UNCOMPLETED = "OVERDUE_UNCOMPLETED";
    }

    public static class ApprovalType {

        /** 不审批（跳过审批） */
        public static final String NO_APPROVAL = "1";

        /** 发起人审批 */
        public static final String INITIATOR_APPROVAL = "2";

        /** 消防员审批 */
        public static final String FIREFIGHTER_APPROVAL = "3";

        /** 自定义审批 */
        public static final String CUSTOM_APPROVAL = "4";
    }


}
