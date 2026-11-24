package com.yutong.modules.risk.controller;

import com.yutong.modules.risk.entity.YTSafetyRiskLevelDict;
import com.yutong.modules.risk.entity.YtsafetyRiskGradingStandard;
import com.yutong.modules.risk.service.IYTSafetyRiskLevelDictService;
import com.yutong.modules.risk.service.IYtsafetyRiskGradingStandardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: 风险分级标准表
 * @Author: jeecg-boot
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "风险分级标准表")
@RestController
@RequestMapping("/risk/ytsafetyRiskGradingStandard")
public class YTSafetyRiskGradingStandardController {

    @Autowired
    private IYtsafetyRiskGradingStandardService riskGradingStandardService;

    @Autowired
    private IYTSafetyRiskLevelDictService riskLevelDictService;

    /**
     * 根据风险类型id和风险类别id查询分级标准
     */
    @ApiOperation(value = "风险分级标准1-暴露频率，2-风险可能性，3-风险严重性-根据风险类型和类别查询", notes = "根据风险类型id和风险类别id查询分级标准")
    @GetMapping(value = "/listByRiskTypeAndFormula")
    public Result<?> listByRiskTypeAndFormula(
            @ApiParam(value = "类型：1-暴露频率，2-风险可能性，3-风险严重性", required = true) @RequestParam(value = "type") Integer type,
            @ApiParam(value = "风险类型id", required = false) @RequestParam(value = "riskTypeId", required = false) Long riskTypeId,
            @ApiParam(value = "风险类别id", required = false) @RequestParam(value = "riskCategoryId",required = false) Long riskCategoryId) {
        List<YtsafetyRiskGradingStandard> list = riskGradingStandardService.listByRiskTypeAndFormula(type, riskTypeId, riskCategoryId);
        return Result.OK(list);
    }

    /**
     * 根据风险暴露频率、风险可能性、后果严重性计算风险等级
     */
    @ApiOperation(value = "风险分级标准表-计算风险等级", notes = "根据风险暴露频率、风险可能性、后果严重性计算风险等级")
    @GetMapping(value = "/calculateRiskLevel")
    public Result<YTSafetyRiskLevelDict> calculateRiskLevel(
            @ApiParam(value = "风险暴露频率") @RequestParam(value = "exposeFreq", required = false) Long exposeFreq,
            @ApiParam(value = "风险可能性") @RequestParam(value = "possibility") Long possibility,
            @ApiParam(value = "后果严重性") @RequestParam(value = "severity") Long severity) {
        YTSafetyRiskLevelDict dict = riskLevelDictService.calculateRiskLevel(exposeFreq, possibility, severity);
        return Result.OK(dict);
    }
}
