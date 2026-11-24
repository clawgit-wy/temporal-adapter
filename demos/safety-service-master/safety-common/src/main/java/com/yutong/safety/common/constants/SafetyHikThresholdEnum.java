package com.yutong.safety.common.constants;


public enum SafetyHikThresholdEnum {

    FACE_QUERY("face_info_search", "人脸信息查询"),
    ENTRY("check_entry_num", "备案黑名单校验"),
    VISITOR("check_business_visit_num", "商务来访黑名单校验"),
    VEHICLE("check_vehicle_num", "物流车黑名单校验"),
    UPDATE_FACE("update_photo_num", "掌宇生活照更新"),
    ABNORMAL_ENTER("abnormal_enter_num", "异常入侵进出"),
    VISIT_FACE_ID_CARD("visit_face_idCard_photo_num", "访客身份证和人脸1V1校验"),
    ;

    SafetyHikThresholdEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    private String name;
    private String desc;

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
