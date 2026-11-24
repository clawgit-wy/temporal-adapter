package com.yutong.safety.web.controller.app;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.app.EipWriteBackParam;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutAuthAddParam;
import com.yutong.safety.core.handler.*;
import com.yutong.safety.core.handler.goodsOut.SafetyGoodsOutAuthHandler;
import com.yutong.safety.core.handler.login.LoginHandler;
import com.yutong.safety.core.handler.spot.SafetySpotCheckTaskHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/app/safety/base")
public class SafetyBaseAppController {

    @Autowired
    private LoginHandler loginHandler;

    @Autowired
    private SafetySpotCheckTaskHandler safetySpotCheckTaskHandler;

    @Autowired
    private SafetyGoodsOutAuthHandler safetyGoodsOutAuthHandler;

    @Autowired
    private SafetyBpmProcessHandler safetyBpmProcessHandler;

    @Autowired
    private SafetyBaseHandler safetyBaseHandler;
    @Autowired
    private SafetyEmployeeMessageHandler safetyEmployeeMessageHandler;

    @Resource
    private SafetySysPermHandler safetySysPermHandler;

    @ApiOperation(value = "根据员工号获取角色列表")
    @RequestMapping(value = "/getRoleListByEmpNo",method = {RequestMethod.GET,RequestMethod.POST})
    public Object getRoleListByEmpNo(String empNo) {
        if (StringUtils.isEmpty(empNo)) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"员工号不能为空!");
        }
        String newWorkNo = empNo;
        if(empNo.startsWith("01")){
            newWorkNo = safetySpotCheckTaskHandler.employeeConvertWorkNo(empNo);
        }
        return loginHandler.getRoleList(newWorkNo);
    }

    @ApiOperation(value = "根据员工号获取角色对应的权限列表")
    @RequestMapping(value = "/getPermListByEmpNo",method = {RequestMethod.GET,RequestMethod.POST})
    public VO getPermListByEmpNo(String empWorkNo) {
        return safetySysPermHandler.getPermListByEmpNo(empWorkNo);
    }

    @ApiOperation(value = "外部接口-删除权限审核人")
    @RequestMapping(value = "/deleteAccessPer", method = {RequestMethod.GET,RequestMethod.POST})
    public Object deleteAccessPer(@RequestBody List<GoodsOutAuthAddParam> enList) {
        safetyGoodsOutAuthHandler.deleteAccessPer(enList);
        return new VO(ResponseEnum.SUCCESS.getCode(), "人员权限删除成功");
    }

    @ApiOperation(value = "外部接口-新增权限审核人")
    @RequestMapping(value = "/addAccessPer", method = {RequestMethod.GET,RequestMethod.POST})
    public Object addAccessPer(@RequestBody List<GoodsOutAuthAddParam> enList) {
        safetyGoodsOutAuthHandler.addAccessPer(enList);
        return new VO(ResponseEnum.SUCCESS.getCode(), "人员权限新增成功");
    }

    @ApiOperation("传阅")
    @RequestMapping("/circulate")
    public Object circulate(String flowId, String fromUser, String toUser) {
        return safetyBpmProcessHandler.circulate(flowId, fromUser, toUser);
    }

    @ApiOperation("转员工号")
    @RequestMapping(value = "/employeeConvertWorkNo",method = {RequestMethod.GET,RequestMethod.POST})
    public Object employeeConvertWorkNo(String empNo){
        if(StringUtils.isEmpty(empNo)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"员工号不能为空");
        }
        if(empNo.startsWith("01")){
            empNo = safetyBaseHandler.employeeConvertWorkNo(empNo);
        }
        return empNo;
    }

    @ApiOperation("获取员工明细")
    @RequestMapping(value = "/getEmployeeInfo",method = {RequestMethod.GET,RequestMethod.POST})
    public Object getEmployeeInfo(String empNo){
        if(StringUtils.isEmpty(empNo)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"员工号不能为空");
        }
        SafetyEmployeeMessageDO messageDO = safetyEmployeeMessageHandler.getEmployeeByWorkNo(empNo,"");
        return messageDO;
    }

    @ApiOperation("校验出入口车行人事信息")
    @RequestMapping(value = "/checkCrkFromVehicle",method = {RequestMethod.GET,RequestMethod.POST})
    public Object checkCrkFromVehicle(String empNo){
        if(com.yutong.gw.utils.StringUtils.isEmpty(empNo)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"员工号不能为空");
        }
        boolean checkFlag = safetyBaseHandler.checkCrkFromVehicle(empNo);
        if(!checkFlag){
            throw new BizException(ResponseEnum.FAIL.getCode(),"暂未查询到您的人事信息，若您为新入职员工，可于次日再进行办理");
        }
        return true;
    }

    @ApiOperation(value = "EIP照片更新流程回写")
    @RequestMapping(value = "/eipWriteBackForPhotoUpdate", method = {RequestMethod.GET, RequestMethod.POST})
    public VO checkInterviewee(@RequestBody EipWriteBackParam vo) {
        return safetyBaseHandler.eipWriteBackForPhotoUpdate(vo);
    }
}
