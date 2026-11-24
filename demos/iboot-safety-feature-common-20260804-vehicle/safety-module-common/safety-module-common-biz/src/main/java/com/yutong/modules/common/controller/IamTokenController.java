package com.yutong.modules.common.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yutong.modules.common.api.IamTokenApiService;
import com.yutong.modules.common.dto.SessionKeyDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Api(tags="掌宇sessionKey登录IAM")
@RestController
@RequestMapping("/app/iam")
public class IamTokenController {

    @Autowired
    private IamTokenApiService iamService;

    @AutoLog(value = "掌宇sessionKey", operateType = 1)
    @ApiOperation(value = "掌宇sessionKey登录获取IAM token", notes = "通过sessionKey获取IAM token")
    @PostMapping("/getIamToken")
    public Result<String> getIamToken(@RequestBody SessionKeyDto sessionKey, HttpServletRequest request) {
        String sessionHeader =  request.getHeader("sessionKey");
        String token = "";
        if(StringUtils.isNotEmpty(sessionHeader)){
            token = iamService.getToken(sessionHeader);
        }else{
            token = iamService.getToken(sessionKey.getSessionKey());
        }
        return Result.OK(token);
    }
}
