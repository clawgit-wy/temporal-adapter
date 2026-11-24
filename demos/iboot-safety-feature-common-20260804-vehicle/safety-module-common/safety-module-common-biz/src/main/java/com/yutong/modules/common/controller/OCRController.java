package com.yutong.modules.common.controller;

import com.yutong.modules.common.api.IOcrService;
import com.yutong.modules.common.vo.DriverLicenseResultVO;
import com.yutong.modules.common.vo.PlateNoResultVO;
import com.yutong.modules.common.vo.VehicleLicenseResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "OCR识别")
@RestController
@RequestMapping("/app/ocr")
public class OCRController {

    @Autowired
    private IOcrService ocrService;

    @AutoLog(value = "行驶证识别")
    @ApiOperation(value = "行驶证识别", notes = "通过图片Base64识别行驶证信息")
    @PostMapping("/identifyVehicleLicenseByBaiduOCR")
    public Result<VehicleLicenseResultVO> recognizeVehicleLicense(
            @ApiParam(value = "图片类型，支持jpg、jpeg、png、bmp", required = true) @RequestParam("imageType") String imageType,
            @ApiParam(value = "图片Base64字符串", required = true) @RequestParam("imageBase64") String imageBase64) {
        VehicleLicenseResultVO result = ocrService.recognizeVehicleLicense(imageType, imageBase64);
        if (result != null) {
            return Result.OK(result);
        }
        return Result.error("行驶证识别失败");
    }

    @AutoLog(value = "驾驶证识别")
    @ApiOperation(value = "驾驶证识别", notes = "通过图片Base64识别驾驶证信息")
    @PostMapping("/identifyDriverLicenseByBaiduOCR")
    public Result<DriverLicenseResultVO> recognizeDriverLicense(
            @ApiParam(value = "图片类型，支持jpg、jpeg、png、bmp", required = true) @RequestParam("imageType") String imageType,
            @ApiParam(value = "图片Base64字符串", required = true) @RequestParam("imageBase64") String imageBase64,
            @ApiParam(value = "驾驶证面别，front-正面，back-背面", required = false) @RequestParam("drivingLicenseSide") String drivingLicenseSide) {
        DriverLicenseResultVO result = ocrService.recognizeDriverLicense(imageType, imageBase64, drivingLicenseSide);
        if (result != null) {
            return Result.OK(result);
        }
        return Result.error("驾驶证识别失败");
    }

    @AutoLog(value = "车牌号识别")
    @ApiOperation(value = "车牌号识别", notes = "通过图片Base64识别车牌号")
    @PostMapping("/identifyPlateNoByBaiduOCR")
    public Result<PlateNoResultVO> recognizePlateNo(
            @ApiParam(value = "图片类型，支持jpg、jpeg、png、bmp", required = true) @RequestParam("imageType") String imageType,
            @ApiParam(value = "图片Base64字符串", required = true) @RequestParam("imageBase64") String imageBase64) {
        PlateNoResultVO result = ocrService.recognizePlateNo(imageType, imageBase64);
        if (result != null) {
            return Result.OK(result);
        }
        return Result.error("车牌号识别失败");
    }
}
