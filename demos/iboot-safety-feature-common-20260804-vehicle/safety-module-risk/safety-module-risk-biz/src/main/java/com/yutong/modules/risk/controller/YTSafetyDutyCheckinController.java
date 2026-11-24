package com.yutong.modules.risk.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.constant.enums.ProcessStatusEnums;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.vo.BmpProcessDetailsVO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.service.IYTSafetyDutyCheckinService;
import com.yutong.modules.risk.vo.DutyCheckinDetailVO;
import com.yutong.modules.risk.vo.DutyCheckinExportVO;
import com.yutong.modules.risk.vo.DutyCheckinPageVO;
import com.yutong.modules.risk.web.DutyCheckinListParam;
import com.yutong.modules.user.service.IYtsafetyResponsibleProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @Description: 在岗签到管理
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
@Api(tags = "在岗签到管理")
@RestController
@RequestMapping("/ytsafety/duty/checkin")
public class YTSafetyDutyCheckinController {

    @Autowired
    private IYTSafetyDutyCheckinService dutyCheckinService;

    @Autowired
    private IYtsafetySummaryProcessService safetySummaryProcessService;

    @Autowired
    IYtsafetyResponsibleProcessService safetyResponsibleProcessService;
    /**
     * 分页查询
     * @param param 查询参数
     * @return 分页结果
     */
    @RequiresPermissions("duty:checkin:list")
    @AutoLog(value = "在岗签到-分页数据列表查询", operateType = 1)
    @ApiOperation(value = "在岗签到-分页数据列表查询接口")
    @PostMapping("/listPage")
    public Result<IPage<DutyCheckinPageVO>> listPage(@RequestBody DutyCheckinListParam param) {
        Page<DutyCheckinPageVO> page = new Page<>(param.getPageNo(), param.getPageSize());
        IPage<DutyCheckinPageVO> pageList = dutyCheckinService.pageList(page, param);
        return Result.OK(pageList);
    }

    /**
     * 根据 ID 查询(查询补岗申请详情-审批时调用查询详情)
     * @param id 主键
     * @return 详情 VO（含流程信息）
     */
    @AutoLog(value = "在岗签到-根据 ID 查询", operateType = 1)
    @ApiOperation(value = "在岗签到-根据 ID 查询接口")
    @GetMapping("/queryById")
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
     * @param processParamDTO 审批参数
     * @return 更新结果
     */
    @AutoLog(value = "在岗签到-审批", operateType = 3)
    @ApiOperation(value = "在岗签到-审批接口")
    @PostMapping("/approve")
    public Result approve(@RequestBody BpmProcessParamDTO processParamDTO) {
        LoginUser loginUser = BusUtits.getLoginUser();
        String approveUser = loginUser != null ? loginUser.getEmpNo() : null;
        BpmReponseParamsVO bpmReponseParamsVO = safetyResponsibleProcessService.executeProcess(processParamDTO);
        if(ProcessStatusEnums.finish.getCode() == bpmReponseParamsVO.getFlowStatus()){
            dutyCheckinService.approve(processParamDTO, approveUser);
        }
        return Result.OK();
    }

    /**
     * 导出
     * @param param 查询参数
     * @return 导出结果
     */
    @RequiresPermissions("duty:checkin:export")
    @AutoLog(value = "在岗签到-数据导出", operateType = 1)
    @ApiOperation(value = "在岗签到-数据导出接口")
    @GetMapping("/export")
    public ModelAndView export(DutyCheckinListParam param) {
        List<DutyCheckinExportVO> exportList = dutyCheckinService.selectListExport(param);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String sheetName = "在岗签到";
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, sheetName);
        mv.addObject(NormalExcelConstants.CLASS, DutyCheckinExportVO.class);
        ExportParams exportParams = new ExportParams(sheetName, "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), sheetName);
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }
}
