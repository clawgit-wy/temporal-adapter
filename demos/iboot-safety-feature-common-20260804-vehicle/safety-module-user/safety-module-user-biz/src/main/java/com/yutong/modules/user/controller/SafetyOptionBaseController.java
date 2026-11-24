package com.yutong.modules.user.controller;


import com.yutong.modules.user.api.ISafetyEmployeeMessageService;
import com.yutong.modules.user.vo.JobOptionListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: 下拉数据
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags="下拉数据")
@RestController
@RequestMapping("/sys/option")
public class SafetyOptionBaseController{

    @Autowired
    private ISafetyEmployeeMessageService safetyEmployeeMessageService;

    /**
     * 获取岗位下拉框列表
     *
     * @return 岗位列表
     */
    @ApiOperation(value="岗位 - 获取岗位下拉框列表", notes="岗位 - 获取岗位下拉框列表")
    @GetMapping(value = "/jobOptionList")
    public Result<?> jobOptionList() {
        List<JobOptionListVO> listVOS = safetyEmployeeMessageService.jobOptionList();
        return Result.OK(listVOS);
    }

}