package com.yutong.modules.risk.controller;


import com.yutong.modules.risk.dto.QrExportReq;
import com.yutong.modules.risk.entity.YtsafetyLocationInfo;
import com.yutong.modules.risk.service.IYtsafetyLocationInfoService;
import com.yutong.modules.risk.service.QrExportServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


/**
 * @Description: 位置二维码
 * @Date:   2026-8-6 11:48:58
 * @Version: V1.0
 */
@Slf4j
@Api(tags="位置二维码")
@RestController
@RequestMapping("/qr/location")
public class QrExportReqController extends JeecgController<YtsafetyLocationInfo, IYtsafetyLocationInfoService> {

    @Autowired
    private QrExportServiceImpl qrExportServiceImpl;



    /**
     * 位置信息表-下载导入模板
     */
    @ApiOperation(value="位置信息表-生成二维码", notes="生成二维码")
    @PostMapping(value = "/qRCodeGenerate")
    public ResponseEntity<byte[]> qRCodeGenerate(HttpServletResponse response ,@RequestBody QrExportReq exportReq) throws IOException {
        byte[] zipBytes = qrExportServiceImpl.exportQrZip(exportReq);

        String fileName = "位置二维码包.zip";
        String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=utf-8''" + encodedName);
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipBytes);
    }


}
