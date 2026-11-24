package com.yutong.safety.common.vo.competitor;

import lombok.Data;

import java.util.List;

@Data
public class CompetitorMangeVO {

    private String msg;
    private Integer code;
    private Data data;

    @lombok.Data
    public static class Data {
        private String logId;
        private List<AccInfo> accInfo;
    }

    @lombok.Data
    public static class AccInfo {
        private String msg;
        private String usci;
        private String isNoAccess;
        private String sysName;
        private String accName;
        private String overseasMark;
        private String ownerName;
        private String ownerEmployeeId;
    }
}
