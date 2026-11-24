package com.yutong.modules.risk.controller;

import com.yutong.modules.risk.api.YTSafetyTaskExecutionService;
import com.yutong.modules.risk.vo.PatrolTaskGroupVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(tags = "任务-APP待办")
@RestController
@RequestMapping("/sys/task/app")
public class YTSafetyTaskAppController {

    @Autowired
    private YTSafetyTaskExecutionService ytSafetyTaskExecutionService;

    @AutoLog(value = "查询用户待办任务统计", operateType = 1)
    @ApiOperation(value = "查询用户待办任务统计（按taskType+postType分组）", notes = "查询当前登录用户的待办任务统计")
    @PostMapping(value = "/selectPatrolToDo")
    public Result<?> selectPatrolToDo() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (sysUser == null || sysUser.getId() == null) {
            return Result.error("用户未登录");
        }
        List<PatrolTaskGroupVO> list = ytSafetyTaskExecutionService.selectPatrolToDo(Long.valueOf(sysUser.getId()));
        return Result.OK(list);
    }
}
