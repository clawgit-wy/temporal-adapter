package com.yutong.safety.common.constants;

/**
 * Created by gwcuiyx on 2023-07-17
 */
public enum RegionDeviceEnum {

    CARDREADR("读卡器"), FACETABLET("人脸平板"), QRCODE("二维码识别"), LANE("车道");

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    RegionDeviceEnum(String name) {
        this.name = name;
    }
}
