package com.yutong.safety.web.controller.web.openclose;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.openclose.SafetyOpenCloseDO;
import com.yutong.safety.core.handler.openclose.SafetyOpenCloseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author caomg
 * @date 2024-11-21 10:52
 * @description OpenCloseController
 */
@Api(tags = "功能开关")
@RestController
@RequestMapping("/web/safety/openClose")
public class SafetyOpenCloseController {

    @Autowired
    SafetyOpenCloseHandler safetyOpenCloseHandler;

    @RequestMapping("/setOpenStatus")
    @ApiOperation("设置开关状态")
    public Object setOpenStatus(SafetyOpenCloseDO en) {
        safetyOpenCloseHandler.setOpenStatus(en);
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName());
    }

    @RequestMapping("/getOpenInfo")
    @ApiOperation("查询开关状态")
    public Object getOpenInfo(SafetyOpenCloseDO en) {
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName(),safetyOpenCloseHandler.getOpenInfo(en));
    }
}
