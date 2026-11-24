package com.yutong.safety.web.controller.web.hik;

import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.handler.hik.SafetyArtemisHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "人脸信息检索")
@RestController
@RequestMapping("/web/safety/hik/faceSearch")
public class SafetyFaceSearchController {

    @Resource
    private SafetyArtemisHandler safetyArtemisHandler;

    @ApiOperation(value = "照片检索")
    @RequestMapping(value = "/searchPhoto", method = {RequestMethod.GET, RequestMethod.POST})
    public VO searchPhoto(CommonImportParam param) {
        return safetyArtemisHandler.searchPhoto(param);
    }
}
