package com.yutong.hksync.controller;

import com.yutong.hksync.common.VO;
import com.yutong.hksync.handler.HikMiddleHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/app/safety/hik/job")
public class HikMiddleJobController {

    @Resource
    private HikMiddleHandler hikMiddleHandler;


    @RequestMapping(value = "/syncToHkAdd", method = {RequestMethod.POST})
    public VO syncToHkAdd() {
        return hikMiddleHandler.syncToHkAdd();
    }
}
