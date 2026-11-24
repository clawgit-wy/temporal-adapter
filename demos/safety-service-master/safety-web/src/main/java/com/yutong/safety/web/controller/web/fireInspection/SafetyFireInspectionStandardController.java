package com.yutong.safety.web.controller.web.fireInspection;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionStandardAddParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionStandardQueryParams;
import com.yutong.safety.core.handler.fireInspection.SafetyFireInspectionStandardHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "消防巡检-风险点台账-检查标准")
@RestController
@RequestMapping("/web/fireInspection/safety/standard")
public class SafetyFireInspectionStandardController {

    @Resource
    private SafetyFireInspectionStandardHandler safetyFireInspectionStandardHandler;

    /**
     * 分页数据列表查询接口
     *
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value = "/pageList", method = {RequestMethod.GET, RequestMethod.POST})
    public Object pageList(SafetyFireInspectionStandardQueryParams param, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionStandardHandler.selectPageList(param, token);
    }

    /**
     * 数据删除操作
     *
     * @param id 数据ID
     */
    @ApiOperation(value = "删除接口")
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public Object delete(Long id, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionStandardHandler.delete(id, token);
    }

    /**
     * 数据删除操作
     *
     * @param ids 数据ID集合
     */
    @ApiOperation(value = "批量删除接口")
    @RequestMapping(value = "/batchDelete", method = {RequestMethod.GET, RequestMethod.POST})
    public Object batchDelete(String ids, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionStandardHandler.batchDelete(ids, token);
    }

    /**
     * 数据更新操作
     */
    @ApiOperation(value = "更新接口")
    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
    public Object update(SafetyFireInspectionStandardAddParams param, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionStandardHandler.update(param, token);
    }

    /**
     * 数据添加操作
     */
    @ApiOperation(value = "添加接口")
    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    public Object add(SafetyFireInspectionStandardAddParams param, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionStandardHandler.add(param, token);
    }

    /**
     * 模版下载
     */
    @ApiOperation(value = "模板下载")
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        safetyFireInspectionStandardHandler.download(response);
    }

    /**
     * 数据导入操作
     *
     * @param param 文件信息
     */
    @ApiOperation(value = "数据导入接口")
    @RequestMapping("/import")
    public Object importData(CommonImportParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyFireInspectionStandardHandler.importData(param, token);
    }

    /**
     * 数据导出操作
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping(value = "/export", method = {RequestMethod.GET, RequestMethod.POST})
    public Object export(SafetyFireInspectionStandardQueryParams param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            safetyFireInspectionStandardHandler.export(response, param, token);
        } catch (Exception e) {
            LogUtils.error("导出消防巡检-风险点台账-检查标准列表异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出消防巡检-风险点台账-检查标准列表异常");
        }
        return null;
    }
}