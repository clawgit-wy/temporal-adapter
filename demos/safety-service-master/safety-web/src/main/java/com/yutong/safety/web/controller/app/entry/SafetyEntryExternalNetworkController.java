package com.yutong.safety.web.controller.app.entry;


import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.entry.param.*;
import com.yutong.safety.core.handler.entry.SafetyEntryCompanyRegisterHandler;
import com.yutong.safety.core.handler.entry.SafetyEntryExternalNetworkHandler;
import com.yutong.safety.core.handler.entry.SafetyEntryImPortFailHandler;
import com.yutong.safety.core.handler.entry.SafetySmsHandler;
import com.yutong.safety.core.service.SafetyFieldManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "入场备案-外网接口")
@RestController
@RequestMapping("/itf/entry/safety/externalNetwork")
public class SafetyEntryExternalNetworkController {
    @Resource
    private SafetyEntryCompanyRegisterHandler safetyEntryCompanyRegisterHandler;

    @Resource
    private SafetySmsHandler safetySmsHandler;

    @Resource
    private SafetyFieldManageService safetyFieldManageService;

    @Resource
    private SafetyEntryExternalNetworkHandler safetyEntryExternalNetworkHandler;

    @Resource
    private SafetyEntryImPortFailHandler safetyEntryImPortFailHandler;

    @ApiOperation(value = "短信验证码")
    @RequestMapping(value = "/sendVerifyCode", method = {RequestMethod.POST})
    public Object getTaskNum(@RequestBody SafetyEntrySendSmsParams params) {
        return safetySmsHandler.sendVerificationCode(params);
    }

    @ApiOperation(value = "登录")
    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    public Object login(@RequestBody SafetyEntryLoginParams params) {
        return safetyEntryCompanyRegisterHandler.login(params);
    }

    @ApiOperation(value = "注册")
    @RequestMapping(value = "/register", method = {RequestMethod.POST})
    public Object register(@RequestBody SafetyEntryCompanyRegisterAddParam param) {
        return safetyEntryCompanyRegisterHandler.register(param);
    }

    @ApiOperation(value = "登出")
    @RequestMapping(value = "/logout", method = {RequestMethod.POST})
    public Object logout(@RequestHeader("X-Access-Token") String token) {
        return safetyEntryCompanyRegisterHandler.logout(token);
    }

    /**
     * 分页数据列表查询接口
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/pageList", method = {RequestMethod.GET})
    public Object pageList(SafetyEntryConfigQueryParams params, @RequestHeader("X-Access-Token") String token) {
        return safetyEntryExternalNetworkHandler.getNetworkPageList(params, token);
    }

    /**
     * 更新和新增接口
     */
    @ApiOperation(value = "新增接口")
    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    public Object save(@RequestBody SafetyEntryConfigAddParams info, @RequestHeader("X-Access-Token") String token) {
        return safetyEntryExternalNetworkHandler.networkSave(info, token);
    }

    /**
     * 模版下载
     */
    @ApiOperation(value = "模板下载")
    @RequestMapping(value = "/download", method = {RequestMethod.GET})
    public void download(HttpServletResponse response) {
        safetyEntryExternalNetworkHandler.download(response);
    }

    /**
     * 数据导入操作
     */
    @ApiOperation(value = "数据导入接口")
    @RequestMapping(value = "/import", method = {RequestMethod.POST})
    public Object importData(CommonImportParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyEntryExternalNetworkHandler.importData(param, token);
    }

    /**
     * 注销权限
     */
    @ApiOperation(value = "注销权限")
    @RequestMapping(value = "/logoutPermission", method = {RequestMethod.GET})
    public Object logoutPermission(Integer id, @RequestHeader("X-Access-Token") String token) {
        return safetyEntryExternalNetworkHandler.logoutPermission(id, token);
    }

    /**
     * 对接人信息接口
     */
    @ApiOperation(value = "对接人信息接口")
    @RequestMapping(value = "/getButtJointInfo", method = {RequestMethod.GET})
    public Object getButtJointInfo(@RequestHeader("X-Access-Token") String token) {
        return safetyEntryExternalNetworkHandler.getButtJointInfo(token);
    }

    /**
     * 当前备案人信息
     */
    @ApiOperation(value = "供应商负责人备案信息")
    @RequestMapping(value = "/getSupplyInfo", method = {RequestMethod.GET})
    public Object getSupplyInfo(@RequestHeader("X-Access-Token") String token) {
        String phone = TokenUtils.getString(token, "phone");
        return safetyEntryCompanyRegisterHandler.getSupplyInfo(phone);
    }

    /**
     * 失败信息删除
     */
    @ApiOperation(value = "失败信息删除")
    @RequestMapping(value = "/deleteFailList", method = {RequestMethod.POST})
    public Object deleteFailList(@RequestBody SafetyEntryImportFailQueryParams params) {
        return safetyEntryImPortFailHandler.deleteFailList(params);
    }

    /**
     * 失败信息导出
     */
    @ApiOperation(value = "失败信息导出")
    @RequestMapping(value = "/export", method = {RequestMethod.GET})
    public Object export(HttpServletResponse response, SafetyEntryImportFailQueryParams params) {
        try {
            safetyEntryImPortFailHandler.export(response, params, null);
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出失败信息列表异常");
        }
        return null;
    }

    /**
     * 准入厂区
     */
    @ApiOperation(value = "准入厂区")
    @RequestMapping(value = "/getFactoryInfo", method = {RequestMethod.POST})
    public Object getFactoryInfo() {
        return safetyFieldManageService.getFactoryNameList();
    }
}
