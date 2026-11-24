package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.*;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.SafetyInoutConfigHandler;
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
 * @date 2024-4-8 16:20
 */
@Api(tags = "出入信息查询权限")
@RestController
@RequestMapping("/web/safety/inoutConfig")
public class SafetyInoutConfigController {
    @Autowired
    private SafetyInoutConfigHandler safetyInoutConfigHandler;

    /**
     * 出入信息查询权限分页接口
     * @param param
     * @return
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/list")
    public Object list(InoutConfigListParam param) {
        return safetyInoutConfigHandler.pageList(param);
    }

    /**
     * 出入信息查询添加接口
     * @param param
     * @return
     */
    @ApiOperation(value = "出入信息查询添加接口")
    @RequestMapping("/add")
    public Object add(InoutConfigAddParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyInoutConfigHandler.add(param, token);
    }

    /**
     * 数据更新接口
     * @param param
     * @param token
     * @return
     */
    @ApiOperation(value = "出入信息查询更新接口")
    @RequestMapping("/update")
    public Object update(InoutConfigUpdateParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyInoutConfigHandler.update(param, token);
    }

    /**
     * 数据删除接口
     * @param param
     * @param token
     * @return
     */
    @ApiOperation(value = "出入信息查询删除接口")
    @RequestMapping("/delete")
    public Object delete(InoutConfigDelParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyInoutConfigHandler.delete(param, token);
    }

    /**
     * 数据导出操作
     * @param param
     * @param response
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public Object export(InoutConfigListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("出入信息查询权限"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyInoutConfigHandler.export(param, token);
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
            LogUtils.error("出入信息查询权限异常", e);
        }
        return null;
    }

}
