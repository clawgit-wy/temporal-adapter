package com.yutong.safety.web.controller.web.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.build.SafetyBuildConfigDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildInfoConfigDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildInfoConfigDOExample;
import com.yutong.safety.core.handler.build.SafetyBuildConfigHandler;
import com.yutong.safety.core.handler.build.SafetyBuildInfoConfigHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "楼宇门禁-提示信息配置")
@RestController
@RequestMapping("/web/build/safety/buildInfoConfig")
public class SafetyBuildInfoConfigController {
    @Autowired
    SafetyBuildInfoConfigHandler safetyBuildInfoConfigHandler;

    @ApiOperation(value = "查询")
    @RequestMapping(value="/query", method = {RequestMethod.GET,RequestMethod.POST})
    public Object query(SafetyBuildInfoConfigDO param) {
        return safetyBuildInfoConfigHandler.query(param);
    }

    @ApiOperation(value = "更新")
    @RequestMapping(value = "/saveOrUpdate", method = {RequestMethod.GET,RequestMethod.POST})
    public Object update(SafetyBuildInfoConfigDO param) {
        try {
            safetyBuildInfoConfigHandler.update(param);
            return null;
        }catch (Exception e){
            LogUtils.error("SafetyBuildInfoConfigController.update异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), ResponseEnum.FAIL.getName());
        }
    }
}
