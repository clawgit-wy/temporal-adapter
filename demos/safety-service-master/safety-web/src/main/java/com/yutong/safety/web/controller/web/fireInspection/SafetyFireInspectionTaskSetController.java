package com.yutong.safety.web.controller.web.fireInspection;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskSetAddParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskSetQueryParams;
import com.yutong.safety.core.handler.fireInspection.SafetyFireInspectionTaskSetHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "消防巡检-任务设置")
@RestController
@RequestMapping("/web/fireInspection/safety/taskSet")
public class SafetyFireInspectionTaskSetController {

    @Resource
    private SafetyFireInspectionTaskSetHandler safetyFireInspectionTaskSetHandler;

    /**
     * 分页数据列表查询接口
     *
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value = "/pageList", method = {RequestMethod.GET, RequestMethod.POST})
    public Object pageList(SafetyFireInspectionTaskSetQueryParams param, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetyFireInspectionTaskSetHandler.selectPageList(param, token);
        } catch (Exception e) {
            LogUtils.error("{\"code\":\"9999\",\"msg\":\"分页查询异常，{}\"}", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "分页查询异常");
        }
    }

    /**
     * 分页数据列表查询接口
     *
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "任务设置检查细分维度-详情页接口")
    @RequestMapping(value = "/pageSubDimensionList", method = {RequestMethod.GET, RequestMethod.POST})
    public Object pageSubDimensionList(SafetyFireInspectionTaskSetQueryParams param, @RequestHeader("X-Access-Token") String token) {
            return safetyFireInspectionTaskSetHandler.selectSubDimensionList(param, token);
    }

    @ApiOperation(value = "状态修改")
    @RequestMapping(value = "/updateStatus", method = {RequestMethod.GET, RequestMethod.POST})
    public Object pageSubDimensionList(Long id, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionTaskSetHandler.updateStatus(id, token);
    }

    /**
     * 数据删除操作
     *
     * @param id 数据ID
     */
    @ApiOperation(value = "删除接口")
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public Object delete(Long id, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionTaskSetHandler.delete(id, token);
    }

    /**
     * 数据删除操作
     *
     * @param ids 数据ID集合
     */
    @ApiOperation(value = "批量删除接口")
    @RequestMapping(value = "/batchDelete", method = {RequestMethod.GET, RequestMethod.POST})
    public Object batchDelete(String ids, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionTaskSetHandler.batchDelete(ids, token);
    }

    /**
     * 数据更新操作
     */
    @ApiOperation(value = "更新接口")
    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
    public Object update(SafetyFireInspectionTaskSetAddParams param, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionTaskSetHandler.update(param, token);
    }

    /**
     * 数据添加操作
     */
    @ApiOperation(value = "添加接口")
    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    public Object add(SafetyFireInspectionTaskSetAddParams param, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionTaskSetHandler.add(param, token);
    }

    @ApiOperation(value = "模板下载")
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        safetyFireInspectionTaskSetHandler.download(response);
    }

    /**
     * 数据导入操作
     *
     * @param param 文件信息
     */
    @ApiOperation(value = "数据导入接口")
    @RequestMapping("/import")
    public Object importData(CommonImportParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionTaskSetHandler.importData(param, token);
    }
}
