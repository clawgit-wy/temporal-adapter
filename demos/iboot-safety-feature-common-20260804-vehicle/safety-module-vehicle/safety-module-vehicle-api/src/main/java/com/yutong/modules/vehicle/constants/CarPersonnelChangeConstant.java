package com.yutong.modules.vehicle.constants;

/**
 * 车辆人员变更流程常量
 */
public class CarPersonnelChangeConstant {

    public static final String MODEL_ID = "car_personnel_change_process";

    //人员变更
    public static class ReplacePersonnel {
        /** 不更换 */
        public static final String NO_CHANGE = "0";

        /** 更换 */
        public static final String CHANGE = "1";

    }

    //调整类型
    public static class HandlerType {
        /** 调整 */
        public static final String ADJUSTMENT = "ADJUSTMENT";

        /** 未调整 */
        public static final String UNCORRECTED = "UNCORRECTED";

    }

    //人员变更类型
    public static class PersonnelChangeType {
        /** 离职 */
        public static final String DIMISSION = "DIMISSION";

        /** 调岗 */
        public static final String TRANSFER_POSITION = "TRANSFER_POSITION";

    }

    //车辆类型
    public static class CarType {
        /** 员工车辆 */
        public static final String EMPLOYEE_CAR = "员工车辆";

        /** 公务车辆 */
        public static final String OFFICIAL_CAR = "公务车辆";

    }

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

}
