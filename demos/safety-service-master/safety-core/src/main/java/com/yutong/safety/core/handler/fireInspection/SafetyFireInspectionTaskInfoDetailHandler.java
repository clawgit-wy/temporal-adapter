package com.yutong.safety.core.handler.fireInspection;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionTaskInfoDO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionTaskInfoDetailDO;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionEipProcessParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskInfoDetailAddParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskInfoDetailParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskInfoDetailQueryParams;
import com.yutong.safety.core.dto.fireInspection.FireInspectionRectificationResultDto;
import com.yutong.safety.core.dto.fireInspection.FireInspectionTaskInfoCountDto;
import com.yutong.safety.core.service.SafetyLogMessageService;
import com.yutong.safety.core.service.fireInspection.SafetyFireInspectionTaskInfoDetailService;
import com.yutong.safety.core.service.fireInspection.SafetyFireInspectionTaskInfoService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.yutong.safety.core.db.entity.fireInspection.common.FireInspectionConstants.*;

@Component
public class SafetyFireInspectionTaskInfoDetailHandler {
    @Resource
    private SafetyLogMessageService safetyLogMessageService;
    @Resource
    private SafetyFireInspectionTaskInfoDetailService safetyFireInspectionTaskInfoDetailService;
    @Resource
    private SafetyFireInspectionTaskInfoService safetyFireInspectionTaskInfoService;

    public PageVO<SafetyFireInspectionTaskInfoDetailDO> selectPageList(SafetyFireInspectionTaskInfoDetailQueryParams params) {
        // 获取数据
        long total = safetyFireInspectionTaskInfoDetailService.selectCount(params);
        List<SafetyFireInspectionTaskInfoDetailDO> list = safetyFireInspectionTaskInfoDetailService.selectList(params, true);
        return new PageVO<>(total, list);
    }

    public List<SafetyFireInspectionTaskInfoDetailDO> getDetailList(SafetyFireInspectionTaskInfoDetailQueryParams params) {
        return safetyFireInspectionTaskInfoDetailService.selectList(params, false);
    }

    /**
     * 任务详情明细完成提交
     *
     * @param params 入参
     */
    public Object submitTaskInfo(SafetyFireInspectionTaskInfoDetailAddParams params) {
        // 过滤掉没有检查结果的数据
        List<SafetyFireInspectionTaskInfoDetailParams> list = params.getDetailInfoList().stream().filter(li -> li.getResult() != null).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) return null;
        List<Long> ids = list.stream().map(SafetyFireInspectionTaskInfoDetailParams::getId).collect(Collectors.toList());
        // 数据筛选，只获取没有提交的数据，只能提交一次
        List<SafetyFireInspectionTaskInfoDetailDO> infoDetailDOS = safetyFireInspectionTaskInfoDetailService.selectList(ids);
        if (CollectionUtils.isEmpty(infoDetailDOS)) return null;
        List<Long> submitIds = infoDetailDOS.stream().map(SafetyFireInspectionTaskInfoDetailDO::getId).collect(Collectors.toList());
        list = list.stream().filter(li -> submitIds.contains(li.getId())).collect(Collectors.toList());

        // 循环创建修改对象
        Date date = new Date();
        List<SafetyFireInspectionTaskInfoDetailDO> updateDto = new ArrayList<>();
        list.forEach(li -> {
            SafetyFireInspectionTaskInfoDetailDO dto = new SafetyFireInspectionTaskInfoDetailDO();
            if (li.getResult() == 0) {
                // 异常的数据
                dto.setInspectionResult(FIRE_ABNORMAL);
                dto.setProcessId(li.getProcessId());
                dto.setProcessIdResult(RECTIFICATION_N);
            } else if (li.getResult() == 1) {
                // 正常的数据
                dto.setInspectionResult(FIRE_NORMAL);
            }
            dto.setInspectionTime(date);
            dto.setId(li.getId());
            updateDto.add(dto);
        });
        // 批量修改
        safetyFireInspectionTaskInfoDetailService.batchUpdate(updateDto);

