package com.yutong.modules.risk.enums;

/**
 * 隐患上报常量
 */
public class HazardReportConstant {

    public static final String MODEL_ID = "hazard_report_process";

    //节点类型常量
    public static class NodeType {
        /** 审核 */
        public static final String CHECK = "CHECK";

        /** 整改 */
        public static final String ABARBEITUNG = "ABARBEITUNG";

        /** 延期审批 */
        public static final String DELAY_CHECK = "DELAY_CHECK";

        /** 验证一 */
        public static final String VERIFY_ONE = "VERIFY_ONE";

        /** 验证二 */
        public static final String VERIFY_TWO = "VERIFY_TWO";

        /** 备案 */
        public static final String RECORD = "RECORD";

    }

    //节点角色范围类型
    public static class ScopeType {
        /** 通用 */
        public static final String UNIVERSAL = "UNIVERSAL";

        /** 部门 */
        public static final String DEPT = "DEPT";

        /** 厂区 */
        public static final String FACTORY = "FACTORY";

        /** 部门加厂区 */
        public static final String DEPT_FACTORY = "DEPT_FACTORY";

    }

    //问题来源
    public static class ProblemSourceType {
        /** 通用 */
        public static final String UNIVERSAL = "UNIVERSAL";

        /** 公司级检查 */
        public static final String COMPANY_INSPECTION = "COMPANY_INSPECTION";

        /** 部门自查 */
        public static final String DEPARTMENT_SELF_CHECK = "DEPARTMENT_SELF_CHECK";

        /** 员工随手拍 */
        public static final String EMPLOYEE_SNAPSHOT = "EMPLOYEE_SNAPSHOT";

        /** 巡查异常 */
        public static final String PATROL_EXCEPTION = "PATROL_EXCEPTION";

    }

    //隐患类别
    public static class HazardCategoryType {
        /** 通用 */
        public static final Integer UNIVERSAL = 0;

        /** 消防 */
        public static final Integer XF = 1;

        /** 治安 */
        public static final Integer ZA = 2;

    }

    //隐患类别 **问题等级：2-一般，3-较大，4-重大
    public static class ProblemLevelType {
        /** 通用 */
        public static final Integer UNIVERSAL = 0;

        /** 一般 */
        public static final Integer GENERAL = 2;

        /** 较大 */
        public static final Integer GREATER = 3;

        /** 重大 */
        public static final Integer SERIOUS = 4;

    }

    //优先级
    public static class PriorityType {
        /** 通用 */
        public static final Integer UNIVERSAL = 0;

        /** 消防 */
        public static final Integer ONE = 1;

        /** 治安 */
        public static final Integer TWO = 2;

    }

}
