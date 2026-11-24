package com.yutong.modules.risk.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.entity.YTSafetyValidateEnvironment;
import com.yutong.modules.risk.handler.YTSafetyValidateEnvironmentHandler;
import com.yutong.modules.risk.web.ValidateEnvironmentAddParam;
import com.yutong.modules.risk.web.ValidateEnvironmentListParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 业务验证-货车环保标准抽查结果
 * @author gwxiexj
 * @since 2024-06-11 16:45
 */
@Api(tags = "岗位验证、业务验证")
@RestController
@RequestMapping("/web/safety/validate/environment")
public class YTSafetyValidateEnvironmentController {

    @Autowired
    private YTSafetyValidateEnvironmentHandler safetyValidateEnvironmentHandler;

    /**
     * 设备设施点检-设备信息管理列表查询方法
     * @param param
     * @return
     */
    @AutoLog(value = "货车环保标准抽查-分页数据列表查询", operateType = 1)
    @ApiOperation(value = "货车环保标准抽查-分页数据列表查询接口")
    @PostMapping("/listPage")
    public Result<IPage<YTSafetyValidateEnvironment>> listPage(@RequestBody ValidateEnvironmentListParam param) {
        Page<YTSafetyValidateEnvironment> page = new Page<>(param.getPageNo(), param.getPageSize());
        IPage<YTSafetyValidateEnvironment> pageList = safetyValidateEnvironmentHandler.pageList(page, param);
        return Result.OK(pageList);
    }

    /**
     * 数据导出操作
     * @param param
     */
    @AutoLog(value = "货车环保标准抽查-导出", operateType = 1)
    @ApiOperation(value = "货车环保标准抽查-数据导出接口")
    @GetMapping("/export")
    public ModelAndView export(HttpServletRequest request, ValidateEnvironmentListParam param){
        return safetyValidateEnvironmentHandler.exportEnvironment(request, param);
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @AutoLog(value = "货车环保标准抽查-添加", operateType = 2)
    @ApiOperation(value = "货车环保标准抽查-添加接口")
    @PostMapping("/add")
    public Result<?> add(@RequestBody ValidateEnvironmentAddParam param){
        param.setEventSource(1);
        param.setBusinessType("货车环保标准抽查");
        //保存
        return Result.OK(safetyValidateEnvironmentHandler.save(param));
    }

    /**
     * 数据修改操作
     * @param param
     * @return
     */
    @AutoLog(value = "货车环保标准抽查-修改", operateType = 3)
    @ApiOperation(value = "货车环保标准抽查-修改接口")
    @PostMapping("/update")
    public Result<?> update(@RequestBody ValidateEnvironmentAddParam param) {
        param.setEventSource(1);
        param.setBusinessType("货车环保标准抽查");
        return Result.OK(safetyValidateEnvironmentHandler.update(param));
    }
}
