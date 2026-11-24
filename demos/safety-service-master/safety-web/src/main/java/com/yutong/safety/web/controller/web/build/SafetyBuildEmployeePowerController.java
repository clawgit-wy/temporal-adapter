package com.yutong.safety.web.controller.web.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.build.param.BuildDeviceAddParam;
import com.yutong.safety.core.db.entity.build.param.BuildDeviceListParam;
import com.yutong.safety.core.db.entity.build.param.BuildEmployeePowerListParam;
import com.yutong.safety.core.db.entity.build.param.BuildProcessListParam;
import com.yutong.safety.core.handler.build.SafetyBuildEmployeePowerHandler;
import com.yutong.safety.core.handler.spot.SafetySpotCheckTaskHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

@Api(tags = "楼宇门禁-人员门禁权限明细")
@RestController
@RequestMapping("/web/build/safety/employeePower")
public class SafetyBuildEmployeePowerController {

    @Resource
    private SafetyBuildEmployeePowerHandler safetyBuildEmployeePowerHandler;

    @Resource
    private SafetySpotCheckTaskHandler safetySpotCheckTaskHandler;


    /**
     * 黑名单信息管理数据列表查询方法
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value="/list", method = {RequestMethod.GET,RequestMethod.POST})
    public Object list(BuildEmployeePowerListParam param,
                       @RequestHeader("X-Access-Token") String token) {
        return safetyBuildEmployeePowerHandler.pageList(param,token);
    }

    @ApiOperation("导出")
    @RequestMapping(value="/exportList",method = {RequestMethod.POST,RequestMethod.GET})
    public Object exportList(BuildEmployeePowerListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token){
        try {
            String fileName = new String(("楼宇门禁-人员权限明细"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyBuildEmployeePowerHandler.export(param, token);
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
            LogUtils.error("导出楼宇门禁-人员权限明细列表异常", e);
        }
        return null;
    }


    /**
     * 黑名单信息管理数据列表查询方法
     * @param employeeCode 员工号
     * @return 列表清单
     */
    @ApiOperation(value = "获取员工门禁权限清单")
    @RequestMapping(value="/getBuildByEmployee", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getBuildByEmployee(String employeeCode,
                             @RequestHeader("X-Access-Token") String token) {
        if(StringUtils.isBlank(employeeCode)){
            return null;
        }
        try{
            String newWOorkNo = safetySpotCheckTaskHandler.employeeConvertWorkNo(employeeCode);
            BuildEmployeePowerListParam listParam = new BuildEmployeePowerListParam();
            listParam.setFilterFlag(1);
            listParam.setEmployeeCode(newWOorkNo);
            return safetyBuildEmployeePowerHandler.selectList(listParam,token);
        }catch (Exception e){
            LogUtils.error("查询失败",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"查询失败"+e.getMessage());
        }
    }

    /**
     * 黑名单信息管理数据列表查询方法
     * @param employeeCode 员工号
     * @return 列表清单
     */
    @ApiOperation(value = "获取员工门禁权限清单-定制")
    @RequestMapping(value="/getBuildByEmployeeForDiy", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getBuildByEmployeeForDiy(String employeeCode,
                                     @RequestHeader("X-Access-Token") String token) {
        if(StringUtils.isBlank(employeeCode)){
            return null;
        }
        try{
            String newWOorkNo = safetySpotCheckTaskHandler.employeeConvertWorkNo(employeeCode);
            return safetyBuildEmployeePowerHandler.getBuildByEmployeeForDiy(newWOorkNo);
        }catch (BizException e){
            throw e;
        }catch (Exception e){
            LogUtils.error("查询失败",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"查询失败"+e.getMessage());
        }
    }
}
