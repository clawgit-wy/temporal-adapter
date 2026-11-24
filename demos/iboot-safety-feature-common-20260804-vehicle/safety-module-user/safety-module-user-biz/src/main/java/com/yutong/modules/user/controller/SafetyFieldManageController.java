package com.yutong.modules.user.controller;

import com.yutong.modules.user.api.ISafetyFieldManageService;
import com.yutong.modules.user.dto.FieldManageListParam;
import com.yutong.modules.user.entity.SafetyFieldManage;
import com.yutong.modules.user.vo.FieldLinkageVO;
import com.yutong.modules.user.vo.FieldManagerTreeVO;
import com.yutong.modules.user.vo.SafetyFieldManageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

 /**
 * @Description: 厂区表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags="厂区表")
@RestController
@RequestMapping("/safety/fieldManage")
public class SafetyFieldManageController extends JeecgController<SafetyFieldManage, ISafetyFieldManageService> {
	@Autowired
	private ISafetyFieldManageService safetyFieldManageService;

    @ApiOperation(value="系统用户表-厂区下拉列表", notes="系统用户表-厂区下拉列表")
    @GetMapping("/list")
    public Result<?> list(){
        List<SafetyFieldManageVO> safetyFieldManageVOList = safetyFieldManageService.getList();
        return Result.OK(safetyFieldManageVOList);
    }

    @ApiOperation(value = "获取子类别树", notes = "获取子类别树", tags = {"岗位验证、业务验证", "岗位验证、业务验证-app"})
    @PostMapping(value = "/fieldChildTree")
    public Result<List<FieldManagerTreeVO>> fieldChildTree(@RequestBody FieldManageListParam param) {
        if (param.getParentId() == null && StringUtils.isEmpty(param.getFieldType())) {
            return Result.OK(new ArrayList<>());
        }
        List<FieldManagerTreeVO> tree = safetyFieldManageService.getSubTree(param);
        return Result.OK(tree);
    }

    @ApiOperation(value = "任务-获取厂区区域联动列表", notes = "获取厂区区域联动列表", tags = {"岗位验证、业务验证", "岗位验证、业务验证-app"})
    @PostMapping(value = "/selectLinkageList")
    public Result<List<FieldLinkageVO>> selectLinkageList() {
        List<FieldLinkageVO> list = safetyFieldManageService.selectLinkageList();
        return Result.OK(list);
    }

     @ApiOperation(value = "任务-获取厂区区域联动列表", notes = "获取厂区区域联动列表", tags = {"岗位验证、业务验证", "岗位验证、业务验证-app"})
     @PostMapping(value = "/selectLinkageListApp")
     public Result<List<FieldLinkageVO>> selectLinkageListApp() {
         List<FieldLinkageVO> list = safetyFieldManageService.selectLinkageListApp();
         return Result.OK(list);
     }

}
