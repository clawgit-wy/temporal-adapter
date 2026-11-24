package com.yutong.modules.risk.service;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.yutong.modules.risk.dto.QrExportReq;
import com.yutong.modules.risk.entity.YtsafetyLocationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class QrExportServiceImpl {

    @Value("${ytsafety.location.qr.sign}")
    private String sign;

    @Value("${ytsafety.location.qr.pass}")
    private String pass;



    @Autowired
    private IYtsafetyLocationInfoService ytsafetyLocationInfoService;
    /**
     * 批量生成二维码打包ZIP【支持多个位置】
     */
    public byte[] exportQrZip(QrExportReq req) throws UnsupportedEncodingException {
        // =========【改动1】批量查询位置信息，使用idList =========
        List<String>  locIdList= Arrays.asList(req.getIds().split(","));
        if (locIdList == null || locIdList.isEmpty()) {
            throw new RuntimeException("请传入位置ID列表");
        }
        List<YtsafetyLocationInfo> locationList = ytsafetyLocationInfoService.listByIds(locIdList);
        if (locationList == null || locationList.isEmpty()) {
            throw new RuntimeException("未查询到有效位置数据");
        }

        List<String> codeTypeList = req.getCodeTypeList();
        if (codeTypeList == null || codeTypeList.isEmpty()) {
            throw new RuntimeException("请选择需要生成的二维码类型");
        }

        Map<String, byte[]> zipFileMap = new HashMap<>();
        QrConfig qrConfig = QrConfig.create()
                .setWidth(req.getWidth())
                .setHeight(req.getHeight());

        // =========【改动2】外层循环遍历【每一个位置】 =========
        for (YtsafetyLocationInfo loc : locationList) {
            String locId = loc.getId() + "";
            String locName = loc.getLocationName();
            String factoryName = loc.getFactoryName();
            long factoryId = loc.getFactoryId();
            String areaName = loc.getAreaName();
            long areaId = loc.getAreaId();

            // 参数URL编码
            String locationIdEnc = URLEncoder.encode(locId, StandardCharsets.UTF_8.name());
            String locationNameEnc = URLEncoder.encode(locName, StandardCharsets.UTF_8.name());
            String factoryNameEnc = URLEncoder.encode(factoryName, StandardCharsets.UTF_8.name());
            String areaNameEnc = URLEncoder.encode(areaName, StandardCharsets.UTF_8.name());

            // 遍历当前位置需要生成的码类型
            for (String codeType : codeTypeList) {
                String baseUrl;
                String folderName;
                if ("sign".equals(codeType)) {
                    baseUrl = sign;
                    folderName = "签到码/";
                } else if ("pass".equals(codeType)) {
                    baseUrl = pass;
                    folderName = "通行码/";
                } else {
                    continue; // 非法类型跳过
                }

                // 拼接完整链接
                String fullUrl = String.format("%s&locationId=%s&locationName=%s&factoryName=%s&factoryId=%s&areaName=%s&areaId=%s",
                        baseUrl, locationIdEnc, locationNameEnc, factoryNameEnc, factoryId, areaNameEnc, areaId);

                // 生成二维码图片字节
                try (FastByteArrayOutputStream imgOut = new FastByteArrayOutputStream()) {
                    QrCodeUtil.generate(fullUrl, qrConfig, ImgUtil.IMAGE_TYPE_JPG, imgOut);
                    byte[] imgBytes = imgOut.toByteArray();

                    // 构建文件名，增加locId防止多个位置名称重复覆盖文件
                    String safeName = StrUtil.cleanBlank(factoryName) + "_"
                            + StrUtil.cleanBlank(areaName) + "_"
                            + StrUtil.cleanBlank(locName) + "_" + locId + ".png";
                    String entryPath = folderName + safeName;

                    zipFileMap.put(entryPath, imgBytes);
                }
            }
        }

        if (zipFileMap.isEmpty()) {
            throw new RuntimeException("没有可生成的二维码数据");
        }

        // 打包ZIP
        FastByteArrayOutputStream zipOut = new FastByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(zipOut, StandardCharsets.UTF_8)) {
            for (Map.Entry<String, byte[]> entry : zipFileMap.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(entry.getKey());
                zos.putNextEntry(zipEntry);
                zos.write(entry.getValue());
                zos.closeEntry();
            }
            zos.finish();
        } catch (IOException e) {
            throw new RuntimeException("打包二维码压缩包异常", e);
        }

        return zipOut.toByteArray();
    }
}