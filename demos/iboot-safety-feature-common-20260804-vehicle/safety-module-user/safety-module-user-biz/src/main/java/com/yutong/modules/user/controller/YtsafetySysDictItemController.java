package com.yutong.modules.user.controller;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yutong.modules.user.dto.SafetyDictItemDTO;
import com.yutong.modules.user.query.SafetyDictItemQuery;
import com.yutong.modules.user.vo.SafetyDictItemVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CacheConstant;
import com.yutong.modules.user.entity.SafetyDictItem;
import com.yutong.modules.user.api.IYtsafetySysDictItemService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * @Description: 字典子表
 * @Author: jeecg-boot
 * @Date: 2026-05-27
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "字典子表")
@RestController
@RequestMapping("/safety/safetySysDictItem")
public class YtsafetySysDictItemController extends JeecgController<SafetyDictItem, IYtsafetySysDictItemService> {
    @Autowired
    private IYtsafetySysDictItemService sysDictItemService;


    @AutoLog(value = "字典子表-新增")
    @ApiOperation(value = "字典子表--新增")
    @RequiresPermissions("systemd:dict:add")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @CacheEvict(value = {CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries = true)
    public Result<SafetyDictItemVO> add(@Valid @RequestBody SafetyDictItemDTO sysDictItemDto) {
        sysDictItemService.add(sysDictItemDto);
        return Result.ok("添加成功");
    }



    @RequiresPermissions("systemd:dict:edit")
    @AutoLog(value = "字典子表-编辑")
    @ApiOperation(value = "字典子表--编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.POST})
    @CacheEvict(value = {CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries = true)
    public Result<SafetyDictItemVO> edit(@RequestBody SafetyDictItemDTO sysDictItem) {
        sysDictItemService.updateDictItem(sysDictItem);
        return Result.ok("编辑成功");
    }

    @RequiresPermissions("systemd:dict:delete")
    @AutoLog(value = "字典子表-删除")
    @ApiOperation(value = "字典子表--删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @CacheEvict(value = {CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries = true)
    public Result<?> delete(@RequestParam(name = "id", required = true) String id, @RequestParam(name = "type", required = true) String type) {
        sysDictItemService.deleteItem(id, type);
        return Result.ok("删除成功");
    }

    @RequiresPermissions("systemd:dict:list")
    @ApiOperation(value = "字典子表--根据id查询")
    @RequestMapping(value = "/selectById", method = RequestMethod.GET)
    @CacheEvict(value = {CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries = true)
    public Result<SafetyDictItemVO> selectById(@RequestParam(name = "id", required = true) @NotNull(message = "id不能为空")  Long id) {
        return Result.ok(sysDictItemService.selectById(id));
    }

    @RequiresPermissions("systemd:dict:select")
    @ApiOperation(value = "字典子表--查询字典树")
    @RequestMapping(value = "/pageList", method = RequestMethod.GET)
    public Result<List<SafetyDictItemVO>> pageList(@Valid SafetyDictItemQuery query) {
        List<SafetyDictItemVO> pageList = sysDictItemService.selectByName(query);
        return Result.OK(pageList);
    }

    @RequiresPermissions("systemd:dict:select")
    @ApiOperation(value = "字典子表--查询平铺字典")
    @RequestMapping(value = "/selectFlat", method = RequestMethod.GET)
    public Result<List<SafetyDictItemVO>> selectFlat(@Valid SafetyDictItemQuery query) {
        List<SafetyDictItemVO> list = sysDictItemService.selectFlat(query);
        return Result.OK(list);
    }

}
