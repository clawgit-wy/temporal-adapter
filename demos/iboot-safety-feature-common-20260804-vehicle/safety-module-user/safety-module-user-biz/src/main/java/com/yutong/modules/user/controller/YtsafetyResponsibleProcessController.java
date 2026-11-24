package com.yutong.modules.user.controller;

import com.alibaba.fastjson.JSON;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.dto.BpmProcessParamJsonDTO;
import com.yutong.modules.common.entity.bpm.Operation;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.user.entity.YtsafetyResponsibleProcess;
import com.yutong.modules.user.service.IYtsafetyResponsibleProcessService;
import com.yutong.modules.user.dto.YtsafetyResponsibleProcessDTO;
import com.yutong.modules.user.vo.YtsafetyResponsibleChangeDropVO;
import com.yutong.modules.user.vo.YtsafetyResponsibleChangeVO;
import com.yutong.modules.user.vo.YtsafetyResponsibleProcessVO;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * @Description: 安全责任人流程信息表
 * @Author: lvss
 * @Date:   2026-06-22
 * @Version: V1.0
 */
@Slf4j
@Api(tags="安全责任人流程信息表")
@RestController
@RequestMapping("/process/responsible")
public class YtsafetyResponsibleProcessController  {

	@Autowired
	private IYtsafetyResponsibleProcessService safetyResponsibleProcessService;

	
	/**
	 * 添加
	 *
	 * @param safetyResponsibleProcessDTO
	 * @return
	 */
	@AutoLog(value = "安全责任人流程信息表-添加",operateType=2)
	@ApiOperation(value="安全责任人流程信息表-添加", notes="安全责任人流程信息表-添加")
	@PostMapping(value = "/add")
	public Result<YtsafetyResponsibleProcess> add(@RequestBody YtsafetyResponsibleProcessDTO safetyResponsibleProcessDTO) {
		return Result.OK(safetyResponsibleProcessService.save(safetyResponsibleProcessDTO));
	}

    /**
     * 添加
     *
     * @param safetyResponsibleProcessDTO
     * @return
     */
    @AutoLog(value = "安全责任人流程信息表-编辑",operateType=3)
    @ApiOperation(value="安全责任人流程信息表-编辑", notes="安全责任人流程信息表-编辑")
    @PostMapping(value = "/edit")
    public Result<YtsafetyResponsibleProcess> edit(@RequestBody YtsafetyResponsibleProcessDTO safetyResponsibleProcessDTO) {
        return Result.OK(safetyResponsibleProcessService.edit(safetyResponsibleProcessDTO));
    }

	/**
	 * 查询变更安全责任人
	 *
	 * @param userInfo
	 * @return
	 */
	@ApiOperation(value="安全责任人流程信息表-查询变更安全责任人", notes="安全责任人流程信息表-查询变更安全责任人")
	@GetMapping(value = "/queryChangeUser")
	public Result<List<YtsafetyResponsibleChangeVO>> queryChangeUser(@RequestParam(name="userInfo",required=true) String userInfo) {
		return Result.OK(safetyResponsibleProcessService.queryChangeUser(userInfo));
	}

    /**
     * 查询变更人权限
     *
     * @param userId
     * @return
     */
    @ApiOperation(value="安全责任人流程信息表-查询变更人权限", notes="安全责任人流程信息表-查询变更人权限")
    @GetMapping(value = "/queryChangeAuth")
    public Result<YtsafetyResponsibleChangeDropVO> queryChangeAuth(@RequestParam(name="userId",required=true) Long userId) {
        return Result.OK(safetyResponsibleProcessService.queryChangeAuth(userId));
    }

     /**
      * 通过id查询
      *
      * @param id
      * @return
      */
     @ApiOperation(value="安全责任人流程信息表-通过id查询", notes="安全责任人流程信息表-通过id查询")
     @GetMapping(value = "/queryById")
     public Result<YtsafetyResponsibleProcessVO> queryById(@RequestParam(name="id",required=true) Long id) {
         return Result.OK(safetyResponsibleProcessService.queryResponsibleProcess(id));
     }

    /**
     * "安全责任人流程信息表-流程操作
     *
     * @param processParamDTO
     * @return
     */
    @AutoLog(value = "安全责任人流程信息表-流程操作",operateType=3)
    @ApiOperation(value="安全责任人流程信息表-流程操作", notes="安全责任人流程信息表-流程操作")
    @PostMapping(value = "/processApprove")
    public Result<BpmReponseParamsVO> processApprove(@RequestBody BpmProcessParamDTO processParamDTO) {
        return Result.OK(safetyResponsibleProcessService.executeProcess(processParamDTO));
    }

    @AutoLog(value = "安全责任人流程信息表-流程操作", operateType=3)
    @ApiOperation(value="安全责任人流程信息表-流程操作")
    @PostMapping(value = "/processApproveApp")
    public Result<BpmReponseParamsVO> processApproveApp(BpmProcessParamJsonDTO processParamJsonDTO) {

        log.info("=======processApproveApp=========：{}", processParamJsonDTO);
        BpmProcessParamDTO processParamDTO = new BpmProcessParamDTO();
        BeanUtils.copyProperties(processParamJsonDTO, processParamDTO);
        String param = processParamJsonDTO.getParamApp();
        log.info("=======param=========：{}", processParamJsonDTO);
        if (StringUtils.isNotBlank(param)) {
            log.info("=======JSONparam=========：{}", JSON.parseObject(param, Operation.class));
            processParamDTO.setParam(JSON.parseObject(param, Operation.class));
        }
        log.info("=======processParamDTO=========：{}", processParamJsonDTO);
        return Result.OK(safetyResponsibleProcessService.executeProcess(processParamDTO));
    }

    /**
     * "安全责任人流程信息表-流程操作
     *
     * @return
     */
    @ApiOperation(value="安全责任人流程信息表-获取默认上级审批人员", notes="安全责任人流程信息表-获取默认上级审批人员")
    @GetMapping(value = "/querySuperApprove")
    public Result<List<YtsafetyResponsibleChangeVO>> querySuperApprove() {
        return Result.OK(safetyResponsibleProcessService.querySuperApprove());
    }

}
