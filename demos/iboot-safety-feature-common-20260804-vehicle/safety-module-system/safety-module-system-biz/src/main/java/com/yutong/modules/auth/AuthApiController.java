package com.yutong.modules.auth;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.api.SafetySysPermService;
import com.yutong.modules.user.vo.UserPermissionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysAuthBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * @description:
 * @author: liuky
 * @createDate: 2023/6/27
 * @version: 1.0
 */
@Api(tags = "用户权限")
@Slf4j
@RestController
@RequestMapping("/sys/auth")
public class AuthApiController {

    @Lazy
    @Resource
    private ISysAuthBaseAPI isysAuthBaseAPI;

    @Resource
    private SafetySysPermService safetySysPermService;

    @Resource
    private ISafetyCommonService commonService;

    /**
     * 【vue3专用】获取用户信息
     */
    @ApiOperation(value = "getUserInfo", notes = "【vue3专用】获取用户信息")
    @GetMapping("/user/getUserInfo")
    public Result<JSONObject> getUserInfo() {
        Result<JSONObject> userInfo = isysAuthBaseAPI.getUserInfo();
        if(userInfo==null) userInfo = new Result<JSONObject>();
        LoginUser user =  BusUtits.getLoginUser();
        userInfo.getResult().put("userLoginInfo",user);
        String empNo = BusUtits.getUserEmpNo(userInfo);
        if(StringUtils.isNotEmpty(empNo)){
            Long num = commonService.checkDefend(empNo);
            if (num > 0) {
                userInfo.getResult().put("isDefend", 1);
            } else {
                userInfo.getResult().put("isDefend", 0);
            }
        }
        return userInfo;
    }

    /**
     *  查询当前用户的所有有效租户【低代码应用专用接口】
     * @return
     */
    @GetMapping("/tenant/getCurrentUserTenant")
    public Result<Map<String,Object>> getCurrentUserTenant(){
        return isysAuthBaseAPI.getCurrentUserTenant();
    }

    /**
     * 查询用户拥有的菜单权限和按钮权限
     *
     * @return
     */
    @GetMapping("/permission/getUserPermissionByToken")
    public Result<UserPermissionVO> getUserPermissionByToken() {
        Result<JSONObject> result = isysAuthBaseAPI.getUserInfo();
        String workNo = "";
        if (Objects.nonNull(result) && Objects.nonNull(result.getResult())) {
            JSONObject userInfo = result.getResult().getJSONObject("userInfo");
            if (Objects.nonNull(userInfo)) {
                workNo = userInfo.getString("workNo");
            }
        }
        UserPermissionVO vo = safetySysPermService.queryUserPermission(workNo);
        return Result.OK("ok", vo);
    }

    /**
     * 【vue3专用】获取
     * 1、查询用户拥有的按钮/表单访问权限
     * 2、所有权限 (菜单权限配置)
     * 3、系统安全模式 (开启则online报表的数据源必填)
     */
    @GetMapping("/permission/getPermCode")
    public Result<JSONObject> getPermCode(){
        JSONObject result = new JSONObject();

        JSONObject code1 = new JSONObject();
        code1.put("action", "system:permission:saveRole");
        code1.put("describe", "保存角色授权");
        code1.put("type", "1");

        JSONObject code2 = new JSONObject();
        code2.put("action", "system:role:queryallNoByTenant");
        code2.put("describe", "查询全部角色不租户隔离");
        code2.put("type", "1");

        JSONObject code3 = new JSONObject();
        code3.put("action", "system:role:add");
        code3.put("describe", "角色添加");
        code3.put("type", "1");

        JSONObject code4 = new JSONObject();
        code4.put("action", "system:role:edit");
        code4.put("describe", "角色编辑");
        code4.put("type", "1");

        JSONArray codeList = new JSONArray();
        codeList.add(code1);
        codeList.add(code2);
        codeList.add(code3);
        codeList.add(code4);

        result.put("auth", codeList);
        result.put("sysSafeMode", false);

        return Result.OK("ok", result);
    }

    /**
     * 获取模拟登录 token（开发测试用）
     */
    @ApiOperation(value = "getMockToken", notes = "获取模拟登录 token（开发测试用）")
    @GetMapping("/getMockToken")
    public Result<String> getMockToken() {
        String mockToken = JWT.create()
                .withClaim("username", "wangyuanh")
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .sign(Algorithm.HMAC256("Aa123456"));
        return Result.OK("模拟 token 获取成功", mockToken);
    }

}
