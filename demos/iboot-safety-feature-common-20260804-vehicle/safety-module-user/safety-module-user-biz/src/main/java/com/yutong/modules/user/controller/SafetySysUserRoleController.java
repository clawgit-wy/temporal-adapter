package com.yutong.modules.user.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import com.yutong.modules.user.entity.SafetySysUserRole;
import com.yutong.modules.user.api.ISafetySysUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 账号角色关联表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags="账号角色关联表")
@RestController
@RequestMapping("/safetySysUserRole")
public class SafetySysUserRoleController extends JeecgController<SafetySysUserRole, ISafetySysUserRoleService> {
	@Autowired
	private ISafetySysUserRoleService safetySysUserRoleService;
	
	/**
	 * 分页列表查询
	 *
	 * @param safetySysUserRole
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="账号角色关联表-分页列表查询", notes="账号角色关联表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SafetySysUserRole safetySysUserRole,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SafetySysUserRole> queryWrapper = QueryGenerator.initQueryWrapper(safetySysUserRole, req.getParameterMap());
		Page<SafetySysUserRole> page = new Page<SafetySysUserRole>(pageNo, pageSize);
		IPage<SafetySysUserRole> pageList = safetySysUserRoleService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param safetySysUserRole
	 * @return
	 */
	@AutoLog(value = "账号角色关联表-添加")
	@ApiOperation(value="账号角色关联表-添加", notes="账号角色关联表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SafetySysUserRole safetySysUserRole) {
		safetySysUserRoleService.save(safetySysUserRole);
		return Result.OK("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param safetySysUserRole
	 * @return
	 */
	@AutoLog(value = "账号角色关联表-编辑")
	@ApiOperation(value="账号角色关联表-编辑", notes="账号角色关联表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody SafetySysUserRole safetySysUserRole) {
		safetySysUserRoleService.updateById(safetySysUserRole);
		return Result.OK("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "账号角色关联表-通过id删除")
	@ApiOperation(value="账号角色关联表-通过id删除", notes="账号角色关联表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		safetySysUserRoleService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "账号角色关联表-批量删除")
	@ApiOperation(value="账号角色关联表-批量删除", notes="账号角色关联表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.safetySysUserRoleService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="账号角色关联表-通过id查询", notes="账号角色关联表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		SafetySysUserRole safetySysUserRole = safetySysUserRoleService.getById(id);
		return Result.OK(safetySysUserRole);
	}


}
