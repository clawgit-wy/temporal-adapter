package com.yutong.safety.web.controller.app.dict;

import com.yutong.safety.common.param.web.sys.SystemParamListParam;
import com.yutong.safety.core.db.entity.dict.SafetySystemParamDO;
import com.yutong.safety.core.handler.dict.SafetySystemParamHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "系统参数-app")
@RestController
@RequestMapping("/app/safety/system/param")
public class SafetySystemParamAppController {

    @Autowired
    private SafetySystemParamHandler safetySystemParamHandler;

    @ApiOperation(value = "根据编码获取参数")
    @RequestMapping(value = "/getParamByCode",method = {RequestMethod.GET,RequestMethod.POST})
    public Object getParamByCode(String paramCode){
        if(StringUtils.isBlank(paramCode)){
            return null;
        }
        SystemParamListParam listParam = new SystemParamListParam();
        listParam.setParamCode(paramCode);
        List<SafetySystemParamDO> list = safetySystemParamHandler.getList(listParam);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }
}
