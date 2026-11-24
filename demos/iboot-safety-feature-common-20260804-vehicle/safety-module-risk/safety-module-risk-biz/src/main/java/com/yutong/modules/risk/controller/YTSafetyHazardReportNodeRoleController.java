package com.yutong.modules.risk.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.api.IYTSafetyHazardReportNodeRoleService;
import com.yutong.modules.risk.entity.YTSafetyHazardReportNodeRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

 /**
 * @Description: 隐患上报表流程节点配置角色
 * @Author: jeecg-boot
 * @Date:   2026-07-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags="隐患上报表流程节点配置角色")
@RestController
@RequestMapping("/com.yutong/yTSafetyHazardReportNodeRole")
public class YTSafetyHazardReportNodeRoleController extends JeecgController<YTSafetyHazardReportNodeRole, IYTSafetyHazardReportNodeRoleService> {
	@Autowired
	private IYTSafetyHazardReportNodeRoleService yTSafetyHazardReportNodeRoleService;
	
	/**
	 * 分页列表查询
	 *
	 * @param yTSafetyHazardReportNodeRole
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "隐患上报表流程节点配置角色-分页列表查询")
	@ApiOperation(value="隐患上报表流程节点配置角色-分页列表查询", notes="隐患上报表流程节点配置角色-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(YTSafetyHazardReportNodeRole yTSafetyHazardReportNodeRole,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<YTSafetyHazardReportNodeRole> queryWrapper = QueryGenerator.initQueryWrapper(yTSafetyHazardReportNodeRole, req.getParameterMap());
		Page<YTSafetyHazardReportNodeRole> page = new Page<YTSafetyHazardReportNodeRole>(pageNo, pageSize);
		IPage<YTSafetyHazardReportNodeRole> pageList = yTSafetyHazardReportNodeRoleService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param yTSafetyHazardReportNodeRole
	 * @return
	 */
	@AutoLog(value = "隐患上报表流程节点配置角色-添加")
	@ApiOperation(value="隐患上报表流程节点配置角色-添加", notes="隐患上报表流程节点配置角色-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody YTSafetyHazardReportNodeRole yTSafetyHazardReportNodeRole) {
		yTSafetyHazardReportNodeRoleService.save(yTSafetyHazardReportNodeRole);
		return Result.OK("添加成功！");
	}

//	/**
//	 * 编辑
//	 *
//	 * @param yTSafetyHazardReportNodeRole
//	 * @return
//	 */
//	@AutoLog(value = "隐患上报表流程节点配置角色-编辑")
//	@ApiOperation(value="隐患上报表流程节点配置角色-编辑", notes="隐患上报表流程节点配置角色-编辑")
//	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
//	public Result<?> edit(@RequestBody YTSafetyHazardReportNodeRole yTSafetyHazardReportNodeRole) {
//		yTSafetyHazardReportNodeRoleService.updateById(yTSafetyHazardReportNodeRole);
//		return Result.OK("编辑成功!");
//	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "隐患上报表流程节点配置角色-批量删除")
	@ApiOperation(value="隐患上报表流程节点配置角色-批量删除", notes="隐患上报表流程节点配置角色-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.yTSafetyHazardReportNodeRoleService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
//	/**
//	 * 通过id查询
//	 *
//	 * @param id
//	 * @return
//	 */
//	@AutoLog(value = "隐患上报表流程节点配置角色-通过id查询")
//	@ApiOperation(value="隐患上报表流程节点配置角色-通过id查询", notes="隐患上报表流程节点配置角色-通过id查询")
//	@GetMapping(value = "/queryById")
//	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
//		YTSafetyHazardReportNodeRole yTSafetyHazardReportNodeRole = yTSafetyHazardReportNodeRoleService.getById(id);
//		return Result.OK(yTSafetyHazardReportNodeRole);
//	}

}
