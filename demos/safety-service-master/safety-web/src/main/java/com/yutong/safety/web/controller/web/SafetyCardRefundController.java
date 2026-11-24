package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CardRefundAddParam;
import com.yutong.safety.common.param.web.CardRefundListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetyCardRefundHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

/**
 * @author gwcuiyx
 * @date 2024-1-18 10:12
 */
@Api(tags = "访客未退卡管理")
@RestController
@RequestMapping("/web/safety/refund")
public class SafetyCardRefundController {
    @Autowired
    private SafetyCardRefundHandler safetyCardRefundHandler;

    /**
     * 访客未退卡管理查询列表
     * @param param
     * @param token
     * @return
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/list")
    public Object list(CardRefundListParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyCardRefundHandler.pageList(param, token);
    }

    /**
     * 数据添加接口
     * @param param
     * @return
     */
    @ApiOperation(value = "数据添加接口")
    @RequestMapping("/add")
    public Object add(CardRefundAddParam param) {
        return safetyCardRefundHandler.add(param);
    }

    /**
     * 数据导出操作
     * @param param
     * @param response
     * @param token
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public Object export(CardRefundListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("访客未退卡管理查询列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyCardRefundHandler.export(param, token);
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
            LogUtils.error("导出访客未退卡管理查询列表异常", e);
        }
        return null;
    }
}
