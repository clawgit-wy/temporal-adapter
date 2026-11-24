package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.NoticeLogParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetyNoticeLogHandler;
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
 * @author lukunpeng
 * @time 2024-4-15
 */
@Api(tags = "通知下发日志")
@RestController
@RequestMapping("/web/safety/noticeLog")
public class SafetyNoticeLogController {
    @Autowired
    private SafetyNoticeLogHandler safetyNoticeLogHandler;

    /**
     * 获取列表接口
     * @return
     */
    @ApiOperation(value = "列表接口")
    @RequestMapping("/list")
    public Object list(NoticeLogParam param) {
        return safetyNoticeLogHandler.getNotice(param);
    }

    /**
     * 导入查询列表
     * @return
     */
    @ApiOperation(value = "导出接口")
    @RequestMapping("/export")
    public Object export(NoticeLogParam param, HttpServletResponse response) {
        try {
            String fileName = new String(("滞留车辆通知发送记录"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyNoticeLogHandler.export(param);
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
            LogUtils.error("导出滞留车辆通知发送记录异常", e);
        }
        return null;
    }
}
