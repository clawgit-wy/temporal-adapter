package com.yutong.modules.vehicle.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.vehicle.api.IYtsafetyVehicleFreezeConfigService;
import com.yutong.modules.vehicle.entity.YtsafetyVehicleFreezeConfig;
import com.yutong.modules.vehicle.param.YtsafetyVehicleFreezeConfigAddParam;
import com.yutong.modules.vehicle.param.YtsafetyVehicleFreezeConfigEditParam;
import com.yutong.modules.vehicle.param.YtsafetyVehicleFreezeConfigQueryParam;
import com.yutong.modules.vehicle.vo.YtsafetyVehicleFreezeConfigVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

 /**
 * @Description: 车辆冻结配
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Slf4j
@Api(tags="车辆冻结配")
@RestController
@RequestMapping("/ytsafety/vehicle/freezeConfig")
public class YtsafetyVehicleFreezeConfigController extends JeecgController<YtsafetyVehicleFreezeConfig, IYtsafetyVehicleFreezeConfigService> {
	@Autowired
	private IYtsafetyVehicleFreezeConfigService ytsafetyVehicleFreezeConfigService;
	
	/**
	 * 分页列表查询
	 *
	 * @param ytsafetyVehicleFreezeConfigQueryParam
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "车辆冻结配-分页列表查询")
	@ApiOperation(value="车辆冻结配-分页列表查询", notes="车辆冻结配-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<YtsafetyVehicleFreezeConfigVo>> queryPageList(YtsafetyVehicleFreezeConfigQueryParam ytsafetyVehicleFreezeConfigQueryParam,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
		Page<YtsafetyVehicleFreezeConfig> page = new Page<YtsafetyVehicleFreezeConfig>(pageNo, pageSize);
        IPage<YtsafetyVehicleFreezeConfigVo> pageList = ytsafetyVehicleFreezeConfigService.queryVehicleFreezeConfigList(page, ytsafetyVehicleFreezeConfigQueryParam);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param ytsafetyVehicleFreezeConfigAddParam
	 * @return
	 */
	@AutoLog(value = "车辆冻结配-添加")
	@ApiOperation(value="车辆冻结配-添加", notes="车辆冻结配-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody YtsafetyVehicleFreezeConfigAddParam ytsafetyVehicleFreezeConfigAddParam) {
		ytsafetyVehicleFreezeConfigService.saveFreezeConfig(ytsafetyVehicleFreezeConfigAddParam);
		return Result.OK("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param ytsafetyVehicleFreezeConfigEditParam
	 * @return
	 */
	@AutoLog(value = "车辆冻结配-编辑")
	@ApiOperation(value="车辆冻结配-编辑", notes="车辆冻结配-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody YtsafetyVehicleFreezeConfigEditParam ytsafetyVehicleFreezeConfigEditParam) {
		ytsafetyVehicleFreezeConfigService.updateFreezeConfig(ytsafetyVehicleFreezeConfigEditParam);
		return Result.OK("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "车辆冻结配-通过id删除")
	@ApiOperation(value="车辆冻结配-通过id删除", notes="车辆冻结配-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		ytsafetyVehicleFreezeConfigService.removeFreezeConfigById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "车辆冻结配-批量删除")
	@ApiOperation(value="车辆冻结配-批量删除", notes="车辆冻结配-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.ytsafetyVehicleFreezeConfigService.removeFreezeConfigByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}

}
