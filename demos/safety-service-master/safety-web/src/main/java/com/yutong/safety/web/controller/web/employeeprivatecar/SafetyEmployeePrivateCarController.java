package com.yutong.safety.web.controller.web.employeeprivatecar;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.excel.ErrorInfo;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.handler.employeeprivatecar.SafetyEmployeePrivateCarHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * @author caomg
 * @date 2024-10-29 13:21
 * @description SafetyEmployeePrivateCar
 */
@Api(tags = "员工入厂私家车信息")
@RestController
@RequestMapping("/web/safety/employeeprivatecar")
public class SafetyEmployeePrivateCarController {

    @Autowired
    SafetyEmployeePrivateCarHandler safetyEmployeePrivateCarHandler;

    @ApiOperation(value = "查询（带分页）")
    @RequestMapping(value = "/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public Object listPage(SafetyEmployeePrivateCarDO param, @RequestHeader("X-Access-Token") String token) {
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), safetyEmployeePrivateCarHandler.pageList(param));
    }

    @ApiOperation(value = "导入")
    @RequestMapping("/importData")
    public VO importData(HttpServletResponse response,
                         @RequestParam("file") MultipartFile file,
                         @RequestHeader("X-Access-Token") String token) {
        response.setCharacterEncoding("utf-8");
        String mes = safetyEmployeePrivateCarHandler.importData(file, token);
        return new VO(ResponseEnum.SUCCESS.getCode(), mes);
    }

    @ApiOperation(value = "模板下载")
    @RequestMapping("/downLoadTemplet")
    public void downLoadTemplet(HttpServletResponse response) {
        String fileName = "私家车入厂权限信息表导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/privateCarTemplet.xlsx");
            InputStream inputStream = classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response, inputStream, fileName);
        } catch (Exception e) {
            LogUtils.error("私家车入厂权限信息表导入模板下载异常", e);
        }
    }
}
