package com.yutong.modules.demo.controller;


import com.alibaba.fastjson.JSONObject;
import com.yutong.modules.user.api.IYtsafetySysUserDataRuleService;
import com.yutong.modules.user.api.UserAuthApiService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysAuthBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Api(tags="角色权限")
@RestController
@RequestMapping("/api/userAuth")
public class UserAuthApiController {
    @Lazy
    @Resource
    private ISysAuthBaseAPI isysAuthBaseAPI;

    @Autowired
    private UserAuthApiService userAuthApiService;

    @Autowired
    private IYtsafetySysUserDataRuleService ytsafetySysUserDataRuleService;

    @GetMapping("/queryUserRoles")
    public Set<String> queryUserRoles(@RequestParam String username) {
        return userAuthApiService.queryUserRoles(username);
    }

    @GetMapping("/queryUserAuths")
    public Set<String> queryUserAuths(@RequestParam String username) {
        return userAuthApiService.queryUserAuths(username);
    }

    @GetMapping("/queryDataRule")
    public String queryUserDataRule(@RequestParam("permCode") String permCode) {
        Result<JSONObject> result = isysAuthBaseAPI.getUserInfo();
        String workNo = "";
        if (Objects.nonNull(result) && Objects.nonNull(result.getResult())) {
            JSONObject userInfo = result.getResult().getJSONObject("userInfo");
            if (Objects.nonNull(userInfo)) {
                workNo = userInfo.getString("workNo");
            }
        }
        return ytsafetySysUserDataRuleService.getListByUserId(workNo, permCode);
    }
}
