package com.yutong.modules.vehicle.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yutong.modules.vehicle.api.ScanInOutService;
import com.yutong.modules.vehicle.dto.ManualReleaseDTO;
import com.yutong.modules.vehicle.dto.ScanRegisterDTO;
import com.yutong.modules.vehicle.dto.YtsafetyReleaseInfoDTO;
import com.yutong.modules.vehicle.mapper.YtsafetyReleaseInfoMapper;
import com.yutong.modules.vehicle.vo.YtsafetyReleaseInfoExporVO;
import com.yutong.modules.vehicle.vo.YtsafetyReleaseInfoVO;
import org.jeecg.common.api.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags="扫码进出厂")
@RestController
@RequestMapping("/sys/scanInOut")
public class ScanInOutController {

    @Resource
    YtsafetyReleaseInfoMapper ytsafetyReleaseInfoMapper;
    @Resource
    ScanInOutService scanInOutService;

    @AutoLog(value = "扫码登记", operateType = 1)
    @ApiOperation(value = "扫码登记", notes = "扫码登记")
    @PostMapping(value = "/scanRegister")
    public Result<ScanRegisterDTO> ScanRegister(ScanRegisterDTO scanRegisterDTO) {
        // todo 调用门禁系统接口
        return Result.OK("提交成功");
    }

    @AutoLog(value = "查询车辆是否有权限通行", operateType = 1)
    @ApiOperation(value = "查询车辆是否有权限通行", notes = "查询车辆是否有权限通行")
    @GetMapping(value = "/getPermission")
    public Result<Boolean> getPermission(@RequestParam("plateNumber") String plateNumber) {
        Map map = ytsafetyReleaseInfoMapper.vehicleInformation(plateNumber);
        return Result.OK("查询成功",map != null && !map.isEmpty());
    }

    @AutoLog(value = "车辆手动放行", operateType = 2)
    @ApiOperation(value = "车辆手动放行", notes = "车辆手动放行")
    @PostMapping(value = "/manualRelease")
    public Result<YtsafetyReleaseInfoVO> manualRelease(ManualReleaseDTO manualReleaseDTO) {
        scanInOutService.manualRelease(manualReleaseDTO);
        // todo 调用门禁系统接口
        return Result.OK("提交成功");
    }

    @AutoLog(value = "车辆放行台账", operateType = 1)
    @ApiOperation(value = "车辆放行台账", notes = "车辆放行台账")
    @PostMapping(value = "/releaseList")
    public Result<IPage<YtsafetyReleaseInfoVO>> releaseList(@RequestBody YtsafetyReleaseInfoDTO releaseInfoDTO) {
        return Result.OK(scanInOutService.queryPageList(releaseInfoDTO));
    }

    @AutoLog(value = "车辆放行台账-导出", operateType = 5)
    @ApiOperation(value = "车辆放行台账-导出", notes = "车辆放行台账-导出")
    @PostMapping(value = "/releaseExport")
    public ModelAndView releaseExport(@RequestBody YtsafetyReleaseInfoDTO scanRegisterDTO) {
        List<YtsafetyReleaseInfoExporVO> list = scanInOutService.exportList(scanRegisterDTO);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "车辆放行台账");
        mv.addObject(NormalExcelConstants.CLASS, YtsafetyReleaseInfoExporVO.class);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("车辆放行台账", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "车辆放行台账");
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }
}
