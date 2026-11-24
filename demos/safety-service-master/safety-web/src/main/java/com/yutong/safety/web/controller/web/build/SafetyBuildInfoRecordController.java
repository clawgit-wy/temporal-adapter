package com.yutong.safety.web.controller.web.build;

import com.yutong.safety.core.dto.build.SafetyBuildInfoRecordDTO;
import com.yutong.safety.core.handler.build.SafetyBuildInfoRecordHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "楼宇门禁-楼宇门禁消息提醒")
@RestController
@RequestMapping("/web/build/safety/buildInfoRecord")
public class SafetyBuildInfoRecordController {
    @Autowired
    SafetyBuildInfoRecordHandler safetyBuildInfoRecordHandler;

    @ApiOperation(value = "查询")
    @RequestMapping(value="/query", method = {RequestMethod.GET,RequestMethod.POST})
    public Object query(SafetyBuildInfoRecordDTO param) {
        return safetyBuildInfoRecordHandler.query(param);
    }

    @ApiOperation(value = "更新")
    @RequestMapping(value="/update", method = {RequestMethod.GET,RequestMethod.POST})
    public Object update(SafetyBuildInfoRecordDTO param) {
        safetyBuildInfoRecordHandler.update(param);
        return null;
    }
}
