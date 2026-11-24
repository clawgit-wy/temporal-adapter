package com.yutong.safety.web.controller.web.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.build.param.BuildAutoDeptListParam;
import com.yutong.safety.core.db.entity.build.param.BuildAutoDeptUpdateParam;
import com.yutong.safety.core.handler.build.SafetyBuildAutoDeptHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "楼宇门禁-自动授权组织管理")
@RestController
@RequestMapping("/web/build/safety/autoDept")
public class SafetyBuildAutoDeptController {

    @Resource
    private SafetyBuildAutoDeptHandler safetyBuildAutoDeptHandler;

    /**
     * 黑名单信息管理数据列表查询方法
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value="/list", method = {RequestMethod.GET,RequestMethod.POST})
    public Object list(BuildAutoDeptListParam param,
                       @RequestHeader("X-Access-Token") String token) {
        return safetyBuildAutoDeptHandler.getPageList(param,token);
    }

    /**
     * 重新下发
     * @param param 请求参数
     * @return 更新状态
     */
    @ApiOperation(value = "更新")
    @RequestMapping(value = "/update", method = {RequestMethod.GET,RequestMethod.POST})
    public Object update(BuildAutoDeptUpdateParam param, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetyBuildAutoDeptHandler.update(param,token);
        }catch (Exception e){
            LogUtils.error("更新异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), ResponseEnum.FAIL.getName());
        }
    }

}
