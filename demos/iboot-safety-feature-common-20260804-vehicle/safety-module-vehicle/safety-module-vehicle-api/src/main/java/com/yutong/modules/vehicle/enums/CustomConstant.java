package com.yutong.modules.vehicle.enums;

/**
 * 自定义常量
 */
public class CustomConstant {

//  权限配置常量
    public static class authType {
        /** 人员信息查询 */
        public static final String person_info = "person_info";

        /** 车辆信息查询 */
        public static final String vehicle_info = "vehicle_info";

        /** 人员通行记录 */
        public static final String person_pass = "person_pass";

        /** 车辆通行记录 */
        public static final String vehicle_pass = "vehicle_pass";

    }

    public static class permType {
        /** 无权限 */
        public static final Integer noAuth = 0;

        /** 全部人员可查 */
        public static final Integer allAuth = 1;

        /** 自定义 */
        public static final Integer custAuth = 2;

    }



}
