package com.yutong.safety.web.controller.web.park;

import com.alibaba.excel.util.StringUtils;
import com.ctc.wstx.util.StringUtil;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeePowerListParam;
import com.yutong.safety.core.handler.park.SafetyParkEmpPowerReportHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "园区权限-员工车辆厂区权限报表")
@RestController
@RequestMapping("/web/park/safety/parkEmpPowerReport")
public class SafetyParkEmpPowerReportController {

    @Resource
    private SafetyParkEmpPowerReportHandler safetyParkEmpPowerReportHandler;

    /**
     * 分页数据列表查询接口
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value="/pageList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object pageList(ParkEmployeePowerListParam param,
                           @RequestHeader("X-Access-Token") String token) {
        try{
            return safetyParkEmpPowerReportHandler.getPageList(param, token);
        }catch (Exception e){
            LogUtils.error("{\"code\":\"9999\",\"msg\":\"分页查询异常，{}\"}",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"分页查询异常"+e.getMessage());
        }
    }

    /**
     * 查看厂区权限详情
     * @param empNo 员工号
     *        userType 员工类型
     * @return 列表清单
     */
    @ApiOperation(value = "查看厂区权限详情")
    @RequestMapping(value="/viewDetail", method = {RequestMethod.GET,RequestMethod.POST})
    public Object viewDetail(String empNo,String userType){
        if(StringUtils.isBlank(empNo) || StringUtils.isBlank(userType)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"员工号和员工类型都不能为空！");
        }
        return safetyParkEmpPowerReportHandler.viewDetail(empNo,userType);
    }

    /**
     * 注销员工权限 -需判断 人脸，卡，车，以及门禁权限
     * @param empNo 员工号
     *        userType 员工类型
     * @return 列表清单
     */
    @ApiOperation(value = "注销权限")
    @RequestMapping(value="/cancelPower", method = {RequestMethod.GET,RequestMethod.POST})
    public Object cancelPower(String empNo,String userType,
                              @RequestHeader("X-Access-Token") String token){
        if(StringUtils.isBlank(empNo) || StringUtils.isBlank(userType)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"员工号和员工类型都不能为空！");
        }
        return safetyParkEmpPowerReportHandler.cancelPower(empNo,userType,token);
    }

    /**
     * 数据导出操作
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping(value = "/export", method = {RequestMethod.POST, RequestMethod.GET})
    public Object export(ParkEmployeePowerListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            safetyParkEmpPowerReportHandler.export(response, param, token);
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("导出员工及车辆入厂权限列表异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出员工及车辆入厂权限列表异常");
        }
        return null;
    }
}
