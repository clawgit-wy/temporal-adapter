package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.param.web.VisitorSetAddParam;
import com.yutong.safety.common.param.web.VisitorSetDelParam;
import com.yutong.safety.common.param.web.VisitorSetListParam;
import com.yutong.safety.common.param.web.VisitorSetUpdateParam;
import com.yutong.safety.core.handler.SafetyVisitorSetHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gwcuiyx
 * @date 2024-4-2 17:06
 */
@Api(tags = "访客被访问人设置")
@RestController
@RequestMapping("/web/safety/visitorSet")
public class SafetyVisitorSetController {
    @Autowired
    private SafetyVisitorSetHandler safetyVisitorSetHandler;

    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/list")
    public Object list(VisitorSetListParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyVisitorSetHandler.pageList(param);
    }

    @ApiOperation(value = "添加接口")
    @RequestMapping("/add")
    public Object add(VisitorSetAddParam param) {
        return safetyVisitorSetHandler.add(param);
    }

    @ApiOperation(value = "更新接口")
    @RequestMapping("/update")
    public Object update(VisitorSetUpdateParam param) {
        return safetyVisitorSetHandler.update(param);
    }

    @ApiOperation(value = "删除接口")
    @RequestMapping("/delete")
    public Object delete(VisitorSetDelParam param) {
        return safetyVisitorSetHandler.delete(param);
    }


}
