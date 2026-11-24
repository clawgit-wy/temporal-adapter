package com.yutong.safety.web.controller.web.enter;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.enter.SafetyFactoryLimitConfigEntity;
import com.yutong.safety.core.handler.enter.SafetyFactoryLimitConfigHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author caomg
 * @date 2024-9-10 10:56
 * @description SafetyFactoryLimitConfigController
 */
@Api(tags = "未入厂时间上限设置")
@RestController
@RequestMapping("/web/limitConfig")
public class SafetyFactoryLimitConfigController {
    @Autowired
    SafetyFactoryLimitConfigHandler safetyFactoryLimitConfigHandler;

    @ApiOperation(value = "查询")
    @RequestMapping("/getData")
    public Object getData() {
        return safetyFactoryLimitConfigHandler.getData();
    }

    @ApiOperation(value = "保存修改")
    @RequestMapping("/saveOrUpdate")
    public Object saveOrUpdate(SafetyFactoryLimitConfigEntity en,@RequestHeader("X-Access-Token") String token) {
        safetyFactoryLimitConfigHandler.saveOrUpdate(en,token);
        return new VO(ResponseEnum.SUCCESS.getCode(), "保存更新完成");
    }

    @ApiOperation(value = "定时刷新（建议每天凌晨1点）")
    @RequestMapping("/updateByTime")
    public Object updateByTime() {
        safetyFactoryLimitConfigHandler.updateByTime();
        return new VO(ResponseEnum.SUCCESS.getCode(), "保存更新完成");
    }

}
