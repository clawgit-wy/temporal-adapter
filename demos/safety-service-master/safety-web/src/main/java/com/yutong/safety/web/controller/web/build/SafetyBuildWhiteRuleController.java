package com.yutong.safety.web.controller.web.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.build.SafetyBuildWhiteRuleDO;
import com.yutong.safety.core.db.entity.build.param.BuildWhiteRuleListParam;
import com.yutong.safety.core.handler.build.SafetyBuildWhiteRuleHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "楼宇门禁-白名单规则管理")
@RestController
@RequestMapping("/web/build/safety/buildWhiteRule")
public class SafetyBuildWhiteRuleController {

    @Resource
    private SafetyBuildWhiteRuleHandler safetyBuildWhiteRuleHandler;

    /**
     * 黑名单信息管理数据列表查询方法
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value="/pageList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object pageList(BuildWhiteRuleListParam param,
                       @RequestHeader("X-Access-Token") String token) {
        return safetyBuildWhiteRuleHandler.pageList(param,token);
    }

    /**
     * 重新下发
     * @param param 请求参数
     * @return 更新状态
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/add", method = {RequestMethod.GET,RequestMethod.POST})
    public Object add(SafetyBuildWhiteRuleDO param, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetyBuildWhiteRuleHandler.save(param,token);
        }catch (Exception e){
            LogUtils.error("新增异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), ResponseEnum.FAIL.getName());
        }
    }

    /**
     * 重新下发
     * @param ids 请求参数
     * @return 更新状态
     */
    @ApiOperation(value = "批量删除")
    @RequestMapping(value = "/deleteBatch", method = {RequestMethod.GET,RequestMethod.POST})
    public Object deleteBatch(String ids, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetyBuildWhiteRuleHandler.deleteBatchs(ids,token);
        }catch (Exception e){
            LogUtils.error("批量删除", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), ResponseEnum.FAIL.getName());
        }
    }
}