        // 检查是否还有未完成的数据
        Long inspectionTaskId = params.getTaskInfoId();
        List<FireInspectionTaskInfoCountDto> countDtos = safetyFireInspectionTaskInfoDetailService.selectCount(Collections.singletonList(inspectionTaskId));
        if (CollectionUtils.isEmpty(countDtos)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "获取任务信息失败，ID：" + inspectionTaskId);
        }
        // 修改主数据状态（任务详情）
        FireInspectionTaskInfoCountDto dto = countDtos.get(0);
        SafetyFireInspectionTaskInfoDO record = new SafetyFireInspectionTaskInfoDO();
        if (dto.getDoneCount().equals(dto.getTotalCount())) {
            // 任务全部完成
            record.setTaskStatus(FIRE_TASK_COMPLETED);
            // 判断异常数据量
            if (dto.getErrorCount() == 0) {
                record.setInspectionResult(FIRE_NORMAL);
            } else {
                String s = String.valueOf(dto.getErrorCount());
                record.setInspectionResult(s + INSPECTION_RESULT_SUFFIX);
                record.setRectificationResult(s + RECTIFICATION_RESULT_SUFFIX);
            }
        }
        record.setTaskCompleteTime(date);
        record.setId(inspectionTaskId);
        safetyFireInspectionTaskInfoService.update(record);
        saveLogInfo("APP-SUBMIT", params.toString(), "APP");
        return null;
    }

    /**
     * EIP 流程状态回写
     * @param processParams 流程状态信息
     */
    public Object eipProcessStatusWriteBack(List<SafetyFireInspectionEipProcessParams> processParams) {
        try {
            if (CollectionUtils.isEmpty(processParams)) {
                return null;
            }
            // 获取符合条件的数据
            List<String> processIds = processParams.stream().map(SafetyFireInspectionEipProcessParams::getProcessId).collect(Collectors.toList());
            Map<String, String> dataMap = processParams.stream().collect(Collectors.toMap(SafetyFireInspectionEipProcessParams::getProcessId, SafetyFireInspectionEipProcessParams::getStatus));
            List<FireInspectionRectificationResultDto> list = safetyFireInspectionTaskInfoService.selectListByRectificationResult(processIds);
            if (CollectionUtils.isEmpty(list)) {
                return null;
            }
            // 数据修改
            List<Long> releaseList = new ArrayList<>();
            List<Long> discardList = new ArrayList<>();
            list.forEach(li -> {
                String processId = li.getProcessId();
                Long detailId = li.getDetailId();
                String status = dataMap.get(processId);
                if (FB.equals(status)) {
                    releaseList.add(detailId);
                }
                if (FQ.equals(status)) {
                    discardList.add(detailId);
                }
            });
            safetyFireInspectionTaskInfoDetailService.updateProcessIdResult(releaseList, 1);
            safetyFireInspectionTaskInfoDetailService.updateProcessIdResult(discardList, 2);

            // 修改任务详情的整改结果 三步走：1.根据任务详情ID（主表ID）获取对应明细表的整改结果完成数量； 2.构建数据（==0表示已完成， 否则数量是多少表示有多少未整改）； 3.批量变更
            List<Long> taskInfoIds = list.stream().map(FireInspectionRectificationResultDto::getId).distinct().collect(Collectors.toList());
            List<FireInspectionTaskInfoCountDto> dtos = safetyFireInspectionTaskInfoDetailService.selectCount(taskInfoIds);

            List<SafetyFireInspectionTaskInfoDO> record = dtos.stream().map(li -> {
                SafetyFireInspectionTaskInfoDO info = new SafetyFireInspectionTaskInfoDO();
                info.setId(li.getInspectionTaskId());
                Integer rectificationCount = li.getRectificationCount();
                if (rectificationCount == 0) {
                    info.setRectificationResult(FIRE_TASK_COMPLETED);
                } else {
                    info.setRectificationResult(li.getRectificationCount() + RECTIFICATION_RESULT_SUFFIX);
                }
                if (li.getErrorCount() == 0) {
                    info.setInspectionResult(FIRE_NORMAL);
                } else {
                    info.setInspectionResult(li.getErrorCount() + INSPECTION_RESULT_SUFFIX);
                }
                return info;
            }).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(record)) {
                safetyFireInspectionTaskInfoService.batchUpdate(record);
            }
            return null;
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "EIP 流程状态回写-执行失败");
        } finally {
            saveLogInfo("EIP-状态回写", processParams.toString(), "EIP回调");
        }
    }
    private void saveLogInfo(String ability, String message, String realName) {
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("消防巡检-APP");
        log.setAbility(ability);
        log.setMessage(message);
        log.setOperator(realName);
        safetyLogMessageService.addLogMessage(log);
    }
}
