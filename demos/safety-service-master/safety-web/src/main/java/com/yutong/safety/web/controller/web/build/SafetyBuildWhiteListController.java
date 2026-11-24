package com.yutong.safety.web.controller.web.build;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.build.SafetyBuildWhiteListDO;
import com.yutong.safety.core.handler.build.SafetyBuildWhiteListHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

@Api(tags = "楼宇门禁-白名单管理")
@RestController
@RequestMapping("/web/build/safety/buildWhiteList")
public class SafetyBuildWhiteListController {

    @Resource
    SafetyBuildWhiteListHandler safetyBuildWhiteListHandler;

    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public Object list(SafetyBuildWhiteListDO param,
                       @RequestHeader("X-Access-Token") String token) {
        return safetyBuildWhiteListHandler.pageList(param, token);
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public Object delete(Integer id) {
        safetyBuildWhiteListHandler.delete(id);
        return null;
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = {RequestMethod.GET, RequestMethod.POST})
    public Object save(SafetyBuildWhiteListDO param, @RequestHeader("X-Access-Token") String token) {
        return safetyBuildWhiteListHandler.save(param, token);
    }

    @ApiOperation(value = "导入")
    @RequestMapping(value = "/importData", method = {RequestMethod.GET, RequestMethod.POST})
    public Object importData(@RequestParam("file") MultipartFile file, @RequestHeader("X-Access-Token") String token) {
        return safetyBuildWhiteListHandler.importData(file, token);
    }

    @ApiOperation(value = "模板下载")
    @RequestMapping(value = "/download", method = {RequestMethod.GET, RequestMethod.POST})
    public void download(HttpServletResponse response) {
        String fileName = "导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/white.xlsx");
            InputStream inputStream = classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response, inputStream, fileName);
        } catch (Exception e) {
            LogUtils.error("白名单管理导出模板出错，{}", e.getMessage());
        }
    }

    @ApiOperation(value = "根据条件导出")
    @RequestMapping(value = "/exportData", method = {RequestMethod.GET, RequestMethod.POST})
    public Object exportData(SafetyBuildWhiteListDO param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("白名单管理" + DateUtils.format(new Date(), 5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyBuildWhiteListHandler.exportData(param, token);
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("白名单管理导出异常", e);
            throw new BizException("999", e.getMessage());
        }
        return null;
    }

    @ApiOperation(value = "根据员工号获取员工信息")
    @RequestMapping(value = "/getEmployeeByWorkNo", method = {RequestMethod.GET, RequestMethod.POST})
    public Object getEmployeeByWorkNo(String workNo, @RequestHeader("X-Access-Token") String token) {
        return safetyBuildWhiteListHandler.getEmployeeByWorkNo(workNo, token);
    }
}
