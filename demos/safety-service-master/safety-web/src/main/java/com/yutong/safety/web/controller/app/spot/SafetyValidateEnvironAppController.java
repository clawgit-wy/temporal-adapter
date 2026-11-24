package com.yutong.safety.web.controller.app.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.spot.ValidateEnvironmentAddParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.spot.SafetyValidateEnvironmentHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gwxiexj
 * @time 2024-06-12 10:34
 */
@Api(tags = "业务验证-货车环保标准app")
@RestController
@RequestMapping("/app/safety/validate/environment")
public class SafetyValidateEnvironAppController {

    @Autowired
    private SafetyValidateEnvironmentHandler safetyValidateEnvironmentHandler;

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @ApiOperation(value = "添加接口")
    @RequestMapping("/add")
    public Object add(ValidateEnvironmentAddParam param) {
        try{
            param.setEventSource(1);
            //保存
            return safetyValidateEnvironmentHandler.save(param);
        }catch (Exception e){
            LogUtils.error("",2);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"");
        }
    }
}
