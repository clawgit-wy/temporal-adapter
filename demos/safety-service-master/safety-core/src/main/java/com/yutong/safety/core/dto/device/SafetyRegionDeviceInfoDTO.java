package com.yutong.safety.core.dto.device;

import lombok.Data;

import java.util.List;

@Data
public class SafetyRegionDeviceInfoDTO {
    private Integer id;

    private String devNo;

    private String devAddr;

    private String commType;

    private String devName;

    private String devTypeCode;

    private String gid;

    private String rid;

    private String faceDevice;

    private String collectCode;

    private List<String> devNoList;

    private List<String> gidList;

    @Override
    public String toString() {
        return "{" +
                "\"ID\":\"" + id + "\"" +
                ", \"DevNo\":\"" + devNo + "\"" +
                ", \"DevAddr\":\"" + devAddr + "\"" +
                ", \"CommType\":\"" + commType + "\"" +
                ", \"DevName\":\"" + devName + "\"" +
                ", \"DevTypeCode\":\"" + devTypeCode + "\"" +
                ", \"Gid\":\"" + gid + "\"" +
                ", \"Rid\":\"" + rid + "\"" +
                ", \"FaceDevice\":\"" + faceDevice + "\"" +
                ", \"collectCode=\":\"" + collectCode + "\"" +
                "}";
    }
}
