package com.yutong.safety.web.controller.web.hot;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.hot.param.SafetyHotWorkLensParam;
import com.yutong.safety.core.db.entity.hot.param.SafetyMonitorVideoParam;
import com.yutong.safety.core.db.entity.hot.param.SyncFireApproveListParam;
import com.yutong.safety.core.handler.hot.SafetyHotWorkHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

@Api(tags = "动火作业")
@RestController
@RequestMapping("/web/safety/hot/work")
public class SafetyHotWorkController {

    @Autowired
    private SafetyHotWorkHandler safetyHotWorkHandler;

    @ApiOperation(value = "查询动火作业列表信息")
    @RequestMapping(value ="/getFireApproveList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getFireApproveList(SyncFireApproveListParam fireApproveListParam,@RequestHeader("X-Access-Token") String tocken){
        return safetyHotWorkHandler.getFireApproveList(fireApproveListParam,tocken);
    }

    @ApiOperation("分页查询镜头信息")
    @RequestMapping(value = "/lensList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getLensList(SafetyHotWorkLensParam param,@RequestHeader("X-Access-Token") String tocken){
        return safetyHotWorkHandler.getLensList(param,tocken);
    }

    @ApiOperation("删除监控设备信息")
    @RequestMapping(value = "/deleteLens", method = {RequestMethod.GET,RequestMethod.POST})
    public Object deleteLens(SafetyHotWorkLensParam param,@RequestHeader("X-Access-Token") String tocken){
        return safetyHotWorkHandler.deleteLens(param,tocken);
    }

    @ApiOperation(value = "查询监控设备视频链接")
    @RequestMapping(value ="/getMonitorVideoUrl", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getMonitorVideoUrl(SafetyMonitorVideoParam param,@RequestHeader("X-Access-Token") String tocken){
        return safetyHotWorkHandler.getMonitorVideoUrl(param,tocken);
    }

    @ApiOperation("查询镜头状态信息")
    @RequestMapping(value = "/getLensStatus", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getLensStatus(SafetyHotWorkLensParam param,@RequestHeader("X-Access-Token") String tocken){
        return safetyHotWorkHandler.getLensStatus(param,tocken);
    }

    @ApiOperation(value = "监控设备导出接口")
    @RequestMapping("/export")
    public Object export(SafetyHotWorkLensParam param,HttpServletResponse response, @RequestHeader("X-Access-Token") String tocken) {
        try {
            String fileName = new String(("监控设备列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyHotWorkHandler.export(param,tocken);
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
            LogUtils.error("导出监控设备导列表异常", e);
        }
        return null;
    }

}

