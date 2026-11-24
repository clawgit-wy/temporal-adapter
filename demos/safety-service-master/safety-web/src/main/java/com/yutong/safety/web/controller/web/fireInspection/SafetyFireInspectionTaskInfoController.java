package com.yutong.safety.web.controller.web.fireInspection;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskInfoDetailQueryParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskInfoQueryParams;
import com.yutong.safety.core.handler.fireInspection.SafetyFireInspectionTaskInfoDetailHandler;
import com.yutong.safety.core.handler.fireInspection.SafetyFireInspectionTaskInfoHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "消防巡检-任务详情")
@RestController
@RequestMapping("/web/fireInspection/safety/taskInfo")
public class SafetyFireInspectionTaskInfoController {

    @Resource
    private SafetyFireInspectionTaskInfoHandler safetyFireInspectionTaskInfoHandler;

    @Resource
    private SafetyFireInspectionTaskInfoDetailHandler safetyFireInspectionTaskInfoDetailHandler;

    /**
     * 分页数据列表查询接口
     *
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value = "/pageList", method = {RequestMethod.GET, RequestMethod.POST})
    public Object pageList(SafetyFireInspectionTaskInfoQueryParams param) {
        try {
            return safetyFireInspectionTaskInfoHandler.selectPageList(param);
        } catch (Exception e) {
            LogUtils.error("{\"code\":\"9999\",\"msg\":\"分页查询异常，{}\"}", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "分页查询异常" + e.getMessage());
        }
    }

    /**
     * 分页数据列表查询接口
     *
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "详情页分页数据列表查询接口")
    @RequestMapping(value = "/pageDetailList", method = {RequestMethod.GET})
    public Object pageDetailList(SafetyFireInspectionTaskInfoDetailQueryParams param) {
        try {
            return safetyFireInspectionTaskInfoDetailHandler.selectPageList(param);
        } catch (Exception e) {
            LogUtils.error("{\"code\":\"9999\",\"msg\":\"分页查询异常，{}\"}", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "分页查询异常" + e.getMessage());
        }
    }

    /**
     * 数据导出操作
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping(value = "/export", method = {RequestMethod.GET, RequestMethod.POST})
    public Object export(SafetyFireInspectionTaskInfoQueryParams param, HttpServletResponse response) {
        try {
            safetyFireInspectionTaskInfoHandler.export(response, param);
        } catch (Exception e) {
            LogUtils.error("导出消防巡检-任务设置列表异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出消防巡检-任务设置列表异常");
        }
        return null;
    }
}
