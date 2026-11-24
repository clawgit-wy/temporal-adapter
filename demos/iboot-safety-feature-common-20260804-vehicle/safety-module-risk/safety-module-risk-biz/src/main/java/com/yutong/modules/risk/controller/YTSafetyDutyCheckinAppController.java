package com.yutong.modules.risk.controller;

import com.alibaba.fastjson.JSON;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.constant.enums.ProcessStatusEnums;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.dto.BpmProcessParamJsonDTO;
import com.yutong.modules.common.entity.bpm.Operation;
import com.yutong.modules.common.vo.BmpProcessDetailsVO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.service.IYTSafetyDutyCheckinService;
import com.yutong.modules.risk.vo.DutyCheckinDetailVO;
import com.yutong.modules.risk.vo.DutyCheckinMonthlyVO;
import com.yutong.modules.risk.vo.DutyCheckinTodayVO;
import com.yutong.modules.risk.vo.SecurityDutyPersonVO;
import com.yutong.modules.risk.web.DutyCheckinAddParam;
import com.yutong.modules.risk.web.MakeupApplyParam;
import com.yutong.modules.user.service.IYtsafetyResponsibleProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Description: 在岗签到管理
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "在岗签到管理app")
@RestController
@RequestMapping("/app/ytsafety/duty/checkin")
public class YTSafetyDutyCheckinAppController {

    @Autowired
    private IYTSafetyDutyCheckinService dutyCheckinService;

    @Autowired
    private IYtsafetySummaryProcessService safetySummaryProcessService;

    @Autowired
    IYtsafetyResponsibleProcessService safetyResponsibleProcessService;

    /**
     * 签到
     * @param param 签到参数
     * @return 签到记录 ID
     */
    @ApiOperation(value = "在岗签到-签到接口")
    @PostMapping("/checkinApp")
    public Result<Long> checkin(@Valid DutyCheckinAddParam param) {
        Long id = dutyCheckinService.checkin(param);
        return Result.OK(id);
    }

    /**
     * 补岗申请
     * @param param 补岗申请参数
     * @return 更新结果
     */
    @AutoLog(value = "在岗签到-补岗申请", operateType = 3)
    @ApiOperation(value = "在岗签到-补岗申请接口")
    @PostMapping("/makeupApplyApp")
    public Result<Integer> makeupApply(@Valid MakeupApplyParam param) {
        int result = dutyCheckinService.makeupApply(param);
        return Result.OK(result);
    }

    /**
     * 根据 ID 查询(查询补岗申请详情-审批时调用查询详情)
     * @param id 主键
     * @return 详情 VO（含流程信息）
     */
    @AutoLog(value = "在岗签到-根据 ID 查询", operateType = 1)
    @ApiOperation(value = "在岗签到-根据 ID 查询接口")
    @GetMapping("/queryByIdApp")
    public Result<DutyCheckinDetailVO> queryById(@RequestParam("id") Long id) {
        DutyCheckinDetailVO vo = dutyCheckinService.queryDetailById(id);
        if (vo != null) {
            LoginUser user = BusUtits.getLoginUser();
            BmpProcessDetailsVO detailsVO = safetySummaryProcessService.queryProcessModelInfo(id, user.getEipName());
            BeanUtils.copyProperties(detailsVO, vo);
        }
        return Result.OK(vo);
    }

    /**
     * 审批
     * @param processParamJsonDTO 审批参数
     * @return 更新结果
     */
    @AutoLog(value = "在岗签到-审批", operateType = 3)
    @ApiOperation(value = "在岗签到-审批接口")
    @PostMapping("/approveApp")
    public Result approve(@RequestBody BpmProcessParamJsonDTO processParamJsonDTO) {
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
        LoginUser loginUser = BusUtits.getLoginUser();
        String approveUser = loginUser != null ? loginUser.getEmpNo() : null;
        BpmReponseParamsVO bpmReponseParamsVO = safetyResponsibleProcessService.executeProcess(processParamDTO);
        if(ProcessStatusEnums.finish.getCode() == bpmReponseParamsVO.getFlowStatus()){
            dutyCheckinService.approve(processParamDTO, approveUser);
        }
        return Result.OK();
    }

    /**
     * 按月份查询在岗记录（默认当前月份）
     * @param month 月份（yyyy-MM），不传默认当前月份
     * @return 月度汇总：已在岗/未在岗/已补签数量 + 签到记录列表（倒序）
     */
    @AutoLog(value = "在岗签到-按月份查询在岗记录", operateType = 1)
    @ApiOperation(value = "在岗签到-按月份查询在岗记录接口")
    @GetMapping("/monthlyRecordsApp")
    public Result<DutyCheckinMonthlyVO> monthlyRecords(@RequestParam(value = "month", required = false) String month) {
        DutyCheckinMonthlyVO result = dutyCheckinService.queryMonthlyRecords(month);
        return Result.OK(result);
    }

    @AutoLog(value = "在岗签到-查询今日安保带班人员", operateType = 1)
    @ApiOperation(value = "在岗签到-查询今日安保带班人员接口(掌宇以及小程序修改带班人员信息)",notes = "factory传参厂区名称")
    @GetMapping("/securityDutyPersonApp")
    public Result<SecurityDutyPersonVO> securityDutyPerson(@RequestParam("factory") String factory) {
        SecurityDutyPersonVO result = dutyCheckinService.querySecurityDutyPerson(factory);
        return Result.OK(result);
    }

    @AutoLog(value = "在岗签到-查询今日在岗状态", operateType = 1)
    @ApiOperation(value = "在岗签到-查询今日在岗状态、人员类型接口")
    @GetMapping("/todayStatusApp")
    public Result<DutyCheckinTodayVO> todayStatus(@RequestParam("empNo") String empNo) {
        DutyCheckinTodayVO result = dutyCheckinService.queryTodayStatus(empNo);
        return Result.OK(result);
    }
}
