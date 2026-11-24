package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.param.web.PersonCarOutRuleAddParam;
import com.yutong.safety.common.param.web.PersonCarOutRuleDeleteParam;
import com.yutong.safety.common.param.web.PersonCarOutRuleListParam;
import com.yutong.safety.common.param.web.PersonCarOutRuleUpdateParam;
import com.yutong.safety.core.handler.SafetyPersonCarOutRuleHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gwcuiyx
 * @date 2024-10-15 10:31
 */
@Api(tags = "私家车出厂检查规则管理")
@RestController
@RequestMapping("/web/safety/personCarOutRule")
public class SafetyPersonCarOutRuleController {
    @Autowired
    private SafetyPersonCarOutRuleHandler safetyPersonCarOutRuleHandler;

    /**
     * 数据添加接口
     * @param param
     * @return
     */
    @ApiOperation(value = "数据新增接口")
    @RequestMapping("/add")
    public Object add(PersonCarOutRuleAddParam param) {
        safetyPersonCarOutRuleHandler.add(param);
        return null;
    }

    /**
     * 数据更新接口
     * @param param
     * @return
     */
    @ApiOperation(value = "数据更新接口")
    @RequestMapping("/update")
    public Object update(PersonCarOutRuleUpdateParam param) {
        safetyPersonCarOutRuleHandler.update(param);
        return null;
    }

    /**
     * 数据删除接口
     * @param param
     * @return
     */
    @ApiOperation(value = "数据删除接口")
    @RequestMapping("/delete")
    public Object delete(PersonCarOutRuleDeleteParam param) {
        safetyPersonCarOutRuleHandler.delete(param);
        return null;
    }

    /**
     * 分页查询
     * @param param
     * @return
     */
    @ApiOperation(value = "数据删除接口")
    @RequestMapping("/pageList")
    public Object pageList(PersonCarOutRuleListParam param) {
        return safetyPersonCarOutRuleHandler.pageList(param);
    }


}
