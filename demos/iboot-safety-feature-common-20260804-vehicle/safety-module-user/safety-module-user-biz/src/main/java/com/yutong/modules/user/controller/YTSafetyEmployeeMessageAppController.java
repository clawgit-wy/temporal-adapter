package com.yutong.modules.user.controller;

import com.yutong.modules.user.api.ISafetyEmployeeMessageService;
import com.yutong.modules.user.dto.EmployeeMessageListParam;
import com.yutong.modules.user.entity.SafetyEmployeeMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: EHR员工信息
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags="EHR员工信息")
@RestController
@RequestMapping("/app/safety/employeeMessage")
public class YTSafetyEmployeeMessageAppController extends JeecgController<SafetyEmployeeMessage, ISafetyEmployeeMessageService> {
	@Autowired
	private ISafetyEmployeeMessageService safetyEmployeeMessageService;

    /**
     * 查询员工列表
     * @param param 查询参数
     * @return 员工列表
     */
    @ApiOperation(value = "查询员工列表", notes = "查询员工列表", tags = {"岗位验证、业务验证", "岗位验证、业务验证-app"})
    @PostMapping("/list")
    public Result<List<SafetyEmployeeMessage>> list(EmployeeMessageListParam param) {
        List<SafetyEmployeeMessage> list = safetyEmployeeMessageService.pageList(param);
        return Result.OK(list);
    }
}
