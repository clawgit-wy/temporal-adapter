package com.yutong.safety.web.controller.app.enter;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.enter.SafetyCrkCarHistoryHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "安保入厂APP-出入口车辆记录")
@RestController
@RequestMapping("/app/enter/crkOutHistory/")
public class SafetyCrkCarOutAppController {

    @Autowired
    private SafetyCrkCarHistoryHandler safetyCrkCarHistoryHandler;

    /**
     * 时生成点检任务
     * @return 返回空
     */
    @ApiOperation(value = "定时更新车辆出入记录")
    @RequestMapping(value = "/taskUpdateCrkHistory", method = {RequestMethod.GET,RequestMethod.POST})
    public Object taskUpdateCrkHistory(String beginTime) {
        try {
            safetyCrkCarHistoryHandler.updateBatch(beginTime);
        }catch (Exception e){
            LogUtils.error("更新车辆出入记录异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"更新车辆出入记录异常");
        }
        return null;
    }
}
