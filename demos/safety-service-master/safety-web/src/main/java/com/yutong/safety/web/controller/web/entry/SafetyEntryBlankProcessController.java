package com.yutong.safety.web.controller.web.entry;


import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDetailDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDetailDOExample;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfig;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryBlankProcessDetailAppParams;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryBlankProcessDetailQueryParams;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.handler.entry.SafetyEntryBlankProcessDetailHandler;
import com.yutong.safety.web.controller.web.AbstractSafetyWebBaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(tags = "黑名单入场备案记录")
@RestController
@RequestMapping("/web/entry/safety/entryBlackRecord")
public class SafetyEntryBlankProcessController extends AbstractSafetyWebBaseController<SafetyEntryBlankProcessDetailDO, SafetyEntryBlankProcessDetailDOExample, SafetyEntryBlankProcessDetailQueryParams, SafetyEntryBlankProcessDetailAppParams> {


    @Resource
    private SafetyEntryBlankProcessDetailHandler safetyEntryBlankProcessDetailHandler;

    @Override
    public AbstractSafetyWebBaseHandler<SafetyEntryBlankProcessDetailDO, SafetyEntryBlankProcessDetailDOExample, SafetyEntryBlankProcessDetailQueryParams, SafetyEntryBlankProcessDetailAppParams> getHandler() {
        return safetyEntryBlankProcessDetailHandler;
    }

    @ApiOperation(value = "流程详情页")
    @RequestMapping(value = "/getDetail", method = {RequestMethod.GET})
    public VO getDetail(Integer processId, @RequestHeader("X-Access-Token") String token) {
        return safetyEntryBlankProcessDetailHandler.getDetail(processId, token);
    }

    /**
     * 黑名单流程新增黑名单数据黑名单信息校验
     */
    @ApiOperation(value = "新增数据校验")
    @RequestMapping(value = "/checkInfo", method = {RequestMethod.POST})
    public VO checkInfo(@RequestBody SafetyEntryConfig info, @RequestHeader("X-Access-Token") String token) {
        return safetyEntryBlankProcessDetailHandler.checkInfo(info, token);
    }
}
