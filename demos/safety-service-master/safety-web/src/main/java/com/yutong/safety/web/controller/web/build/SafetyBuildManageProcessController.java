package com.yutong.safety.web.controller.web.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.build.SafetyBuildManageProcessDO;
import com.yutong.safety.core.dto.build.SafetyBuildManageProcessDTO;
import com.yutong.safety.core.handler.build.SafetyBuildManageProcessHandler;
import com.yutong.safety.core.service.build.SafetyBuildManageProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "楼宇门禁管理责任部门调整流程")
@RestController
@RequestMapping("/web/safety/buildManage")
public class SafetyBuildManageProcessController {
    @Autowired
    SafetyBuildManageProcessHandler safetyBuildManageProcessHandler;

    @ApiOperation(value = "根据责任部门查询授权门禁")
    @RequestMapping(value = "/getGuardInfoListByDept")
    public Object getGuardInfoListByDept(String dutyDeptCode){
        return safetyBuildManageProcessHandler.getGuardInfoListByDept(dutyDeptCode);
    }

    @ApiOperation(value = "根据id查询明细")
    @RequestMapping(value = "/getDetailById", method = {RequestMethod.POST})
    public Object getDetailById(String id){
        return safetyBuildManageProcessHandler.getDetailById(id);
    }

    @ApiOperation(value = "保存数据创建流程")
    @RequestMapping(value = "/saveAndCreateProcess", method = {RequestMethod.POST})
    public Object saveAndCreateProcess(@RequestBody SafetyBuildManageProcessDTO entity, @RequestHeader("X-Access-Token") String token) {
        if(StringUtils.isBlank(entity.getCreateEmployeeLogin()) && StringUtils.isBlank(token)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"提交人“CreateEmployeeLogin”不能为空");
        }
        try {
            safetyBuildManageProcessHandler.saveAndCreateProcess(entity,token);
            return null;
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("创建保密区域管理责任部门调整流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "创建保密区域管理责任部门调整流程异常");
        }
    }

    @ApiOperation(value = "审批流程")
    @RequestMapping(value = "/approvalProcess", method = {RequestMethod.POST})
    public Object approvalProcess(@RequestBody SafetyBuildManageProcessDTO entity, @RequestHeader("X-Access-Token") String token) {
        if(StringUtils.isBlank(token)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
        }
        try {
            return safetyBuildManageProcessHandler.approvalProcess(entity,token);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("审批保密区域管理责任部门调整流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "审批保密区域管理责任部门调整流程异常");
        }
    }

    @ApiOperation("分页查询")
    @RequestMapping(value = "/getPageList", method = {RequestMethod.POST})
    public Object getPageList(SafetyBuildManageProcessDTO param, @RequestHeader("X-Access-Token") String token){
        return safetyBuildManageProcessHandler.getPageList(param,token);
    }

    @ApiOperation("查询门禁管理员对应的Eip角色")
    @RequestMapping(value = "/getRoleList", method = {RequestMethod.POST})
    public Object getRoleList(String roleName){
        return safetyBuildManageProcessHandler.getRoleList(roleName);
    }

    @ApiOperation("测试回调逻辑接口")
    @RequestMapping(value = "/testCallbackEvent", method = {RequestMethod.POST})
    public Object testCallbackEvent(String bpmFlowId,String curNodeId){
        return safetyBuildManageProcessHandler.testCallbackEvent(bpmFlowId,curNodeId);
    }
}
