package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.dto.SafetyMonitorPostProcessDTO;
import com.yutong.safety.core.handler.SafetyBpmProcessHandler;
import com.yutong.safety.core.handler.SafetyMonitorPostHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

/**
 * 监控岗位上报相关controller
 * @author lukunpeng
 * @time 2024-4-30
 */
@Api(tags = "监控岗位上报")
@RestController
@RequestMapping("/web/safety/monitor/post")
public class SafetyMonitorPostController {

    @Autowired
    private SafetyMonitorPostHandler safetyMonitorPostHandler;

    @Autowired
    private SafetyBpmProcessHandler safetyBpmProcessHandler;
    /**
     * 保存数据创建流程
     * @return
     */
    @ApiOperation(value = "保存数据创建流程")
    @RequestMapping("/saveAndCreateProcess")
    public Object saveAndCreateProcess(@RequestBody SafetyMonitorPostProcessDTO processDTO,
                                       @RequestHeader("X-Access-Token") String tocken) {
        return safetyMonitorPostHandler.saveAndCerateProcess(processDTO, tocken);
    }

    /**
     * 数据新增或者更新操作
     * @param
     * @return
     */
    @ApiOperation(value = "更新数据审批流程")
    @RequestMapping("/updateAndAudit")
    public Object updateAndAudit(@RequestBody SafetyMonitorPostProcessDTO processDTO, @RequestHeader("X-Access-Token") String tocken) {
        return safetyMonitorPostHandler.updateAndAudit(processDTO, tocken);
    }

    @ApiOperation(value = "获取字段权限")
    @RequestMapping("/getAuthFields")
    public Object getAuthFields(String id, String flowId, String formId, String nodeId) {
        return safetyBpmProcessHandler.getAuthFields(id, flowId, formId, nodeId);
    }

    @ApiOperation(value = "查询单个")
    @RequestMapping("/detail")
    public Object getDetail(int id) {
        return safetyMonitorPostHandler.getDetail(id);
    }

    @ApiOperation(value = "列表")
    @RequestMapping("/list")
    public Object getList(@RequestBody SafetyMonitorPostProcessDTO processDTO, @RequestHeader("X-Access-Token") String tocken) {
        return safetyMonitorPostHandler.getList(processDTO, tocken);
    }

    @ApiOperation(value = "根据条件导出")
    @RequestMapping("/exportData")
    public void exportData(SafetyMonitorPostProcessDTO processDTO, HttpServletResponse response, @RequestHeader("X-Access-Token") String tocken) {
        try {
            String fileName = new String(("监控岗位验证结果列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyMonitorPostHandler.exportData(processDTO, tocken);
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
            LogUtils.error("导出监控岗位验证结果列表异常", e);
        }
    }
}
