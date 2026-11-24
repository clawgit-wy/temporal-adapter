package com.yutong.modules.user.controller;

import com.yutong.modules.user.api.fallback.IYtsafetyFieldManageFileService;
import com.yutong.modules.user.entity.YtsafetyFieldManageFile;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

 /**
 * @Description: 字段扩展表
 * @Author: jeecg-boot
 * @Date:   2026-06-29
 * @Version: V1.0
 */
@Slf4j
@Api(tags="字段扩展表")
@RestController
@RequestMapping("/safety/ytsafetyFieldManageFile")
public class YtsafetyFieldManageFileController extends JeecgController<YtsafetyFieldManageFile, IYtsafetyFieldManageFileService> {


}
