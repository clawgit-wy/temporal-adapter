package com.yutong.modules.vehicle.controller;

import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.vehicle.service.IUserJob;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags="定时任务模块")
@RestController
@RequestMapping("/job/User")
public class UserJobController {

    @Autowired
    private IUserJob userJobService;

    /**
     * EIP用户同步信息
     *
     * @param processParamDTO
     * @return
     */
    @ApiOperation(value = "EIP用户同步信息", notes = "EIP用户同步信息")
    @PostMapping(value = "/processApprove")
    public Result<BpmReponseParamsVO> userSync() {
        userJobService.tasksMessage(true,"2026-08-11");
        return Result.OK();
    }
}
