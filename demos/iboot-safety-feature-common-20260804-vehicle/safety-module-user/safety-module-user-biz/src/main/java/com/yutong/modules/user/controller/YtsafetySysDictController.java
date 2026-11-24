package com.yutong.modules.user.controller;


import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import com.yutong.modules.user.dto.SafetyDictItemDTO;
import com.yutong.modules.user.dto.SafetyDictSupperDTO;
import com.yutong.modules.user.query.SafetyDictItemQuery;
import com.yutong.modules.user.query.SafetyDictSupperQuery;
import com.yutong.modules.user.api.IYtsafetySysDictItemService;
import com.yutong.modules.user.vo.SafetyDictItemVO;
import com.yutong.modules.user.vo.SafetyDictSupperVO;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CacheConstant;
import com.yutong.modules.user.entity.SafetyDictSupper;
import com.yutong.modules.user.api.IYtsafetySysDictService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;

/**
 * @Description: 字典表
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
@Slf4j
@Api(tags="字典表")
@RestController
@RequestMapping("/safety/dict")
public class YtsafetySysDictController extends JeecgController<SafetyDictSupper, IYtsafetySysDictService> {
	@Autowired
	private IYtsafetySysDictService sysDictService;
     @Autowired
     private IYtsafetySysDictItemService sysDictItemService;



    @ApiOperation(value = "字典表--分页查询")
    @RequiresPermissions("systemd:dict:list")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
     public Result<IPage<SafetyDictSupperVO>> queryPageList(SafetyDictSupperQuery safetyDictSupperQuery, @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
         Result<IPage<SafetyDictSupperVO>> result = new Result<IPage<SafetyDictSupperVO>>();
         Page<SafetyDictSupperVO> page = new Page<SafetyDictSupperVO>(pageNo, pageSize);
         IPage<SafetyDictSupperVO> pageList = sysDictService.pageList(page, safetyDictSupperQuery);
         result.setSuccess(true);
         result.setResult(pageList);
         return result;
     }


     /**
      * @功能：新增
      * @param sysDict
      * @return
      */
     @AutoLog(value = "字典表-新增")
     @ApiOperation(value = "字典表--新增")
     @RequiresPermissions("systemd:dict:add")
     @RequestMapping(value = "/add", method = RequestMethod.POST)
     public Result<SafetyDictSupperVO> add(@RequestBody SafetyDictSupperDTO sysDict) {
         sysDictService.add(sysDict);
         return Result.ok("添加成功");
     }

     /**
      * @功能：编辑
      * @param
      * @return
      */
     @AutoLog(value = "字典表-编辑")
     @ApiOperation(value = "字典表--编辑")
     @RequiresPermissions("systemd:dict:edit")
     @RequestMapping(value = "/edit", method = { RequestMethod.POST })
     public Result<SafetyDictSupperVO> edit(@RequestBody SafetyDictSupperDTO dto) {

         sysDictService.edit(dto);
         return Result.ok("编辑成功");
     }

    /**
     * @功能：编辑
     * @param
     * @return
     */
    @ApiOperation(value = "字典表--通过id查询")
    //@RequiresPermissions("systemd:dict:edit")
    @RequestMapping(value = "/getById", method = { RequestMethod.GET })
    public Result<SafetyDictSupperVO> getById(@RequestParam("id") @NotNull(message = "id不能为空") Long id) {

        return Result.ok(sysDictService.getTreeById(id));
    }

     /**
      * @功能：删除
      * @param id
      * @return
      */
     @AutoLog(value = "字典表-删除")
     @ApiOperation(value = "字典表--删除")
     @RequiresPermissions("systemd:dict:delete")
     @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
     @CacheEvict(value={CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries=true)
     public Result<SafetyDictSupperVO> delete(@RequestParam(name="id",required=true) Long id) {
         sysDictService.deleteDict(id);
         return Result.ok("删除成功");
     }

    @ApiOperation(value="字典表-通过编码查询子项", notes="字典表-通过编码查询子项")
    @GetMapping("/selectList")
    public Result<List<SafetyDictItemVO>>  selectList(SafetyDictItemQuery query){
         return  Result.ok(sysDictItemService.selectList(query));
     }

 }
