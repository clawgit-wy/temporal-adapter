package com.yutong.modules.vehicle.controller;

import com.yutong.modules.common.utils.YTLogUtils;
import com.yutong.modules.common.vo.VO;
import com.yutong.modules.vehicle.api.ISafetyEmployeePrivateCarService;
import com.yutong.modules.vehicle.vo.SafetyEmployeePrivateCarVO;
import com.yutong.modules.vehicle.vo.VerifyCarBrandVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.BizException;
import org.jeecg.common.util.ResponseEnum;
import com.yutong.modules.vehicle.employeeprivatecar.param.EmployeeCarAddParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author
 * @description SafetyEmployeePrivateCar
 */
@Api(tags = "员工车辆信息App")
@RestController
@RequestMapping("/app/safety/employeeprivatecar")
public class SafetyEmployeePrivateCarAppController {

    @Autowired
    private ISafetyEmployeePrivateCarService safetyEmployeePrivateCarService;

    @ApiOperation(value = "查询单个")
    @RequestMapping(value = "/getOneCarApp", method = {RequestMethod.GET})
    public Result<SafetyEmployeePrivateCarVO> getOne(@RequestParam(value = "carNo") @ApiParam(value = "车辆编号") String carNo) {
        SafetyEmployeePrivateCarVO one = safetyEmployeePrivateCarService.queryOne(carNo);
        return Result.OK(one);
    }

    @AutoLog(value = "更新私家车信息-EIP流程使用", operateType = 3)
    @ApiOperation(value = "更新私家车信息-EIP流程使用")
    @RequestMapping(value = "/modifyPrivateCarForEip", method = {RequestMethod.GET, RequestMethod.POST})
    public Object modifyPrivateCarForEip(@RequestBody EmployeeCarAddParam param){
        System.out.println("更新私家车信息-EIP流程使用ABC:"+ param);
        try {
            safetyEmployeePrivateCarService.modifyPrivateCar(param);
        }catch (Exception e){
            YTLogUtils.error("更新私家车信息失败",e);
            throw new BizException(ResponseEnum.FAIL.getCode(),"更新失败");
        }
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName(),null);
    }


    @ApiOperation(value = "校验车辆品牌库")
    @RequestMapping(value = "/verifyCarBrandApp", method = {RequestMethod.GET})
    public Result<VerifyCarBrandVO> verifyCarBrand(@RequestParam(value = "carBrandName") @ApiParam(value = "车辆品牌名称") String carBrandName) {
        VerifyCarBrandVO verifyCarBrand =safetyEmployeePrivateCarService.verifyCarBrand(carBrandName);

        return Result.OK(verifyCarBrand);
    }
}
