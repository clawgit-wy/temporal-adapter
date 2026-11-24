package com.yutong.safety.web.controller.web.fireInspection;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionWorkStepAddParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionWorkStepQueryParams;
import com.yutong.safety.core.handler.fireInspection.SafetyFireInspectionWorkStepHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "消防巡检-风险点台账-作业环节")
@RestController
@RequestMapping("/web/fireInspection/safety/workStep")
public class SafetyFireInspectionWorkStepController {

    @Resource
    private SafetyFireInspectionWorkStepHandler safetyFireInspectionWorkStepHandler;

    /**
     * 分页数据列表查询接口
     *
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value = "/pageList", method = {RequestMethod.GET, RequestMethod.POST})
    public Object pageList(SafetyFireInspectionWorkStepQueryParams param, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetyFireInspectionWorkStepHandler.selectPageList(param, token);
        } catch (Exception e) {
            LogUtils.error("{\"code\":\"9999\",\"msg\":\"分页查询异常，{}\"}", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "分页查询异常" + e.getMessage());
        }
    }

    /**
     * 数据删除操作
     *
     * @param id 数据ID
     */
    @ApiOperation(value = "删除接口")
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public Object delete(Long id, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionWorkStepHandler.delete(id, token);
    }

    /**
     * 数据删除操作
     *
     * @param ids 数据ID集合
     */
    @ApiOperation(value = "批量删除接口")
    @RequestMapping(value = "/batchDelete", method = {RequestMethod.GET, RequestMethod.POST})
    public Object batchDelete(String ids, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionWorkStepHandler.batchDelete(ids, token);
    }

    /**
     * 数据更新操作
     */
    @ApiOperation(value = "更新接口")
    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
    public Object update(SafetyFireInspectionWorkStepAddParams param, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionWorkStepHandler.update(param, token);
    }

    /**
     * 数据添加操作
     */
    @ApiOperation(value = "添加接口")
    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    public Object add(SafetyFireInspectionWorkStepAddParams param, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionWorkStepHandler.add(param, token);
    }

    /**
     * 模版下载
     *
     * @param response
     */
    @ApiOperation(value = "模板下载")
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        safetyFireInspectionWorkStepHandler.download(response);
    }

    /**
     * 数据导入操作
     *
     * @param param 文件信息
     * @return
     */
    @ApiOperation(value = "数据导入接口")
    @RequestMapping("/import")
    public Object importData(CommonImportParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionWorkStepHandler.importData(param, token);
    }

    /**
     * 数据导出操作
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping(value = "/export", method = {RequestMethod.POST, RequestMethod.GET})
    public Object export(SafetyFireInspectionWorkStepQueryParams param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            safetyFireInspectionWorkStepHandler.export(response, param, token);
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("导出消防巡检-风险点台账-作业环节列表异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出消防巡检-风险点台账-作业环节列表异常");
        }
        return null;
    }
}
