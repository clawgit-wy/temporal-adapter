package com.yutong.safety.web.controller.app;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.dto.SafetyOutsiderPromiseChangeDTO;
import com.yutong.safety.core.handler.SafetyOutsiderPromiseChangeHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lukunpeng
 * @time 2024-6-14
 * 外协外包入厂权限调整EHR调用
 */
@Api(tags = "外协外包入厂权限调整申请-app")
@RestController
@RequestMapping("/app/safety/outsiderPromise")
public class SafetyOutsiderPromiseAppController {

    @Autowired
    SafetyOutsiderPromiseChangeHandler safetyOutsiderPromiseChangeHandler;

    /**
     * EHR变更或者新增回传
     * @return
     */
    @ApiOperation("EHR变更或者新增回传")
    @RequestMapping("/forEhrAdd")
    public Object forEhrAdd(@RequestBody String promise){
        safetyOutsiderPromiseChangeHandler.forEhrAdd(promise);
        return true;
    }

    /**
     * EHR变更或者新增回传
     * @return
     */
    @ApiOperation("EHR变更或者新增回传")
    @RequestMapping("/cardDistribute")
    public Object cardDistribute(){
        safetyOutsiderPromiseChangeHandler.cardDistribute();
        return true;
    }

    /**
     * 审批流程
     * @return
     */
    @ApiOperation("审批流程")
    @RequestMapping("/audit")
    public Object audit(SafetyOutsiderPromiseChangeDTO dto){
        return safetyOutsiderPromiseChangeHandler.auditByUserName(dto);
    }

    @ApiOperation("根据员工号查询当前员工的授权厂区")
    @RequestMapping("/processFactoryByEmpNo")
    public Object processFactoryByEmpNo(@RequestParam("empNo")String empNo){
        return new VO(ResponseEnum.SUCCESS.getCode(), "", safetyOutsiderPromiseChangeHandler.processFactoryByEmpNo(empNo));
    }
}
