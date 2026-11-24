package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.param.web.GeneralConfigInfoParam;
import com.yutong.safety.common.param.web.GeneralConfigParam;
import com.yutong.safety.core.handler.SafetyGeneralConfigHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gwcuiyx
 * @date 2024-2-27 15:10
 */
@Api(tags = "通用配置类")
@RestController
@RequestMapping("/web/safety/config")
public class SafetyGeneralConfigController {

    @Autowired
    private SafetyGeneralConfigHandler safetyGeneralConfigHandler;

    /**
     * 获取数据操作接口
     * @return
     */
    @ApiOperation(value = "数据详情接口")
    @RequestMapping("/infoByType")
    public Object getConfigByType(GeneralConfigInfoParam param) {
        return safetyGeneralConfigHandler.getConfigByType(param.getGeneralType());
    }

    /**
     * 数据新增或者更新操作
     * @param param
     * @return
     */
    @ApiOperation(value = "数据新增或者更新接口")
    @RequestMapping("/saveOrUpdate")
    public Object saveOrUpdate(GeneralConfigParam param, @RequestHeader("X-Access-Token") String token) {
        safetyGeneralConfigHandler.saveOrUpdate(param, token);
        return null;
    }

    /**
     * 数据列表查询(只包含短信和宇信通或者其他的数据，不包含整体开关项)
     * @return
     */
    @ApiOperation(value = "数据列表")
    @RequestMapping("/list")
    public Object list() {
        return safetyGeneralConfigHandler.list();
    }

}
