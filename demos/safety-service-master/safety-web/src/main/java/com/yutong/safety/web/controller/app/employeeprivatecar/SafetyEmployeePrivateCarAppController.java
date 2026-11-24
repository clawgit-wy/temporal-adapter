package com.yutong.safety.web.controller.app.employeeprivatecar;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.db.entity.employeeprivatecar.param.EmployeeCarAddParam;
import com.yutong.safety.core.db.entity.employeeprivatecar.param.EmployeeCarDeleteParam;
import com.yutong.safety.core.handler.SafetyOutsiderPromiseChangeHandler;
import com.yutong.safety.core.handler.employeeprivatecar.SafetyEmployeePrivateCarHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author caomg
 * @date 2024-10-29 13:21
 * @description SafetyEmployeePrivateCar
 */
@Api(tags = "员工入厂私家车信息App")
@RestController
@RequestMapping("/app/safety/employeeprivatecar")
public class SafetyEmployeePrivateCarAppController {

    @Autowired
    SafetyEmployeePrivateCarHandler safetyEmployeePrivateCarHandler;
    @Resource
    private SafetyOutsiderPromiseChangeHandler safetyOutsiderPromiseChangeHandler;

    @ApiOperation(value = "查询单个")
    @RequestMapping(value = "/getOne", method = {RequestMethod.GET, RequestMethod.POST})
    public Object getOne(SafetyEmployeePrivateCarDO param) {
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), safetyEmployeePrivateCarHandler.getOne(param));
    }

    @ApiOperation(value = "更新私家车信息-EIP流程使用")
    @RequestMapping(value = "/modifyPrivateCarForEip", method = {RequestMethod.GET, RequestMethod.POST})
    public Object modifyPrivateCarForEip(@RequestBody EmployeeCarAddParam param){
        try {
            safetyEmployeePrivateCarHandler.modifyPrivateCar(param);
        }catch (Exception e){
            LogUtils.error("更新私家车信息失败",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"更新失败");
        }
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName(),null);
    }

    @ApiOperation(value = "APP-车辆删除接口")
    @RequestMapping(value = "/deleteCar", method = {RequestMethod.POST})
    public Object deleteCar(@RequestBody EmployeeCarDeleteParam param){
        try {
            safetyEmployeePrivateCarHandler.deleteCar(param);
        } catch (BizException e){
            LogUtils.error(e.getMsg());
            throw new BizException(e.getCode(), e.getMsg());
        } catch (Exception e){
            LogUtils.error("车辆删除失败",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"车辆删除失败");
        }
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName());
    }

    @ApiOperation(value = "APP-车辆查询接口")
    @RequestMapping(value = "/selectCar", method = {RequestMethod.GET})
    public Object selectCar(String empNo){
        try {
            return safetyOutsiderPromiseChangeHandler.getByFunc(empNo);
        } catch (Exception e){
            LogUtils.error("车辆查询失败",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"车辆查询失败");
        }
    }
}
