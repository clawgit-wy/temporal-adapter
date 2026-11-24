package com.yutong.modules.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yutong.modules.common.api.IOcrService;
import com.yutong.modules.common.vo.DriverLicenseResultVO;
import com.yutong.modules.common.vo.PlateNoResultVO;
import com.yutong.modules.common.vo.VehicleLicenseResultVO;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.HttpFileUtil;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OcrServiceImpl implements IOcrService {

    private static final String VEHICLE_LICENSE_OCR_URL = "http://sguat.yutong.com/iboot-tools/drivingLicense?apikey=4d22e6b1cb92477d8699fad8a671389e";
    private static final String DRIVER_LICENSE_OCR_URL = "http://sguat.yutong.com/iboot-tools/carDrivingLicenseOCR?apikey=4d22e6b1cb92477d8699fad8a671389e";
    private static final String PLATE_NO_OCR_URL = "http://sguat.yutong.com/iboot-tools/carNumberOCR?apikey=4d22e6b1cb92477d8699fad8a671389e";
    private static final String APP_ID = "2087061044439998465";

    @Override
    public VehicleLicenseResultVO recognizeVehicleLicense(String imageType, String imageBase64) {
        JSONObject param = buildPlateNoOcrParam(imageType, imageBase64);
        String result = HttpFileUtil.sendPost(VEHICLE_LICENSE_OCR_URL, param.toJSONString());
        return parseVehicleLicenseResult(result);
    }

    @Override
    public DriverLicenseResultVO recognizeDriverLicense(String imageType, String imageBase64, String drivingLicenseSide) {
        JSONObject param = buildDriverLicenseOcrParam(imageType, imageBase64, drivingLicenseSide);
        String result = HttpFileUtil.sendPost(DRIVER_LICENSE_OCR_URL, param.toJSONString());
        return parseDriverLicenseResult(result);
    }

    @Override
    public PlateNoResultVO recognizePlateNo(String imageType, String imageBase64) {
        JSONObject param = buildPlateNoOcrParam(imageType, imageBase64);
        String result = HttpFileUtil.sendPost(PLATE_NO_OCR_URL, param.toJSONString());
        return parsePlateNoResult(result);
    }

    private JSONObject buildDriverLicenseOcrParam(String imageType, String imageBase64, String drivingLicenseSide) {
        JSONObject param = new JSONObject();
        param.put("appId", APP_ID);
        param.put("imageType", imageType);
        param.put("drivingLicenseSide", drivingLicenseSide);
        param.put("imageBase64", imageBase64);
        return param;
    }

    private JSONObject buildPlateNoOcrParam(String imageType, String imageBase64) {
        JSONObject param = new JSONObject();
        param.put("appId", APP_ID);
        param.put("imageType", imageType);
        param.put("imageBase64", imageBase64);
        return param;
    }

    private VehicleLicenseResultVO parseVehicleLicenseResult(String result) {
        if (result == null) {
            log.error("行驶证识别失败，请求结果为null");
            return null;
        }
        JSONObject resp = JSON.parseObject(result);
        if (resp == null) {
            log.error("行驶证识别失败，响应解析为空，响应：{}", result);
            return null;
        }
        Boolean success = resp.getBoolean("success");
        if (success != null && success) {
            JSONObject data = resp.getJSONObject("result");
            if (data != null) {
                JSONObject cardInfo = data.getJSONObject("cardInfo");
                if (cardInfo != null) {
                VehicleLicenseResultVO vo = cardInfo.toJavaObject(VehicleLicenseResultVO.class);
                    return vo;
                }
            }
        }
        log.error("行驶证识别失败：{}", result);
        return null;
    }

    private DriverLicenseResultVO parseDriverLicenseResult(String result) {
        if (result == null) {
            log.error("驾驶证识别失败，请求结果为null");
            return null;
        }
        JSONObject resp = JSON.parseObject(result);
        if (resp == null) {
            log.error("驾驶证识别失败，响应解析为空，响应：{}", result);
            return null;
        }
        Boolean success = resp.getBoolean("success");
        if (success != null && success) {
            JSONObject data = resp.getJSONObject("result");
            if (data != null) {
                JSONObject wordsResult = data.getJSONObject("words_result");
                if (wordsResult != null) {
                    DriverLicenseResultVO vo = wordsResult.toJavaObject(DriverLicenseResultVO.class);
                    return vo;
                }
            }
        }
        log.error("驾驶证识别失败：{}", result);
        return null;
    }

    private PlateNoResultVO parsePlateNoResult(String result) {
        if (result == null) {
            log.error("车牌号识别失败，请求结果为null");
            return null;
        }
        JSONObject resp = JSON.parseObject(result);
        if (resp == null) {
            log.error("车牌号识别失败，响应解析为空，响应：{}", result);
            return null;
        }
        // 新接口使用success字段，result或message中包含车牌号
        Boolean success = resp.getBoolean("success");
        if (success != null && success) {
            PlateNoResultVO vo = new PlateNoResultVO();
            String plateNo = resp.getString("result");
            if (plateNo == null || plateNo.isEmpty()) {
                plateNo = resp.getString("message");
            }
            vo.setResult(plateNo);
            return vo;
        }
        log.error("车牌号识别失败：{}", result);
        return null;
    }
}
