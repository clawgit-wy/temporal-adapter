package com.yutong.safety.web.controller.web.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.build.SafetyBuildConfigDO;
import com.yutong.safety.core.handler.build.SafetyBuildConfigHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@Api(tags = "楼宇门禁-授权配置")
@RestController
@RequestMapping("/web/build/safety/buildConfig")
public class SafetyBuildConfigController {

    @Resource
    private SafetyBuildConfigHandler safetyBuildConfigHandler;

    @ApiOperation(value = "查询")
    @RequestMapping(value="/query", method = {RequestMethod.GET,RequestMethod.POST})
    public Object query() {
        return safetyBuildConfigHandler.query();
    }

    @ApiOperation(value = "保存或更新")
    @RequestMapping(value = "/saveOrUpdate", method = {RequestMethod.GET,RequestMethod.POST})
    public Object saveOrUpdate(SafetyBuildConfigDO param, @RequestHeader("X-Access-Token") String token) {
        try {
            safetyBuildConfigHandler.saveOrUpdate(param,token);
            return null;
        }catch (Exception e){
            LogUtils.error("保存或更新异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), ResponseEnum.FAIL.getName());
        }
    }
}
