package com.yutong.modules.risk.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.alibaba.fastjson.JSON;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.dto.BpmProcessParamJsonDTO;
import com.yutong.modules.common.entity.bpm.Operation;
import com.yutong.modules.risk.api.IYTSafetyHazardReportService;
import com.yutong.modules.risk.dto.*;
import com.yutong.modules.risk.entity.YTSafetyHazardReport;
import com.yutong.modules.risk.vo.YTSafetyHazardLedgerVO;
import com.yutong.modules.risk.vo.YTSafetyHazardReportHistoryVO;
import com.yutong.modules.risk.vo.YTSafetyHazardReportListVO;
import com.yutong.modules.risk.vo.YTSafetyHazardReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 隐患上报表
 * @Author: jeecg-boot
 * @Date:   2026-06-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags="隐患上报表")
@RestController
@RequestMapping("/ytsafety/hazard/report")
public class YTSafetyHazardReportController extends JeecgController<YTSafetyHazardReport, IYTSafetyHazardReportService> {
	@Autowired
	private IYTSafetyHazardReportService yTSafetyHazardReportService;

     @AutoLog(value = "隐患上报表-提交上报",operateType = 2)
     @ApiOperation(value="隐患上报表-提交上报", notes="隐患上报表-提交上报")
     @PostMapping(value = "/submitReport")
     public Result<?> submitReport(@RequestBody @Valid YTSafetyHazardReportDTO ytSafetyHazardReportDTO) {
         YTSafetyHazardReport ytSafetyHazardReport = yTSafetyHazardReportService.submitReport(ytSafetyHazardReportDTO);
         return Result.OK(ytSafetyHazardReport);
     }

    @AutoLog(value = "隐患上报表-提交上报App",operateType = 2)
    @ApiOperation(value="隐患上报表-提交上报App", notes="隐患上报表-提交上报App")
    @PostMapping(value = "/submitReportApp")
    public Result<?> submitReportApp(@Valid YTSafetyHazardReportAppFormDTO formDTO){
         //descriptionList: JSON字符串 → List<YTSafetyHazardDescriptionDTO>
        List<YTSafetyHazardDescriptionDTO> descriptionList = null;
        if (StringUtils.isNotBlank(formDTO.getDescriptionList())) {
            descriptionList = JSON.parseArray(formDTO.getDescriptionList(), YTSafetyHazardDescriptionDTO.class);
        }
        // resultList: JSON字符串 → List<YTSafetyHazardResultDTO>
        List<YTSafetyHazardResultDTO> resultList = null;
        if (StringUtils.isNotBlank(formDTO.getResultList())) {
            resultList = JSON.parseArray(formDTO.getResultList(), YTSafetyHazardResultDTO.class);
        }
        // 拷贝字段 + 设置列表
        YTSafetyHazardReportDTO dto = new YTSafetyHazardReportDTO();
        BeanUtils.copyProperties(formDTO, dto);
        dto.setDescriptionList(descriptionList);
        dto.setResultList(resultList);
        YTSafetyHazardReport ytSafetyHazardReport = yTSafetyHazardReportService.submitReport(dto);
        return Result.OK(ytSafetyHazardReport);
    }

     @AutoLog(value = "隐患上报表-暂存草稿",operateType = 2)
     @ApiOperation(value="隐患上报表-暂存草稿", notes="隐患上报表-暂存草稿")
     @PostMapping(value = "/TSDraft")
     public Result<?> TSDraft(@RequestBody YTSafetyHazardReportDTO ytSafetyHazardReportDTO) {
         YTSafetyHazardReport ytSafetyHazardReport = yTSafetyHazardReportService.TSDraft(ytSafetyHazardReportDTO);
         return Result.OK("暂存草稿成功！",ytSafetyHazardReport);
     }

