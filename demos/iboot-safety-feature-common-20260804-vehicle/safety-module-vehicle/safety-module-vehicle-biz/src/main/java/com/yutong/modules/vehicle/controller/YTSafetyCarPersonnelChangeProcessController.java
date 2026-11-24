package com.yutong.modules.vehicle.controller;


import com.alibaba.fastjson.JSON;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.dto.BpmProcessParamJsonDTO;
import com.yutong.modules.common.entity.bpm.Operation;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.vehicle.api.IYTSafetyCarPersonnelChangeProcessService;
import com.yutong.modules.vehicle.dto.YTSafetyCarPersonnelChangeProcessDTO;
import com.yutong.modules.vehicle.entity.YTSafetyCarPersonnelChangeProcess;
import com.yutong.modules.vehicle.vo.YTSafetyCarPersonnelChangeProcessVO;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.validation.Valid;

/**
 * @Description: 车辆人员变更流程表
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Slf4j
@Api(tags="员工车辆人员变更流程表")
@RestController
@RequestMapping("/safety/car/personnel/ChangeProcess")
public class YTSafetyCarPersonnelChangeProcessController extends JeecgController<YTSafetyCarPersonnelChangeProcess, IYTSafetyCarPersonnelChangeProcessService> {
	@Autowired
	private IYTSafetyCarPersonnelChangeProcessService safetyCarPersonnelChangeProcessService;

	@AutoLog(value = "车辆人员变更流程表-人员调整",operateType=3)
	@ApiOperation(value="车辆人员变更流程表-人员调整", notes="车辆人员变更流程表-人员调整")
	@RequestMapping(value = "/turnoverApp", method = {RequestMethod.POST})
	public Result<?> turnover(@RequestBody @Valid YTSafetyCarPersonnelChangeProcessDTO changeProcessDTO) {
        safetyCarPersonnelChangeProcessService.turnover(changeProcessDTO);
		return Result.OK("编辑成功!");
	}

	@ApiOperation(value="车辆人员变更流程表-通过id查询", notes="车辆人员变更流程表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<YTSafetyCarPersonnelChangeProcessVO> queryById(@RequestParam(name="id",required=true) Long id) {
        YTSafetyCarPersonnelChangeProcessVO carPersonnelChangeProcessVO = safetyCarPersonnelChangeProcessService.queryById(id);
		return Result.OK(carPersonnelChangeProcessVO);
	}

     @AutoLog(value = "车辆人员变更-流程操作",operateType=3)
     @ApiOperation(value="车辆人员变更-流程操作", notes="车辆人员变更-流程操作")
     @PostMapping(value = "/processApprove")
     public Result<?> processApprove(@RequestBody BpmProcessParamDTO processParamDTO) {
         return Result.OK(new BpmReponseParamsVO());
     }

    @AutoLog(value = "隐患上报表-流程操作App",operateType=3)
    @ApiOperation(value="隐患上报表-流程操作App", notes="隐患上报表-流程操作App")
    @PostMapping(value = "/processApproveApp")
    public Result<?> processApproveApp(BpmProcessParamJsonDTO formDTO) {
        BpmProcessParamDTO processParamDTO = new BpmProcessParamDTO();
        BeanUtils.copyProperties(formDTO, processParamDTO);
        String param = formDTO.getParamApp();
        if (StringUtils.isNotBlank(param)) {
            processParamDTO.setParam(JSON.parseObject(param, Operation.class));
        }
        BpmReponseParamsVO bpmReponseParamsVO = safetyCarPersonnelChangeProcessService.executeProcess(processParamDTO);
        return Result.OK(bpmReponseParamsVO);
    }

}
