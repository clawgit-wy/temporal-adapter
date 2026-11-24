package com.yutong.safety.web.controller.app.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.spot.SpotCheckPlanBatchParam;
import com.yutong.safety.common.param.web.spot.SpotCheckPlanDormancyParam;
import com.yutong.safety.common.param.web.spot.SpotCheckPlanListParam;
import com.yutong.safety.common.param.web.spot.SpotCheckPlanSaveParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.dto.SafetySpotCheckProcessDTO;
import com.yutong.safety.core.handler.SafetyBpmProcessHandler;
import com.yutong.safety.core.handler.spot.SafetySpotCheckPlanHandler;
import com.yutong.safety.core.handler.spot.SafetySpotCheckProcessHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;


/**
 * @author lukunpeng
 * @time 2024-5-14
 */
@Api(tags = "设备设施点检-流程相关")
@RestController
@RequestMapping("/app/safety/spot/process")
public class SafetySpotCheckProcessController {

    @Autowired
    SafetySpotCheckProcessHandler safetySpotCheckProcessHandler;

    @Autowired
    private SafetyBpmProcessHandler safetyBpmProcessHandler;

    /**
     * 扫码查询设备详情
     * @param deviceId
     * @return
     */
    @ApiOperation("查询单个")
    @RequestMapping("/findForAdd")
    public Object findForAdd(String deviceId, String loginName){
        return safetySpotCheckProcessHandler.findForAdd(deviceId, loginName);
    }

    /**
     * 创建流程
     * @return
     */
    @ApiOperation("创建流程")
    @RequestMapping("/createFlow")
    public Object createFlow(SafetySpotCheckProcessDTO processDTO){
        return safetySpotCheckProcessHandler.createFlow(processDTO);
    }
    /**
     * 查看流程
     * @return
     */
    @ApiOperation("查看流程")
    @RequestMapping("/view")
    public Object view(String flowId){
        return safetySpotCheckProcessHandler.view(flowId);
    }

    /**
     * 审批流程
     * @return
     */
    @ApiOperation("审批流程")
    @RequestMapping("/audit")
    public Object audit(SafetySpotCheckProcessDTO processDTO){
        return safetySpotCheckProcessHandler.audit(processDTO);
    }
    /**
     * 延迟整改--更新计划整改完成时间
     * @return
     */
    @ApiOperation("延迟整改")
    @RequestMapping("/delay")
    public Object delay(String flowId, String delayTime, String leaderUsername){
        return safetySpotCheckProcessHandler.delay(flowId, delayTime, leaderUsername);
    }

    @ApiOperation("根据formInstanceId查询流程id")
    @RequestMapping("/getProcessId")
    public Object getProcessId(@RequestParam(name = "formInstanceId",required = true) String formInstanceId, @RequestParam(name = "type",required = false) String type) {
        return safetyBpmProcessHandler.getProcessId(formInstanceId,type);
    }
}
