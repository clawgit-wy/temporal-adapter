package com.yutong.safety.web.controller.app;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.handler.SafetyGroupHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "组织结构管理App")
@RestController
@RequestMapping("/app/safety/group")
public class SafetyGroupAppController {

    @Autowired
    private SafetyGroupHandler safetyGroupHandler;

    @ApiOperation(value = "模糊查询部门名称")
    @RequestMapping("/getDepartName")
    public Object getDepartName(String keyWords){
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), safetyGroupHandler.getDepartName(keyWords));
    }

    @ApiOperation(value = "获取组织结构树-非异步")
    @RequestMapping("/selectGroupTreeNonAsyn")
    public Object selectGroupTreeNonAsyn(String key){
        return safetyGroupHandler.selectGroupTreeNonAsyn();
    }
}
