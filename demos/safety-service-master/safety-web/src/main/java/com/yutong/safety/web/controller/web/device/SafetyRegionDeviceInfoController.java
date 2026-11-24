package com.yutong.safety.web.controller.web.device;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.device.param.SafetyRegionDeviceInfoQueryParams;
import com.yutong.safety.core.db.entity.device.param.SafetyRegionDeviceInfoUpdateParams;
import com.yutong.safety.core.handler.device.SafetyRegionDeviceInfoHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "设备管理-楼宇门禁设备-设备分类")
@RestController
@RequestMapping("/web/safety/device/regionDevice")
public class SafetyRegionDeviceInfoController {

    @Resource
    private SafetyRegionDeviceInfoHandler safetyRegionDeviceInfoHandler;

    /**
     * 分页数据列表查询接口
     *
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value = "/pageList", method = {RequestMethod.GET})
    public Object pageList(SafetyRegionDeviceInfoQueryParams param) {
        return safetyRegionDeviceInfoHandler.selectPageList(param);
    }

    /**
     * 更新设备类型
     *
     * @param updateParams 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "设备更新接口")
    @RequestMapping(value = "/updateDeviceType", method = {RequestMethod.POST})
    public Object updateDeviceType(@RequestBody List<SafetyRegionDeviceInfoUpdateParams> updateParams) {
        return safetyRegionDeviceInfoHandler.updateDeviceType(updateParams);
    }

    /**
     * 数据导出操作
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping(value = "/export", method = {RequestMethod.GET})
    public Object export(HttpServletResponse response) {
        try {
            safetyRegionDeviceInfoHandler.export(response);
        } catch (Exception e) {
            LogUtils.error("楼宇门禁-设备分类-导出异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "楼宇门禁-设备分类导出异常");
        }
        return null;
    }

    /**
     * 手动更新
     */
    @ApiOperation(value = "手动更新")
    @RequestMapping(value = "/updateAndSaveToCrk", method = {RequestMethod.POST})
    public Object updateAndSaveToCrk() {
        try {
            safetyRegionDeviceInfoHandler.save();
        } catch (Exception e) {
            LogUtils.error("楼宇门禁-设备分类-手动更新异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "楼宇门禁-设备分类-手动更新异常");
        }
        return null;
    }
}
