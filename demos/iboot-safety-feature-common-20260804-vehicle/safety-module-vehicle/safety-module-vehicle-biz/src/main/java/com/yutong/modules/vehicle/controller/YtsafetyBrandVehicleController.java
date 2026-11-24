package com.yutong.modules.vehicle.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.vehicle.entity.YtsafetyBrandVehicle;
import com.yutong.modules.vehicle.query.YtsafetyBrandVehicleQuery;
import com.yutong.modules.vehicle.service.IYtsafetyBrandVehicleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

 /**
  * @Description: 日韩品牌库
  * @Author: jeecg-boot
  * @Date:   2026-08-04
  * @Version: V1.0
  */
@Slf4j
@Api(tags="日韩品牌库")
@RestController
@RequestMapping("/vehicle/ytsafetyBrandVehicle")
public class YtsafetyBrandVehicleController extends JeecgController<YtsafetyBrandVehicle, IYtsafetyBrandVehicleService> {
	@Autowired
	private IYtsafetyBrandVehicleService ytsafetyBrandVehicleService;

	/**
	 * 分页列表查询
	 *
	 * @param query
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "日韩品牌库-分页列表查询")
	@ApiOperation(value="日韩品牌库-分页列表查询", notes="日韩品牌库-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<YtsafetyBrandVehicle>> queryPageList(YtsafetyBrandVehicleQuery query,
									   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									   HttpServletRequest req) {
		Page<YtsafetyBrandVehicle> page = new Page<YtsafetyBrandVehicle>(pageNo, pageSize);
		IPage<YtsafetyBrandVehicle> pageList = ytsafetyBrandVehicleService.queryPageList(page, query);
		return Result.OK(pageList);
	}

	/**
	 * 添加
	 *
	 * @param ytsafetyBrandVehicle
	 * @return
	 */
	@AutoLog(value = "日韩品牌库-添加")
	@ApiOperation(value="日韩品牌库-添加", notes="日韩品牌库-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody YtsafetyBrandVehicle ytsafetyBrandVehicle) {
		ytsafetyBrandVehicleService.saveBrandVehicle(ytsafetyBrandVehicle);
		return Result.OK("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param ytsafetyBrandVehicle
	 * @return
	 */
	@AutoLog(value = "日韩品牌库-编辑")
	@ApiOperation(value="日韩品牌库-编辑", notes="日韩品牌库-编辑")
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public Result<String> edit(@RequestBody YtsafetyBrandVehicle ytsafetyBrandVehicle) {
		ytsafetyBrandVehicleService.editBrandVehicle(ytsafetyBrandVehicle);
		return Result.OK("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "日韩品牌库-通过id删除")
	@ApiOperation(value="日韩品牌库-通过id删除", notes="日韩品牌库-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		ytsafetyBrandVehicleService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "日韩品牌库-通过id查询")
	@ApiOperation(value="日韩品牌库-通过id查询", notes="日韩品牌库-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<YtsafetyBrandVehicle> queryById(@RequestParam(name="id",required=true) String id) {
		YtsafetyBrandVehicle ytsafetyBrandVehicle = ytsafetyBrandVehicleService.getById(id);
		return Result.OK(ytsafetyBrandVehicle);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param query
	 */
	@AutoLog(value = "日韩品牌库-导出", operateType = 5)
	@ApiOperation(value="日韩品牌库-导出excel", notes="日韩品牌库-导出excel")
	@GetMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, YtsafetyBrandVehicleQuery query) {
		List<YtsafetyBrandVehicle> list = ytsafetyBrandVehicleService.queryList(query);
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		mv.addObject(NormalExcelConstants.FILE_NAME, "日韩品牌库");
		mv.addObject(NormalExcelConstants.CLASS, YtsafetyBrandVehicle.class);
		LoginUser sysUser = BusUtits.getLoginUser();
		ExportParams exportParams = new ExportParams("日韩品牌库", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "日韩品牌库");
		mv.addObject(NormalExcelConstants.PARAMS, exportParams);
		mv.addObject(NormalExcelConstants.DATA_LIST, list);
		return mv;
	}

}
