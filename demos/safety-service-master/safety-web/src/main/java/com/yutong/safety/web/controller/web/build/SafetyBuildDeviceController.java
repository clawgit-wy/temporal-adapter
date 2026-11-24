package com.yutong.safety.web.controller.web.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.build.param.BuildAnDeviceAddParam;
import com.yutong.safety.core.db.entity.build.param.BuildDeviceAddParam;
import com.yutong.safety.core.db.entity.build.param.BuildDeviceCopyPermissionParams;
import com.yutong.safety.core.db.entity.build.param.BuildDeviceListParam;
import com.yutong.safety.core.handler.build.SafetyBuildDeviceHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

@Api(tags = "楼宇门禁-设备管理")
@RestController
@RequestMapping("/web/build/safety/buildDevice")
public class SafetyBuildDeviceController {

    @Resource
    private SafetyBuildDeviceHandler safetyBuildDeviceHandler;

    /**
     * 黑名单信息管理数据列表查询方法
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value="/list", method = {RequestMethod.GET,RequestMethod.POST})
    public Object list(BuildDeviceListParam param,
                       @RequestHeader("X-Access-Token") String token) {
        return safetyBuildDeviceHandler.pageList(param,token);
    }

    /**
     * 重新下发
     * @param param 请求参数
     * @return 更新状态
     */
    @ApiOperation(value = "更新")
    @RequestMapping(value = "/update", method = {RequestMethod.GET,RequestMethod.POST})
    public Object update(BuildDeviceAddParam param, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetyBuildDeviceHandler.update(param,token);
        }catch (Exception e){
            LogUtils.error("更新异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), ResponseEnum.FAIL.getName());
        }
    }

    /**
     * 重新下发
     * @param param 请求参数
     * @return 更新状态
     */
    @ApiOperation(value = "更新安保相关配置")
    @RequestMapping(value = "/updateForAn", method = {RequestMethod.GET,RequestMethod.POST})
    public Object updateForAn(BuildAnDeviceAddParam param, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetyBuildDeviceHandler.updateForAn(param,token);
        }catch (Exception e){
            LogUtils.error("更新异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), ResponseEnum.FAIL.getName());
        }
    }

    /**
     * 黑名单信息管理数据列表查询方法
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value="/listForAn", method = {RequestMethod.GET,RequestMethod.POST})
    public Object listForAn(BuildDeviceListParam param,
                       @RequestHeader("X-Access-Token") String token) {
        return safetyBuildDeviceHandler.pageListForAn(param,token);
    }

    /**
     * 黑名单信息管理数据列表查询方法
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "导出接口")
    @RequestMapping(value="/exportForAn", method = {RequestMethod.GET,RequestMethod.POST})
    public Object exportForAn(BuildDeviceListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token){
        try {
            String fileName = new String(("楼宇门禁设备列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyBuildDeviceHandler.exportForAn(param, token);
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("导出楼宇门禁设备列表异常", e);
        }
        return null;
    }
    /**
     * 数据展示操作
     * @param id 主键ID
     * @return 返回对象
     */
    @ApiOperation(value = "详情接口")
    @RequestMapping(value = "/view", method = {RequestMethod.GET,RequestMethod.POST})
    public Object view(String id) {
        return safetyBuildDeviceHandler.view(id);
    }

    /**
     * 数据展示操作
     * @param deviceCode 门禁编码
     * @return 返回对象
     */
    @ApiOperation(value = "根据门禁编码获取详情")
    @RequestMapping(value = "/getByDeviceCode", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getByDeviceCode(String deviceCode) {
        return safetyBuildDeviceHandler.getByDeviceCode(deviceCode);
    }

    /**
     * 数据展示操作
     * @param listParam 检索条件
     * @return 返回对象
     */
    @ApiOperation(value = "根据检索条件获取门禁清单")
    @RequestMapping(value = "/getListByCondition", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getListByCondition(BuildDeviceListParam listParam) {
        return safetyBuildDeviceHandler.selectList(listParam);
    }


    @ApiOperation(value = "克隆设备信息下拉框接口")
    @RequestMapping(value = "/getCopyDeviceInfo", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getCopyDeviceInfo(String deviceNameOrCode) {
        return safetyBuildDeviceHandler.getCopyDeviceInfo(deviceNameOrCode);
    }

    @ApiOperation(value = "克隆权限提交接口")
    @RequestMapping(value = "/submitCopyData", method = {RequestMethod.GET,RequestMethod.POST})
    public Object submitCopyData(BuildDeviceCopyPermissionParams param, @RequestHeader("X-Access-Token") String token) {
        return safetyBuildDeviceHandler.submitCopyData(param, token);
    }
}
