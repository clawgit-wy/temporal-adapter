package com.yutong.modules.common.api;

import com.yutong.modules.common.vo.DriverLicenseResultVO;
import com.yutong.modules.common.vo.PlateNoResultVO;
import com.yutong.modules.common.vo.VehicleLicenseResultVO;

public interface IOcrService {

    /**
     * 行驶证识别
     */
    VehicleLicenseResultVO recognizeVehicleLicense(String imageType, String imageBase64);

    /**
     * 驾驶证识别
     */
    DriverLicenseResultVO recognizeDriverLicense(String imageType, String imageBase64, String drivingLicenseSide);

    /**
     * 车牌号识别
     */
    PlateNoResultVO recognizePlateNo(String imageType, String imageBase64);
}
