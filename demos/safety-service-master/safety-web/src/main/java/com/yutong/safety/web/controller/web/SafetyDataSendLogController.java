package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.DataSendLogListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetyDataSendLogHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

/**
 *  create gwcuiyx 2023.08.08
 *  数据下发日志管理--controller
 */
@Api(tags = "数据下发日志管理")
@RestController
@RequestMapping("/web/safety/datasend")
public class SafetyDataSendLogController {
    @Autowired
    private SafetyDataSendLogHandler safetyDataSendLogHandler;

    /**
     * 数据下发日志管理数据列表查询方法
     * @param param
     * @return
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/list")
    public Object list(DataSendLogListParam param) {
        return safetyDataSendLogHandler.pageList(param);
    }

    /**
     * 数据导出操作
     * @param param
     * @param response
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public Object export(DataSendLogListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("黑名单信息列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyDataSendLogHandler.export(param, token);
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
            LogUtils.error("导出数据下发日志列表异常", e);
        }
        return null;
    }

    /**
     * 下发数据失败，重新下发
     * @param id
     * @return
     */
    @RequestMapping("/sendAgain")
    public Object sendAgain(Integer id, @RequestHeader("X-Access-Token") String token) {
        return safetyDataSendLogHandler.sendAgain(id, token);
    }

    @RequestMapping("/test")
    public Object test() {
        return safetyDataSendLogHandler.test();
    }
}
