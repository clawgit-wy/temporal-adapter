package com.yutong.safety.web.controller.app.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.build.param.BuildProcessAddParam;
import com.yutong.safety.core.handler.SafetyEmployeeMessageHandler;
import com.yutong.safety.core.handler.build.*;
import com.yutong.safety.core.handler.spot.SafetySpotCheckTaskHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "楼宇门禁APP-员工权限调整")
@RestController
@RequestMapping("/app/build/safety/buildProcess")
public class SafetyBuildProcessAppController {

    @Resource
    private SafetyBuildProcessHandler safetyBuildProcessHandler;
    @Resource
    private SafetySpotCheckTaskHandler safetySpotCheckTaskHandler;
    @Resource
    private SafetyBuildEmployeePowerHandler safetyBuildEmployeePowerHandler;
    @Resource
    private SafetyBuildPowerHandler safetyBuildPowerHandler;
    @Autowired
    private SafetyBuildDeviceCollectHandler safetyBuildDeviceCollectHandler;
    @Resource
    private SafetyEmployeeMessageHandler safetyEmployeeMessageHandler;
    @Autowired
    private SafetyBuildManageProcessHandler safetyBuildManageProcessHandler;
    @Autowired
    private SafetyBuildDepartConfirmHandler safetyBuildDepartConfirmHandler;

    /**
     * 保存数据创建流程
     * @return 返回值
     */
    @ApiOperation(value = "保存数据创建流程")
    @RequestMapping(value = "/saveAndCreateProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object saveAndCreateProcess(@RequestBody BuildProcessAddParam processDTO) {
        if(StringUtils.isBlank(processDTO.getCreateEmployeeLogin())){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"提交人“creator”不能为空");
        }
        try {
            String workNo = processDTO.getCreateEmployeeNo();
            String newWorkNo = workNo;
            if(workNo.startsWith("01")){
                newWorkNo = safetySpotCheckTaskHandler.employeeConvertWorkNo(workNo);
            }
            if(newWorkNo.startsWith("01")){
                throw new BizException(ResponseEnum.FAIL.getCode(),"卡号0开头的外协人员，无法申请门禁信息!");
            }
            SafetyEmployeeMessageDO empInfo = safetyEmployeeMessageHandler.getEmpById(newWorkNo);
            if(empInfo == null || StringUtils.isBlank(empInfo.getEmpNo())){
                //TODO 提示语需要BA确认
                throw new BizException(ResponseEnum.FAIL.getCode(),"员工号"+workNo+"还未生效，请次日再提交申请!");
            }
            processDTO.setCreateEmployeeNo(newWorkNo);
            return safetyBuildProcessHandler.saveAndCreateProcessByUserNameApp(processDTO);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("创建流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "创建流程异常");
        }
    }

    @ApiOperation(value = "获取员工门禁权限清单-定制")
    @RequestMapping(value="/getBuildByEmployeeForDiy", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getBuildByEmployeeForDiy(String employeeCode) {
        if(StringUtils.isBlank(employeeCode)){
            return null;
        }
        try{
            String newWOorkNo = safetySpotCheckTaskHandler.employeeConvertWorkNo(employeeCode);
            return safetyBuildEmployeePowerHandler.getBuildByEmployeeForDiy(newWOorkNo);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("查询失败",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"查询失败"+e.getMessage());
        }
    }

    @ApiOperation(value = "数据列表查询接口(批量)-带绝密信息")
    @RequestMapping(value="/listWithSecretBatch", method = {RequestMethod.POST})
    public Object listWithSecretBatch(String deviceCodes) {
        return safetyBuildPowerHandler.listWithSecretBatch(deviceCodes);
    }

    @ApiOperation(value = "获取门禁树-异步")
    @RequestMapping(value = "/selectBuildTree", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectBuildTree(String key){
        return safetyBuildDeviceCollectHandler.selectBuildTree(key);
    }

    @ApiOperation(value = "查询门禁")
    @RequestMapping(value = "/selectGuard", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectGuard(String guard){
        return safetyBuildDeviceCollectHandler.selectGuard(guard);
    }

    @ApiOperation(value = "发布后更新流程状态-测试")
    @RequestMapping(value = "/updateFlowStatus", method = {RequestMethod.GET,RequestMethod.POST})
    public Object updateFlowStatus(String bpmFlowId,String status){
        safetyBuildProcessHandler.updateFlowStatus(bpmFlowId,status);
        return "0";
    }


    @ApiOperation(value = "节点结束事件")
    @RequestMapping(value = "/testAfterProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object testAfterProcess(String bpmFlowId,String noteId){
        safetyBuildProcessHandler.afterProcess(bpmFlowId,noteId);
        return "0";
    }

    @ApiOperation(value = "责任部门-发布后更新流程状态-测试")
    @RequestMapping(value = "/testManageCallBack", method = {RequestMethod.GET,RequestMethod.POST})
    public Object testManageCallBack(String bpmFlowId,String noteId){
        safetyBuildManageProcessHandler.testCallBack(bpmFlowId,noteId);
        return "0";
    }

    @ApiOperation(value = "责任部门-测试回调方法")
    @RequestMapping(value = "/testCallBackForBpm", method = {RequestMethod.GET,RequestMethod.POST})
    public Object testCallBackForBpm(String bpmFlowId,String noteId){
        safetyBuildManageProcessHandler.testCallBackForBpm(bpmFlowId,noteId);
        return "0";
    }

    @ApiOperation(value = "校验员工号是否有效生效")
    @RequestMapping(value = "/checkEmployeeForBuild", method = {RequestMethod.GET,RequestMethod.POST})
    public Object checkEmployeeForBuild(String employeeCode){
        try{
            String newWorkNo = safetySpotCheckTaskHandler.employeeConvertWorkNo(employeeCode);
            SafetyEmployeeMessageDO empInfo = safetyEmployeeMessageHandler.getEmpById(newWorkNo);
            if(empInfo == null || StringUtils.isBlank(empInfo.getEmpNo())){
                return false;
            }
            return true;
        }catch (BizException biz){
            throw new BizException(biz.getCode(),biz.getMsg());
        }catch (Exception e){
            LogUtils.error("校验员工号是否有效失败",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"查询失败"+e.getMessage());
        }
    }

    @ApiOperation(value = "测试发送EIP代办")
    @RequestMapping(value = "/testSendEipToDO", method = {RequestMethod.GET,RequestMethod.POST})
    public Object testSendEipToDO(String employeeCode){
        try{
            safetyBuildDepartConfirmHandler.testSendEipToDO(employeeCode);
            return true;
        }catch (BizException biz){
            throw new BizException(biz.getCode(),biz.getMsg());
        }catch (Exception e){
            LogUtils.error("测试发送EIP代办失败",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"查询失败"+e.getMessage());
        }
    }

    @ApiOperation(value = "人脸照片信息更新校验")
    @RequestMapping(value = "/checkPhoto", method = {RequestMethod.GET})
    public VO checkPhoto(String empCode, String oldPhoto, String newPhoto){
        return safetyBuildProcessHandler.checkPhoto(empCode, oldPhoto, newPhoto);
    }
}
