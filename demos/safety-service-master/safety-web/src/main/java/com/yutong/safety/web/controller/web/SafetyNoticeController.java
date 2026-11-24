package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.param.web.NoticeParam;
import com.yutong.safety.core.handler.SafetyNoticeHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  create gwcuiyx 2023.07.12
 *  访客须知--controller
 */
@Api(tags = "访客须知")
@RestController
@RequestMapping("/web/safety/notice")
public class SafetyNoticeController {
    @Autowired
    private SafetyNoticeHandler safetyNoticeHandler;

    /**
     * 获取数据操作接口
     * @return
     */
    @ApiOperation(value = "数据详情接口")
    @RequestMapping("/info")
    public Object getNotice() {
        return safetyNoticeHandler.getNotice();
    }

    /**
     * 数据新增或者更新操作
     * @param param
     * @return
     */
    @ApiOperation(value = "数据新增或者更新接口")
    @RequestMapping("/saveOrUpdate")
    public Object saveOrUpdate(NoticeParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyNoticeHandler.saveOrUpdate(param, tocken);
    }

}
