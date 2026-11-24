package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CarInFactoryListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetyCarInFactoryHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

/**
 * @author gwxiexj
 * @since 2024-04-11 14:36
 */
@Api(tags = "在厂车辆列表管理")
@RestController
@RequestMapping("/web/safety/retention/carInFactory")
public class SafetyCarInFactoryController {

    @Autowired
    private SafetyCarInFactoryHandler safetyCarInFactoryHandler;

    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/list")
    public Object list(CarInFactoryListParam param) {
        try {
            return safetyCarInFactoryHandler.getCarList(param);
        }catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("在厂车辆列表管理查询列表异常", e);
            throw new BizException("999",e.getMessage());
        }
    }

    @ApiOperation(value = "厂内车辆信息导出")
    @RequestMapping("/export")
    public Object export(CarInFactoryListParam param, HttpServletResponse response) {
        try {
            LogUtils.info("厂内车辆信息导出");
            String fileName = new String(("厂内车辆信息列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyCarInFactoryHandler.export(param);
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
            LogUtils.error("厂内车辆信息导出异常", e);
            throw new BizException("999",e.getMessage());
        }
        return null;
    }

}
