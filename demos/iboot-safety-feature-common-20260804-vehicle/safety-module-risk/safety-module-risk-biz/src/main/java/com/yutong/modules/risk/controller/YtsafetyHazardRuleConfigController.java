package com.yutong.modules.risk.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.api.IYtsafetyHazardRuleConfigService;
import com.yutong.modules.risk.entity.YtsafetyHazardRuleConfig;
import com.yutong.modules.risk.param.YtsafetyHazardRuleConfigAddParam;
import com.yutong.modules.risk.param.YtsafetyHazardRuleConfigEditParam;
import com.yutong.modules.risk.param.YtsafetyHazardRuleConfigQueryParam;
import com.yutong.modules.risk.vo.YtsafetyHazardRuleConfigVo;
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
 * @Description: 隐患规则配置
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Slf4j
@Api(tags="隐患规则配置")
@RestController
@RequestMapping("/ytsafety/hazard/ruleConfig")
public class YtsafetyHazardRuleConfigController extends JeecgController<YtsafetyHazardRuleConfig, IYtsafetyHazardRuleConfigService> {
	@Autowired
	private IYtsafetyHazardRuleConfigService ytsafetyHazardRuleConfigService;
	
	/**
	 * 分页列表查询
	 *
	 * @param ytsafetyHazardRuleConfigQueryParam
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "隐患规则配置-分页列表查询")
	@ApiOperation(value="隐患规则配置-分页列表查询", notes="隐患规则配置-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<YtsafetyHazardRuleConfigVo>> queryPageList(YtsafetyHazardRuleConfigQueryParam ytsafetyHazardRuleConfigQueryParam,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
        Page<YtsafetyHazardRuleConfig> page = new Page<YtsafetyHazardRuleConfig>(pageNo, pageSize);
        IPage<YtsafetyHazardRuleConfigVo> pageList = ytsafetyHazardRuleConfigService.queryVehicleFreezeConfigList(page, ytsafetyHazardRuleConfigQueryParam);
		return Result.OK(pageList);
	}
	
	/**
	 * 添加
	 *
	 * @param ytsafetyHazardRuleConfigAddParam
	 * @return
	 */
	@AutoLog(value = "隐患规则配置-添加")
	@ApiOperation(value="隐患规则配置-添加", notes="隐患规则配置-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody YtsafetyHazardRuleConfigAddParam ytsafetyHazardRuleConfigAddParam) {
		ytsafetyHazardRuleConfigService.saveHazardRuleConfig(ytsafetyHazardRuleConfigAddParam);
		return Result.OK("添加成功！");
	}
	
	/**
	 * 编辑
	 *
	 * @param ytsafetyHazardRuleConfigEditParam
	 * @return
	 */
	@AutoLog(value = "隐患规则配置-编辑")
	@ApiOperation(value="隐患规则配置-编辑", notes="隐患规则配置-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<?> edit(@RequestBody YtsafetyHazardRuleConfigEditParam ytsafetyHazardRuleConfigEditParam) {
		ytsafetyHazardRuleConfigService.updateHazardRuleConfig(ytsafetyHazardRuleConfigEditParam);
		return Result.OK("编辑成功!");
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "隐患规则配置-通过id删除")
	@ApiOperation(value="隐患规则配置-通过id删除", notes="隐患规则配置-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		ytsafetyHazardRuleConfigService.removeRuleById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "隐患规则配置-批量删除")
	@ApiOperation(value="隐患规则配置-批量删除", notes="隐患规则配置-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.ytsafetyHazardRuleConfigService.removeRuleByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}


}