    @AutoLog(value = "隐患上报表-暂存草稿App",operateType = 2)
    @ApiOperation(value="隐患上报表-暂存草稿App", notes="隐患上报表-暂存草稿")
    @PostMapping(value = "/TSDraftApp")
    public Result<?> TSDraftApp(YTSafetyHazardReportAppFormDTO formDTO) {
        // descriptionList: JSON字符串 → List<YTSafetyHazardDescriptionDTO>
        List<YTSafetyHazardDescriptionDTO> descriptionList = null;
        if (StringUtils.isNotBlank(formDTO.getDescriptionList())) {
            descriptionList = JSON.parseArray(formDTO.getDescriptionList(), YTSafetyHazardDescriptionDTO.class);
        }
        // resultList: JSON字符串 → List<YTSafetyHazardResultDTO>
        List<YTSafetyHazardResultDTO> resultList = null;
        if (StringUtils.isNotBlank(formDTO.getResultList())) {
            resultList = JSON.parseArray(formDTO.getResultList(), YTSafetyHazardResultDTO.class);
        }
        // 拷贝字段 + 设置列表
        YTSafetyHazardReportDTO dto = new YTSafetyHazardReportDTO();
        BeanUtils.copyProperties(formDTO, dto);
        dto.setDescriptionList(descriptionList);
        dto.setResultList(resultList);
        YTSafetyHazardReport ytSafetyHazardReport = yTSafetyHazardReportService.TSDraft(dto);
        return Result.OK("暂存草稿成功！",ytSafetyHazardReport);
    }

	@ApiOperation(value="隐患上报表-历史上报列表", notes="隐患上报表-历史上报列表")
	@GetMapping(value = "/getHistoryList")
	public Result<IPage<YTSafetyHazardReportListVO>> historyList(@ApiParam(value = "筛选条件") @RequestParam(name="screeningCondition", required = false) String screeningCondition,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        IPage<YTSafetyHazardReportListVO> pageList = yTSafetyHazardReportService.historyList(pageNo, pageSize, screeningCondition);
		return Result.OK(pageList);
	}

     @ApiOperation(value="隐患上报表-厂区隐患列表", notes="隐患上报表-厂区隐患列表")
     @GetMapping(value = "/getFactoryHazardList")
     public Result<IPage<YTSafetyHazardReportListVO>> getFactoryHazardList(@ApiParam(value = "筛选条件") @RequestParam(name="screeningCondition", required = false) String screeningCondition,
                                           @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                           @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                           HttpServletRequest req) {
         IPage<YTSafetyHazardReportListVO> pageList = yTSafetyHazardReportService.getFactoryHazardList(pageNo, pageSize, screeningCondition);
         return Result.OK(pageList);
     }

