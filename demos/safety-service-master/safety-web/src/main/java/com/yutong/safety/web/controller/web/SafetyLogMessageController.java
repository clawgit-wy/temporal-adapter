package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.LogMessageListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetyLogMessageHandler;
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
 *  create gwcuiyx 2023.07.13
 *  日志管理--controller
 */
@Api(tags = "日志管理")
@RestController
@RequestMapping("/web/safety/log")
public class SafetyLogMessageController {

    @Autowired
    private SafetyLogMessageHandler safetyLogMessageHandler;

    /**
     * 日志管理数据列表查询方法
     * @param param
     * @return
     */
    @ApiOperation(value = "分页查询列表接口")
    @RequestMapping("/list")
    public Object list(LogMessageListParam param) {
        return safetyLogMessageHandler.pageList(param);
    }

    /**
     * 数据导出功能
     * @param param
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public Object export(LogMessageListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String tocken) {
        try {
            String fileName = new String(("黑名单信息列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyLogMessageHandler.export(param, tocken);
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
            LogUtils.error("导出日志管理信息列表异常", e);
        }
        return null;
    }
}
