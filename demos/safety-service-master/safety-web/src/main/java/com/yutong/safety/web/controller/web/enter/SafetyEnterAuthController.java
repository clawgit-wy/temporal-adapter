package com.yutong.safety.web.controller.web.enter;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.enter.SafetyEnterAuth;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterAuthRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterAuthSh;
import com.yutong.safety.core.handler.enter.SafetyEnterAuthHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 车辆备案
 * */
@Api(tags = "安保入厂-车辆备案权限管理")
@RestController
@RequestMapping("/web/enter/auth/")
public class SafetyEnterAuthController {
    @Autowired
    private SafetyEnterAuthHandler enterAuthHandler;

    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "selectEnterAuthPage", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectEnterAuthPage(@RequestBody SafetyEnterAuthSh enterAuthSh) {
        PageVO pageVO = enterAuthHandler.selectEnterAuthPage(enterAuthSh);
        return pageVO;
    }

    @ApiOperation(value = "根据ID查询详情")
    @RequestMapping(value = "selectEnterAuthInfo", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectEnterAuthInfo(Integer id) {
        SafetyEnterAuthRt rt = enterAuthHandler.selectEnterAuthInfo(id);
        return new VO(ResponseEnum.SUCCESS.getCode(), "查询成功", rt);
    }

    @ApiOperation(value = "查询操作权限")
    @RequestMapping(value = "selectEnterAuth", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectEnterAuth(@RequestHeader("X-Access-Token") String token) {
        List list = enterAuthHandler.selectEnterAuth(token);
        return new VO(ResponseEnum.SUCCESS.getCode(), "查询成功", list);
    }

    @ApiOperation(value = "查询可选择的操作权限")
    @RequestMapping(value = "selectEnterAuthDict", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectEnterAuthDict() {
        List list = enterAuthHandler.selectEnterAuthDict();
        return new VO(ResponseEnum.SUCCESS.getCode(), "查询成功", list);
    }

    @ApiOperation(value = "添加")
    @RequestMapping(value = "insertEnterAuth", method = {RequestMethod.GET,RequestMethod.POST})
    public VO insertEnterAuth(@RequestBody SafetyEnterAuth enterAuth, @RequestHeader("X-Access-Token") String token) {
        enterAuthHandler.insertEnterAuth(enterAuth, token);
        return new VO(ResponseEnum.SUCCESS.getCode(), "添加成功");
    }

    @ApiOperation(value = "编辑")
    @RequestMapping(value = "updateEnterAuth", method = {RequestMethod.GET,RequestMethod.POST})
    public VO updateEnterAuth(@RequestBody SafetyEnterAuth enterAuth, @RequestHeader("X-Access-Token") String token) {
        enterAuthHandler.updateEnterAuth(enterAuth, token);
        return new VO(ResponseEnum.SUCCESS.getCode(), "更新成功");
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "deleteEnterAuth", method = {RequestMethod.GET,RequestMethod.POST})
    public VO deleteEnterAuth(Integer id, @RequestHeader("X-Access-Token") String token) {
        enterAuthHandler.deleteEnterAuth(id, token);
        return new VO(ResponseEnum.SUCCESS.getCode(), "删除成功");
    }

    @ApiOperation(value = "查询可选择的备案厂区")
    @RequestMapping(value = "selectAuthFactory", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectAuthFactory(@RequestHeader("X-Access-Token") String token, String filingId) {
        return enterAuthHandler.selectAuthFactory(token, null, filingId);
    }

    @ApiOperation(value = "获取选择项参数")
    @RequestMapping(value = "getSearchParams", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getSearchParams() {
        return enterAuthHandler.getSearchParams();
    }

    @ApiOperation(value = "验证用户信息")
    @RequestMapping(value = "checkAuthEmpInfo", method = {RequestMethod.GET,RequestMethod.POST})
    public Object checkAuthEmpInfo(String empNo, String phone) {
        SafetyEnterAuthRt rt = enterAuthHandler.checkAuthEmpInfo(empNo, phone);
        return VoUtil.successData(rt);
    }


    @ApiOperation(value = "验证对接人-外来人员")
    @RequestMapping(value = "checkAuthEmpInfoNew", method = {RequestMethod.GET,RequestMethod.POST})
    public Object checkAuthEmpInfoNew(String empNo, String phone) {
        SafetyEnterAuthRt rt = enterAuthHandler.checkAuthEmpInfoNew(empNo, phone,true);
        return VoUtil.successData(rt);
    }

    @ApiOperation(value = "设置组织权限")
    @RequestMapping(value = "updatePowerOrg", method = {RequestMethod.GET,RequestMethod.POST})
    public Object updatePowerOrg(Integer id,String powerOrgCode, @RequestHeader("X-Access-Token") String token){
        String userName = TokenUtils.getUserName(token);
        if(StringUtils.isBlank(userName)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }
        if(id == null){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"未接收到要修改的ID，请联系前端管理员!");
        }
        return enterAuthHandler.updatePowerOrg(id,powerOrgCode,userName);
    }



}
