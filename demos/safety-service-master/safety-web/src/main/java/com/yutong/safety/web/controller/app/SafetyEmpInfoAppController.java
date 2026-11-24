package com.yutong.safety.web.controller.app;

import com.yutong.safety.common.param.app.InoutConfigAppParam;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.handler.SafetyEmployeeMessageHandler;
import com.yutong.safety.core.handler.SafetySysUserHandler;
import com.yutong.safety.core.service.SafetyEmpPhotoInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/app/safety/photo")
public class SafetyEmpInfoAppController {

    @Autowired
    SafetySysUserHandler safetySysUserHandler;

    @Autowired
    SafetyEmpPhotoInfoService safetyEmpPhotoInfoService;

    @Resource
    SafetyEmployeeMessageHandler safetyEmployeeMessageHandler;

    /**
     * 定时更新员工照片信息
     * @return
     */
    @RequestMapping("/tasksPhotoIncrementInfo")
    public Object tasksPhotoIncrementInfo(String dateTime){
        return safetySysUserHandler.updatePhotoIncrementInfo(dateTime);
    }

    /**
     * 定时更新员工照片信息-新逻辑
     * @return
     */
    @RequestMapping(value="/tasksPhotoIncrementInfoNew",method = {RequestMethod.GET,RequestMethod.POST})
    public Object tasksPhotoIncrementInfoNew(String dateTime){
        return safetySysUserHandler.updatePhotoIncrementInfoNew(dateTime);
    }

    /**
     * 定时下发人脸不合格的自动更新识别照下发逻辑
     * @return
     */
    @RequestMapping(value="/jobFaceUnqualified",method = {RequestMethod.GET,RequestMethod.POST})
    public VO jobFaceUnqualified(){
        return safetyEmployeeMessageHandler.jobFaceUnqualified();
    }

    /**
     * 指定员工号更新生活照
     * @param inParams 员工号
     * @return
     */
    @ApiOperation(value = "更新指定员工的生活照")
    @RequestMapping(value = "/modifyLifePhoneByEmp",method = {RequestMethod.GET,RequestMethod.POST})
    public Object modifyLifePhoneByEmp(@RequestBody InoutConfigAppParam inParams){
        String empNo = inParams.getEmpCode();
        if(StringUtils.isEmpty(empNo)){
            return "员工号不能为空";
        }
        SafetyEmployeeMessageDO employeeMessageDO = safetyEmployeeMessageHandler.getEmployeeByWorkNo(empNo,null);
        if(ObjectUtils.isEmpty(employeeMessageDO) || StringUtils.isEmpty(employeeMessageDO.getEmpNo()) ){
            return "未找到员工号="+empNo+"的员工信息";
        }
        return safetyEmpPhotoInfoService.modifyLifePhoneByEmp(employeeMessageDO);
    }

    @ApiOperation(value = "新员工发行人脸")
    @RequestMapping(value = "/issueFaceForNewEmp",method = {RequestMethod.GET,RequestMethod.POST})
    public Object issueFaceForNewEmp(String beginDate){
        return safetyEmpPhotoInfoService.issueFaceForNewEmp(beginDate);
    }

    @ApiOperation(value = "新员工发行卡")
    @RequestMapping(value = "/issueCardForNewEmp",method = {RequestMethod.GET,RequestMethod.POST})
    public Object issueCardForNewEmp(String beginDate){
        return safetyEmpPhotoInfoService.issueCardForNewEmp(beginDate);
    }

}
