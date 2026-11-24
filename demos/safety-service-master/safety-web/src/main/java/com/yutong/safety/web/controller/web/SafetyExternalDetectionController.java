package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.*;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetyExternalDetectionHandler;
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
 * @author gwcuiyx
 * @date 2024-2-28 15:38
 */
@Api(tags = "被访人白名单")
@RestController
@RequestMapping("/web/safety/external")
public class SafetyExternalDetectionController {

    @Autowired
    private SafetyExternalDetectionHandler safetyExternalDetectionHandler;

    /**
     * 访客未退卡管理查询列表
     * @param param
     * @param token
     * @return
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/list")
    public Object list(ExternalDetectionListParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyExternalDetectionHandler.pageList(param, token);
    }

    /**
     * 数据导出操作
     * @param param
     * @param response
     * @param token
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public Object export(ExternalDetectionListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("外协免检台账列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyExternalDetectionHandler.export(param, token);
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
            LogUtils.error("导出外协免检台账列表异常", e);
        }
        return null;
    }

    /**
     * 数据添加接口
     * @param param
     * @return
     */
    @ApiOperation(value = "数据添加更新接口")
    @RequestMapping("/addOrUpdate")
    public Object addOrUpdate(ExternalDetectionAddParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyExternalDetectionHandler.addOrUpdate(param, token);
    }

    /**
     * 数据删除操作
     * @param param
     * @return
     */
    @ApiOperation(value = "删除接口")
    @RequestMapping("/delete")
    public Object delete(ExternalDetectionDelParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyExternalDetectionHandler.delete(param.getId());
    }

}
