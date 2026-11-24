package com.yutong.safety.web.controller.web.park;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.park.param.ParkPowerRuleAddParam;
import com.yutong.safety.core.db.entity.park.param.ParkPowerRuleListParam;
import com.yutong.safety.core.handler.park.SafetyParkPowerRuleHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "园区权限-默认授权规则")
@RestController
@RequestMapping("/web/park/safety/parkPowerRule")
public class SafetyParkPowerRuleController {

    @Resource
    private SafetyParkPowerRuleHandler safetyParkPowerRuleHandler;

    /**
     * 黑名单信息管理数据列表查询方法
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value="/list", method = {RequestMethod.GET,RequestMethod.POST})
    public Object list(ParkPowerRuleListParam param,
                       @RequestHeader("X-Access-Token") String token) {
        return safetyParkPowerRuleHandler.pageList(param);
    }

    /**
     * 重新下发
     * @param param 请求参数
     * @return 更新状态
     */
    @ApiOperation(value = "保存或更新")
    @RequestMapping(value = "/saveOrUpdate", method = {RequestMethod.GET,RequestMethod.POST})
    public Object insert(ParkPowerRuleAddParam param, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetyParkPowerRuleHandler.saveOrUpdate(param);
        }catch (Exception e){
            LogUtils.error("更新异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), ResponseEnum.FAIL.getName());
        }
    }

    /**
     * 重新下发
     * @param idsStr ID拼接串
     * @return 更新状态
     */
    @ApiOperation(value = "更新安保相关配置")
    @RequestMapping(value = "/deleteBatch", method = {RequestMethod.GET,RequestMethod.POST})
    public Object deleteBatch(String idsStr, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetyParkPowerRuleHandler.deleteBatch(idsStr);
        }catch (Exception e){
            LogUtils.error("更新异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), ResponseEnum.FAIL.getName());
        }
    }
}
