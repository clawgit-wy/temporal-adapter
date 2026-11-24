package com.yutong.safety.web.controller.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.*;
import com.yutong.safety.common.param.web.spot.SpotCheckBatchDeleteParam;
import com.yutong.safety.common.param.web.spot.SpotCheckBatchUpdateParam;
import com.yutong.safety.common.param.web.spot.SpotCheckDeviceAddParam;
import com.yutong.safety.common.param.web.spot.SpotCheckDeviceListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.spot.SafetySpotCheckDeviceInfoHandler;
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
 * @author gwcuiyx
 * @date 2024-4-28 14:53
 */
@Api(tags = "设备设施点检-设备信息管理")
@RestController
@RequestMapping("/web/safety/spot/device")
public class SafetySpotCheckDeviceInfoController {

    @Autowired
    private SafetySpotCheckDeviceInfoHandler safetySpotCheckDeviceInfoHandler;

    /**
     * 设备设施点检-设备信息管理列表查询方法
     * @param param
     * @return
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/list")
    public Object list(SpotCheckDeviceListParam param, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetySpotCheckDeviceInfoHandler.pageList(param,token);
        }catch (BizException e){
            LogUtils.error(e.getMsg(),e);
            throw new BizException(e.getCode(),e.getMsg());
        }catch (Exception e){
            LogUtils.error("分页查询设备信息异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"分页查询设备信息异常:"+e.getMessage());
        }
    }

    /**
     * 数据导出操作
     * @param param
     * @param response
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public Object export(SpotCheckDeviceListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("设备设施点检-设备信息管理列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetySpotCheckDeviceInfoHandler.export(param, token);
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
            LogUtils.error("导出设备设施点检-设备信息管理列表异常", e);
        }
        return null;
    }

    /**
     * 批量删除操作
     * @param param
     * @return
     */
    @ApiOperation(value = "批量删除接口")
    @RequestMapping("/batchDelete")
    public Object batchDelete(SpotCheckBatchDeleteParam param, @RequestHeader("X-Access-Token") String token) {
        safetySpotCheckDeviceInfoHandler.batchDelete(param, token);
        return null;
    }

    /**
     * 模板下载接口
     * @param response
     */
    @ApiOperation(value = "设备设施点检-模板下载")
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        String fileName = "设备设施点检-设备信息管理导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/spotCheckDeviceDownload.xlsx");
            InputStream inputStream =classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response,inputStream,fileName);
        } catch (BizException e){
            LogUtils.error(e.getMsg(),e);
            throw new BizException(e.getCode(),e.getMsg());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    @ApiOperation(value = "删除接口")
    @RequestMapping("/delete")
    public Object delete(Integer id, @RequestHeader("X-Access-Token") String token) {
        return safetySpotCheckDeviceInfoHandler.delete(id, token);
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @ApiOperation(value = "添加接口")
    @RequestMapping("/add")
    public Object add(SpotCheckDeviceAddParam param, @RequestHeader("X-Access-Token") String token) {
        try{
            return safetySpotCheckDeviceInfoHandler.add(param, token);
        }catch (BizException e){
            LogUtils.error(e.getMsg(),e);
            throw new BizException(e.getCode(),e.getMsg());
        }catch (Exception e){
            LogUtils.error("设备设施点检-添加设备信息异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"设备设施点检-添加设备信息异常");
        }
    }

    /**
     * 数据更新操作
     * @param param
     * @return
     */
    @ApiOperation(value = "更新接口")
    @RequestMapping("/update")
    public Object update(SpotCheckDeviceAddParam param, @RequestHeader("X-Access-Token") String token) {
        try{
            return safetySpotCheckDeviceInfoHandler.update(param, token);
        }catch (BizException e){
            LogUtils.error(e.getMsg(),e);
            throw new BizException(e.getCode(),e.getMsg());
        }catch (Exception e){
            LogUtils.error("设备设施点检-更新设备信息异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"设备设施点检-更新设备信息异常");
        }
    }

    /**
     * 一键应用临时点检人
     * @param param
     * @param token
     * @return
     */
    @ApiOperation(value = "一键应用临时点检人")
    @RequestMapping("/updateCheckEmp")
    public Object updateCheckEmp(SpotCheckBatchUpdateParam param, @RequestHeader("X-Access-Token") String token) {
        return safetySpotCheckDeviceInfoHandler.updateCheckEmp(param, token);
    }

    /**
     * 一键应用临时点检人
     * @param param
     * @param token
     * @return
     */
    @ApiOperation(value = "一键取消临时点检人")
    @RequestMapping("/batchCancelCheckEmp")
    public Object batchCancelCheckEmp(SpotCheckBatchUpdateParam param, @RequestHeader("X-Access-Token") String token) {
        return safetySpotCheckDeviceInfoHandler.batchCancelCheckEmp(param, token);
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    @ApiOperation(value = "数据导入接口")
    @RequestMapping("/import")
    public Object importBlackMessage(CommonImportParam param, @RequestHeader("X-Access-Token") String token) {
        try{
            return safetySpotCheckDeviceInfoHandler.importSpotCheckDevice(param, token);
        }catch (BizException e){
            LogUtils.error(e.getMsg(),e);
            throw new BizException(e.getCode(),e.getMsg());
        }catch (Exception e){
            LogUtils.error("设备设施点检-数据导入异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),e.getMessage());
        }
    }

    @ApiOperation(value = "一键设置点检人")
    @RequestMapping("/batchUpdateCheckEmp")
    public Object batchUpdateCheckEmp(SpotCheckBatchUpdateParam param, @RequestHeader("X-Access-Token") String token) {
        return safetySpotCheckDeviceInfoHandler.batchUpdateCheckEmp(param, token);
    }
}