    @ApiOperation(value="隐患上报表-草稿列表", notes="隐患上报表-草稿列表")
    @GetMapping(value = "/getDraftList")
    public Result<IPage<YTSafetyHazardReportListVO>> getDraftList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                                HttpServletRequest req) {
        IPage<YTSafetyHazardReportListVO> pageList = yTSafetyHazardReportService.getDraftList(pageNo, pageSize);
        return Result.OK(pageList);
    }

	@AutoLog(value = "隐患上报表-批量删除", operateType = 4)
	@ApiOperation(value="隐患上报表-批量删除", notes="隐患上报表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		return yTSafetyHazardReportService.deleteDraftBatch(Arrays.asList(ids.split(",")));
	}

    @AutoLog(value = "隐患上报表-批量删除App", operateType = 4)
    @ApiOperation(value="隐患上报表-批量删除App", notes="隐患上报表-批量删除App")
    @PostMapping(value = "/deleteBatchApp")
    public Result<?> deleteBatchApp(@RequestParam(name="ids",required=true) String ids) {
        log.info("deleteBatchAppESC：{}", ids);
        return yTSafetyHazardReportService.deleteDraftBatch(Arrays.asList(ids.split(",")));
    }

	@ApiOperation(value="隐患上报表-通过id查询", notes="隐患上报表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<YTSafetyHazardReportVO> queryById(@RequestParam(name="id",required=true) Long id) {
        YTSafetyHazardReportVO hazardReportVO = yTSafetyHazardReportService.getReportDetail(id);
		return Result.OK(hazardReportVO);
	}

    @AutoLog(value = "隐患上报表-审核",operateType = 3)
    @ApiOperation(value="隐患上报表-审核", notes="隐患上报表-审核")
    @PostMapping(value = "/reviewReport")
    public Result<?> reviewReport(@RequestBody @Valid YTSafetyHazardReportReviewDTO reportReviewDTO) {
        yTSafetyHazardReportService.reviewReport(reportReviewDTO);
        return Result.OK("审核修改成功");
    }

    @AutoLog(value = "隐患上报表-审核App",operateType = 3)
    @ApiOperation(value="隐患上报表-审核App", notes="隐患上报表-审核App")
    @PostMapping(value = "/reviewReportApp")
    public Result<?> reviewReportApp(@Valid YTSafetyHazardReportReviewAppFormDTO formDTO) {
        // descriptionList: JSON字符串 → List<YTSafetyHazardDescriptionDTO>
        List<YTSafetyHazardDescriptionDTO> descriptionList = null;
        if (StringUtils.isNotBlank(formDTO.getDescriptionList())) {
            descriptionList = JSON.parseArray(formDTO.getDescriptionList(), YTSafetyHazardDescriptionDTO.class);
        }
        // resultList: JSON字符串 → List<YTSafetyHazardResultDTO>
        List<YTSafetyHazardResultDTO> resultList = null;
        if (StringUtils.isNotBlank(formDTO.getResultList())) {
            resultList = JSON.parseArray(formDTO.getResultList(), YTSafetyHazardResultDTO.class);
        }
        // 拷贝字段 + 设置列表
        YTSafetyHazardReportReviewDTO dto = new YTSafetyHazardReportReviewDTO();
        BeanUtils.copyProperties(formDTO, dto);
        dto.setDescriptionList(descriptionList);
        dto.setResultList(resultList);
        yTSafetyHazardReportService.reviewReport(dto);
        return Result.OK("审核修改成功");
    }

    @AutoLog(value = "隐患上报表-整改人提交整改结果",operateType = 2)
    @ApiOperation(value="隐患上报表-整改人提交整改结果", notes="隐患上报表-整改人提交整改结果")
    @PostMapping(value = "/submitRectification")
    public Result<?> submitRectification(@RequestBody @Valid YTSafetyHazardReportAbarbeitungDTO reportAbarbeitungDTO) {
        return yTSafetyHazardReportService.submitRectification(reportAbarbeitungDTO);
    }

    @AutoLog(value = "隐患上报表-整改人提交整改结果App",operateType = 2)
    @ApiOperation(value="隐患上报表-整改人提交整改结果App", notes="隐患上报表-整改人提交整改结果App")
    @PostMapping(value = "/submitRectificationApp")
    public Result<?> submitRectificationApp(@Valid YTSafetyHazardReportAbarbeitungAppFormDTO formDTO) {
        YTSafetyHazardReportAbarbeitungDTO abarbeitungDTO = new YTSafetyHazardReportAbarbeitungDTO();

        abarbeitungDTO.setId(formDTO.getId());
        String abarbeitungResult = formDTO.getAbarbeitungResult();
        if (StringUtils.isNotBlank(abarbeitungResult)){
            YTSafetyHazardResultDTO ytSafetyHazardResultDTO = JSON.parseObject(abarbeitungResult, YTSafetyHazardResultDTO.class);
            abarbeitungDTO.setAbarbeitungResult(ytSafetyHazardResultDTO);
        }else {
            return Result.error("整改结果不能为空");
        }
        return yTSafetyHazardReportService.submitRectification(abarbeitungDTO);
    }

    @AutoLog(value = "延期申请-提交延期申请",operateType = 2)
    @ApiOperation(value="延期申请-提交延期申请", notes="延期申请-提交延期申请")
    @PostMapping(value = "/applyDelay")
    public Result<?> applyDelay(@RequestBody @Valid YTSafetyHazardDelayApplyDTO delayDTO) {
        return yTSafetyHazardReportService.applyDelay(delayDTO);
    }

    @AutoLog(value = "延期申请-提交延期申请App",operateType = 2)
    @ApiOperation(value="延期申请-提交延期申请App", notes="延期申请-提交延期申请App")
    @PostMapping(value = "/applyDelayApp")
    public Result<?> applyDelayApp(@Valid YTSafetyHazardDelayApplyDTO delayDTO) {
        return yTSafetyHazardReportService.applyDelay(delayDTO);
    }

    @AutoLog(value = "隐患上报表-流程操作",operateType=3)
    @ApiOperation(value="隐患上报表-流程操作", notes="隐患上报表-流程操作")
    @PostMapping(value = "/processApprove")
    public Result<?> processApprove(@RequestBody BpmProcessParamDTO processParamDTO) {
        return Result.OK(yTSafetyHazardReportService.executeProcess(processParamDTO));
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
        return Result.OK(yTSafetyHazardReportService.executeProcess(processParamDTO));
    }

    @ApiOperation(value="隐患上报表-修改历史列表", notes="隐患上报表-修改历史列表")
    @GetMapping(value = "/getModifyHistoryList")
    public Result<List<YTSafetyHazardReportHistoryVO>> getModifyHistoryList(@RequestParam(name="reportId",required=true) Long reportId) {
        List<YTSafetyHazardReportHistoryVO> historyVOList = yTSafetyHazardReportService.getModifyHistoryList(reportId);
        return Result.OK(historyVOList);
    }

    @RequiresPermissions("systemd:hazard:report:ledger:list")
    @ApiOperation(value="隐患台账-分页查询", notes="隐患台账-分页查询")
    @GetMapping(value = "/getHazardLedgerList")
    public Result<IPage<YTSafetyHazardLedgerVO>> getHazardLedgerList(YTSafetyHazardLedgerQueryDTO queryDTO) {
        IPage<YTSafetyHazardLedgerVO> ledgerList = yTSafetyHazardReportService.getHazardLedgerList(queryDTO);
        return Result.OK(ledgerList);
    }

    @RequiresPermissions("systemd:hazard:report:ledger:export")
    @ApiOperation(value="隐患台账-导出", notes="隐患台账-导出")
    @GetMapping(value = "/exportHazardLedger")
    public ModelAndView exportHazardLedger(YTSafetyHazardLedgerQueryDTO queryDTO,
                                           HttpServletRequest request) {
        LoginUser loginUser = BusUtits.getLoginUser();
        List<YTSafetyHazardLedgerVO> hazardLedgerVOList = yTSafetyHazardReportService.exportHazardLedger(queryDTO, request);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "隐患台账");
        mv.addObject(NormalExcelConstants.CLASS, YTSafetyHazardLedgerVO.class);
        ExportParams exportParams = new ExportParams("隐患台账", "导出人:" + (loginUser != null ? loginUser.getRealname() : ""));
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, hazardLedgerVOList);
        return mv;
    }

//    @RequiresPermissions("systemd:hazard:report:batch:submission")
//    @AutoLog(value = "隐患台账-批量提交",operateType=3)
//    @ApiOperation(value="隐患台账-批量提交", notes="隐患台账-批量提交")
//    @PostMapping(value = "/batchSubmission")
//    public  Result<?> batchSubmission(@RequestBody YTSafetyHazardBatchSubmissionDTO batchSubmissionDTO, HttpServletRequest request) {
//        yTSafetyHazardReportService.batchSubmission(batchSubmissionDTO);
//        return Result.OK();
//    }
}
