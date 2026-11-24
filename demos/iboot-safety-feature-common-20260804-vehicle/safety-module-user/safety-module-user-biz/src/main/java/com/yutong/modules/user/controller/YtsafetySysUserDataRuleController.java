package com.yutong.modules.user.controller;



import com.yutong.modules.user.entity.YtsafetySysUserDataRule;
import com.yutong.modules.user.api.IYtsafetySysUserDataRuleService;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;


 /**
 * @Description: 用户菜单数据规则表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags="用户菜单数据规则表")
@RestController
@RequestMapping("/com.yutong.modules.user/ytsafetySysUserDataRule")
public class YtsafetySysUserDataRuleController extends JeecgController<YtsafetySysUserDataRule, IYtsafetySysUserDataRuleService> {
	@Autowired
	private IYtsafetySysUserDataRuleService ytsafetySysUserDataRuleService;


}
