package com.yutong.modules.vehicle.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yutong.modules.vehicle.api.IYtsafetyVehicleViolationsReportService;
import com.yutong.modules.vehicle.api.YtsafetyQueryInfoService;
import com.yutong.modules.vehicle.dto.AccessRecordDTO;
import com.yutong.modules.vehicle.dto.YtsafetyQueryPermInfoDTO;
import com.yutong.modules.vehicle.entity.YtsafetyQueryPermInfo;
import com.yutong.modules.vehicle.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Api(tags = "信息查询")
@RestController
@RequestMapping("/sys/queryInfo")
public class YtsafetyQueryInfoController {

    @Autowired
    private YtsafetyQueryInfoService ytsafetyQueryPermInfoService;

    @Autowired
    IYtsafetyVehicleViolationsReportService iYtsafetyVehicleViolationsReportService;

    @AutoLog(value = "人车信息查询", operateType = 2)
    @ApiOperation(value = "人车信息查询", notes = "人车信息查询")
    @GetMapping(value = "/personAndCarList")
    public Result<PersonAndCarVO> personAndCarList(@RequestParam("KeyWord") String KeyWord) {
        return Result.OK(ytsafetyQueryPermInfoService.getPersonAndCarList(KeyWord));
    }

    @AutoLog(value = "人车详细信息", operateType = 2)
    @ApiOperation(value = "人车详细信息", notes = "人车详细信息")
    @GetMapping(value = "/personAndCarDetails")
    public Result<PersonAndCarVO> personAndCarDetails(@RequestParam("plateNumber") String plateNumber) {

        return Result.OK(new PersonAndCarVO());
    }

    @AutoLog(value = "人员进出记录查询", operateType = 2)
    @ApiOperation(value = "人员进出记录查询", notes = "人员进出记录查询")
    @GetMapping(value = "/personnelAccessList")
    public Result<List<PersonnelAccessVO>> personnelAccessList(AccessRecordDTO accessRecordDTO) {
        return Result.OK(ytsafetyQueryPermInfoService.gtePersonnelInOutHistory(accessRecordDTO));
    }

    @AutoLog(value = "车辆进出记录查询", operateType = 2)
    @ApiOperation(value = "车辆进出记录查询", notes = "车辆进出记录查询")
    @GetMapping(value = "/carAccessList")
    public Result<List<CarAccessVO>> carAccessList(AccessRecordDTO accessRecordDTO) {
        return Result.OK(ytsafetyQueryPermInfoService.gteCarAccessList(accessRecordDTO));
    }

    @AutoLog(value = "车辆违规记录查询", operateType = 2)
    @ApiOperation(value = "车辆违规记录查询", notes = "车辆违规记录查询")
    @GetMapping(value = "/carViolationList")
    public Result<List<YtsafetyVehicleViolationsReportVo>> carViolationList(@RequestParam("plateNumber") String plateNumber) {
        return Result.OK(iYtsafetyVehicleViolationsReportService.getVehicleViolationsByVehicleNo(plateNumber));
    }

    @AutoLog(value = "信息查询权限配置列表", operateType = 2)
    @ApiOperation(value = "信息查询权限配置列表", notes = "按角色分组返回权限配置列表")
    @GetMapping(value = "/queryPermInfo")
    public Result<List<YtsafetyQueryPermGroupVO>> queryPermInfo(@RequestParam(value = "roleCode", required = false) String roleCode) {
        return Result.OK(ytsafetyQueryPermInfoService.listGroupByRole(roleCode));
    }

    @AutoLog(value = "信息查询权限-保存配置", operateType = 2)
    @ApiOperation(value = "信息查询权限-保存配置", notes = "保存角色查询权限配置，先删后增")
    @PostMapping(value = "/saveOrUpdate")
    public Result<?> saveOrUpdate(@RequestBody YtsafetyQueryPermInfoDTO dto) {
        ytsafetyQueryPermInfoService.saveOrUpdatePerm(dto);
        return Result.OK("保存成功");
    }

    @AutoLog(value = "信息查询权限-删除配置", operateType = 2)
    @ApiOperation(value = "信息查询权限-删除配置", notes = "信息查询权限，删除配置")
    @GetMapping(value = "/delete")
    public Result<?> delete(@RequestParam("roleCode") String roleCode) {
        LambdaQueryWrapper<YtsafetyQueryPermInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(roleCode),YtsafetyQueryPermInfo::getRoleCode, roleCode);
        return Result.OK(ytsafetyQueryPermInfoService.remove(queryWrapper));
    }
}
