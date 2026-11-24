package com.yutong.safety.web.controller.web.park;

import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterIssuanceSh;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeePowerListParam;
import com.yutong.safety.core.handler.park.SafetyParkEmpPowerHistoryHanlder;
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

@Api(tags = "园区权限-员工车辆厂区权限-上一次")
@RestController
@RequestMapping("/web/park/safety/parkEmpPowerHistory")
public class SafetyParkEmpPowerHistoryController {

    @Resource
    private SafetyParkEmpPowerHistoryHanlder safetyParkEmpPowerHistoryHanlder;

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
            return safetyParkEmpPowerHistoryHanlder.selectPageList(param);
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
    public Object viewDetail(String empNo){
        if(StringUtils.isBlank(empNo)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"员工号不能为空！");
        }
        return safetyParkEmpPowerHistoryHanlder.viewDetail(empNo);
    }
}
