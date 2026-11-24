package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.RegionDeviceAddParam;
import com.yutong.safety.common.param.web.RegionDeviceListParam;
import com.yutong.safety.common.param.web.RegionDeviceUpdateParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetyRegionDeviceHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 *  create gwcuiyx 2023.07.17
 *  区域设备绑定--controller
 */
@Api(tags = "区域设备绑定")
@RestController
@RequestMapping("/web/safety/region")
public class SafetyRegionDeviceController {
    @Autowired
    private SafetyRegionDeviceHandler safetyRegionDeviceHandler;

    /**
     * 数据列表查询
     * @param param
     * @return
     */
    @ApiOperation(value = "分页数据列表接口")
    @RequestMapping("/list")
    public Object list(RegionDeviceListParam param) {
        return safetyRegionDeviceHandler.pageList(param);
    }

    /**
     * 数据详情
     * @param id
     * @return
     */
    @ApiOperation(value = "数据详情接口")
    @RequestMapping("/view")
    public Object view(Integer id) {
        return safetyRegionDeviceHandler.view(id);
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @ApiOperation(value = "数据添加接口")
    @RequestMapping("/add")
    public Object add(RegionDeviceAddParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyRegionDeviceHandler.add(param, tocken);
    }

    /**
     * 数据更新操作
     * @param param
     * @return
     */
    @ApiOperation(value = "数据更新接口")
    @RequestMapping("/update")
    public Object update(RegionDeviceUpdateParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyRegionDeviceHandler.update(param, tocken);
    }

    /**
     * 数据导出操作
     * @param param
     * @param response
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public Object export(RegionDeviceListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String tocken) {
        try {
            String fileName = new String(("区域设备绑定信息列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyRegionDeviceHandler.export(param, tocken);
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("导出区域设备绑定信息列表异常", e);
        }
        return null;
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    @ApiOperation(value = "数据导入接口")
    @RequestMapping("/import")
    public Object importRegionDevice(CommonImportParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyRegionDeviceHandler.importRegionDevice(param, tocken);
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    @ApiOperation(value = "数据删除接口")
    @RequestMapping("/delete")
    public Object delete(Integer id, @RequestHeader("X-Access-Token") String tocken) {
        return safetyRegionDeviceHandler.delete(id, tocken);
    }

    /**
     * 根据工厂id和区域id获取数据
     * @param factoryId
     * @param regionId
     * @return
     */
    @ApiOperation(value = "根据厂区id和区域id获取设备号")
    @RequestMapping("/getDeviceNos")
    public Object getDeviceNos(Integer factoryId, Integer regionId) {
        return safetyRegionDeviceHandler.getDeviceNos(factoryId, regionId);
    }

    @ApiOperation(value = "区域设备绑定导入模板-模板下载")
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        String fileName = "区域设备绑定导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/regionDownload.xlsx");
            InputStream inputStream =classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response,inputStream,fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
