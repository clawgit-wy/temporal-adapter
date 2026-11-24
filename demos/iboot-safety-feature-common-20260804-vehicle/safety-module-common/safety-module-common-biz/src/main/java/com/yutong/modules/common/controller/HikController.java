package com.yutong.modules.common.controller;

import com.yutong.modules.common.api.IHikArtemisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "海康视频回放")
@RestController
@RequestMapping("/app/hik")
public class HikController {

    @Autowired
    private IHikArtemisService hikArtemisService;

    @ApiOperation(value = "获取设备预览地址", notes = "根据locationId获取设备预览url")
    @GetMapping("/getPreviewUrl")
    public Result<String> getPreviewUrl(@RequestParam("locationId") String locationId) {
        // TODO: 根据locationId查询设备信息并生成预览url
        String url = "";
        if (url != null) {
            return Result.OK(url);
        }
        return Result.error("未找到对应的设备");
    }
}
