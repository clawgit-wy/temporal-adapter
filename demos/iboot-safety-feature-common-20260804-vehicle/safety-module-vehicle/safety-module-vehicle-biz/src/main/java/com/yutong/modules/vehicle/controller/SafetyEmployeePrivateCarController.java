package com.yutong.modules.vehicle.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.vehicle.api.ISafetyEmployeePrivateCarService;
import com.yutong.modules.vehicle.dto.SafetyEmployeePrivateCarPersonnelChangeDTO;
import com.yutong.modules.vehicle.dto.SafetyEmployeePrivateCarQueryDTO;
import com.yutong.modules.vehicle.vo.SafetyEmployeePrivateCarQueryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author caomg
 * @date 2024-10-29 13:21
 * @description SafetyEmployeePrivateCar
 */
@Api(tags = "员工车辆信息")
@RestController
@RequestMapping("/safety/employeeprivatecar")
public class SafetyEmployeePrivateCarController {

    @Autowired
    ISafetyEmployeePrivateCarService safetyEmployeePrivateCarService;

    @ApiOperation(value = "台账列表查询（带分页）")
    @RequestMapping(value = "/listPage", method = {RequestMethod.GET})
    public Result<Page<SafetyEmployeePrivateCarQueryVO>> listPage(SafetyEmployeePrivateCarQueryDTO queryDTO) {
        Page<SafetyEmployeePrivateCarQueryVO> queryVOList = safetyEmployeePrivateCarService.listPage(queryDTO);
        return Result.OK(queryVOList);
    }

    @AutoLog(value = "人员变更", operateType = 3)
    @ApiOperation(value = "人员变更")
    @RequestMapping(value = "/personnelChange", method = {RequestMethod.GET})
    public Result<?> personnelChange(@Valid SafetyEmployeePrivateCarPersonnelChangeDTO queryDTO) {
        safetyEmployeePrivateCarService.personnelChange(queryDTO);
        return Result.OK("变更成功");
    }
}
