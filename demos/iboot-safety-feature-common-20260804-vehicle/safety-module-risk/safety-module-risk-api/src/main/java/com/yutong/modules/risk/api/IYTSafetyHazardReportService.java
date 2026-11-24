package com.yutong.modules.risk.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.dto.*;
import com.yutong.modules.risk.entity.YTSafetyHazardReport;
import com.yutong.modules.risk.vo.YTSafetyHazardLedgerVO;
import com.yutong.modules.risk.vo.YTSafetyHazardReportHistoryVO;
import com.yutong.modules.risk.vo.YTSafetyHazardReportListVO;
import com.yutong.modules.risk.vo.YTSafetyHazardReportVO;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IYTSafetyHazardReportService extends IService<YTSafetyHazardReport> {

    /** 提交上报 */
    YTSafetyHazardReport submitReport(YTSafetyHazardReportDTO reportDTO);

    /** 暂存草稿 */
    YTSafetyHazardReport TSDraft(YTSafetyHazardReportDTO ytSafetyHazardReportDTO);

    /** 历史列表 */
    IPage<YTSafetyHazardReportListVO> historyList(Integer pageNo, Integer pageSize, String screeningCondition);

    /** 厂区隐患 */
    IPage<YTSafetyHazardReportListVO> getFactoryHazardList(Integer pageNo, Integer pageSize, String screeningCondition);

    /** 草稿列表 */
    IPage<YTSafetyHazardReportListVO> getDraftList(Integer pageNo, Integer pageSize);

    /** 批量删除（仅草稿状态） */
    Result<?> deleteDraftBatch(List<String> ids);

    /** 获取完整详情（主表+描述+结果+延期+历史） */
    YTSafetyHazardReportVO getReportDetail(Long id);

    /** 审核（通过/驳回） */
    void reviewReport(YTSafetyHazardReportReviewDTO reportReviewDTO);

    /** 提交整改结果 */
    Result<?> submitRectification(YTSafetyHazardReportAbarbeitungDTO reportAbarbeitungDTO);

    /** 申请延期 */
    Result<?> applyDelay(YTSafetyHazardDelayApplyDTO delayDTO);

    BpmReponseParamsVO executeProcess(BpmProcessParamDTO processParamDTO);

    /** 获取修改历史列表（仅orgId和rectifyDeadline有变化的记录） */
    List<YTSafetyHazardReportHistoryVO> getModifyHistoryList(Long reportId);

    /** 隐患台账分页查询 */
    IPage<YTSafetyHazardLedgerVO> getHazardLedgerList(YTSafetyHazardLedgerQueryDTO queryDTO);

    /** 隐患台账导出 */
    List<YTSafetyHazardLedgerVO> exportHazardLedger(YTSafetyHazardLedgerQueryDTO queryDTO, HttpServletRequest request);

    /** 隐患批量提交 */
    Result<?> batchSubmission(YTSafetyHazardBatchSubmissionDTO batchSubmissionDTO);
}
