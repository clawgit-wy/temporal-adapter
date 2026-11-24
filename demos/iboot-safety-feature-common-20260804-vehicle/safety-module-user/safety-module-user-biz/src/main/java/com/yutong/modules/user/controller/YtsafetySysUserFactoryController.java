package com.yutong.modules.user.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import com.yutong.modules.user.entity.YtsafetySysUserFactory;
import com.yutong.modules.user.api.IYtsafetySysUserFactoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 用户授权厂区表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags="用户授权厂区表")
@RestController
@RequestMapping("/ytsafetySysUserFactory")
public class YtsafetySysUserFactoryController extends JeecgController<YtsafetySysUserFactory, IYtsafetySysUserFactoryService> {
	@Autowired
	private IYtsafetySysUserFactoryService ytsafetySysUserFactoryService;
	
	/**
	 * 分页列表查询
	 *
	 * @param ytsafetySysUserFactory
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="用户授权厂区表-分页列表查询", notes="用户授权厂区表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(YtsafetySysUserFactory ytsafetySysUserFactory,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<YtsafetySysUserFactory> queryWrapper = QueryGenerator.initQueryWrapper(ytsafetySysUserFactory, req.getParameterMap());
		Page<YtsafetySysUserFactory> page = new Page<YtsafetySysUserFactory>(pageNo, pageSize);
		IPage<YtsafetySysUserFactory> pageList = ytsafetySysUserFactoryService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param ytsafetySysUserFactory
	 * @return
	 */
	@AutoLog(value = "用户授权厂区表-添加")
	@ApiOperation(value="用户授权厂区表-添加", notes="用户授权厂区表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody YtsafetySysUserFactory ytsafetySysUserFactory) {
		ytsafetySysUserFactoryService.save(ytsafetySysUserFactory);
		return Result.OK("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param ytsafetySysUserFactory
	 * @return
	 */
	@AutoLog(value = "用户授权厂区表-编辑")
	@ApiOperation(value="用户授权厂区表-编辑", notes="用户授权厂区表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody YtsafetySysUserFactory ytsafetySysUserFactory) {
		ytsafetySysUserFactoryService.updateById(ytsafetySysUserFactory);
		return Result.OK("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用户授权厂区表-通过id删除")
	@ApiOperation(value="用户授权厂区表-通过id删除", notes="用户授权厂区表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		ytsafetySysUserFactoryService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "用户授权厂区表-批量删除")
	@ApiOperation(value="用户授权厂区表-批量删除", notes="用户授权厂区表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.ytsafetySysUserFactoryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="用户授权厂区表-通过id查询", notes="用户授权厂区表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		YtsafetySysUserFactory ytsafetySysUserFactory = ytsafetySysUserFactoryService.getById(id);
		return Result.OK(ytsafetySysUserFactory);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param ytsafetySysUserFactory
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, YtsafetySysUserFactory ytsafetySysUserFactory) {
      return super.exportXls(request, ytsafetySysUserFactory, YtsafetySysUserFactory.class, "用户授权厂区表");
  }

  /**
   * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      return super.importExcel(request, response, YtsafetySysUserFactory.class);
  }

}
