package com.yutong.safety.web.controller.web.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceCollectDO;
import com.yutong.safety.core.handler.build.SafetyBuildDeviceCollectHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *  create gwcuiyx 2023.07.11
 *  字段管理--controller
 */
@Api(tags = "字段管理")
@RestController
@RequestMapping("/web/build/safety/collect")
public class SafetyBuildDeviceCollectController {

    @Autowired
    SafetyBuildDeviceCollectHandler safetyBuildDeviceCollectHandler;

    @ApiOperation(value = "获取门禁树-异步")
    @RequestMapping(value = "/selectBuildTree", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectBuildTree(String key){
        return safetyBuildDeviceCollectHandler.selectBuildTree(key);
    }

    @ApiOperation(value = "根据楼栋节点查询旗下所有的门禁")
    @RequestMapping(value = "/selectAllGuard", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectAllGuard(String key){
        return safetyBuildDeviceCollectHandler.selectAllGuard(key);
    }

    @ApiOperation(value = "查询门禁")
    @RequestMapping(value = "/selectGuard", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectGuard(String guard){
        return safetyBuildDeviceCollectHandler.selectGuard(guard);
    }

    @ApiOperation(value = "门禁组合保存")
    @RequestMapping(value = "/saveBuildCollect", method = {RequestMethod.GET,RequestMethod.POST})
    public Object saveBuildCollect(SafetyBuildDeviceCollectDO param) {
        return safetyBuildDeviceCollectHandler.saveBuildCollect(param);
    }

    @ApiOperation(value = "查询门禁组合")
    @RequestMapping(value = "/selectBuildCollect", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectBuildCollect(String userCode) {
        return safetyBuildDeviceCollectHandler.selectBuildCollect(userCode);
    }

    @ApiOperation(value = "门禁组合删除")
    @RequestMapping(value = "/deleteBuildCollect", method = {RequestMethod.GET,RequestMethod.POST})
    public Object deleteBuildCollect(Integer id) {
        safetyBuildDeviceCollectHandler.deleteBuildCollect(id);
        return new VO(ResponseEnum.SUCCESS.getCode(), "操作成功");
    }

    @ApiOperation(value = "查询门禁组合详情")
    @RequestMapping(value = "/selectBuildCollectDetail", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectBuildCollectDetail(String id) {
        return safetyBuildDeviceCollectHandler.selectBuildCollectDetail(id);
    }
}
