package com.yutong.modules.user.controller;


import com.yutong.modules.user.entity.SafetyGroupInfo;
import com.yutong.modules.user.api.ISafetyGroupInfoService;
import com.yutong.modules.user.vo.SafetyGroupInfoOptionListVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import java.util.List;

/**
 * @Description: 部门表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags="部门表")
@RestController
@RequestMapping("/sys/group/info")
public class SafetyGroupInfoController extends JeecgController<SafetyGroupInfo, ISafetyGroupInfoService> {
	@Autowired
	private ISafetyGroupInfoService safetyGroupInfoService;

    /**
     * 获取部门下拉框列表
     *
     * @return 部门列表
     */
    @ApiOperation(value="部门 - 获取部门下拉框列表", notes="部门 - 获取部门下拉框列表")
    @GetMapping(value = "/optionList")
    public Result<?> optionlist() {
        List<SafetyGroupInfoOptionListVO> list = safetyGroupInfoService.optionlist();
        return Result.OK(list);
    }

}
