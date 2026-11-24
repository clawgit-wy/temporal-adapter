package com.yutong.modules.bpmcallback.action;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import com.yutong.modules.risk.entity.YtsafetyRiskData;
import com.yutong.modules.risk.entity.YtsafetyRiskDataProcess;
import com.yutong.modules.risk.entity.YtsafetyRiskDataProcessDetails;
import com.yutong.modules.risk.service.IYtsafetyRiskDataProcessDetailsService;
import com.yutong.modules.risk.service.IYtsafetyRiskDataProcessService;
import com.yutong.modules.risk.service.IYtsafetyRiskDataService;
import com.yutong.modules.risk.vo.YtsafetyRiskDataDetailsVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 风险数据完成回调，同步流程明细数据到风险数据主表
 */
@Slf4j
@Component("risk_data_finish_callback")
public class RiskDataFinishAction extends NodeAction {

    @Autowired
    private IYtsafetyRiskDataProcessDetailsService riskDataProcessDetailsService;

    @Autowired
    private IYtsafetyRiskDataProcessService riskDataProcessService;

    @Autowired
    private IYtsafetyRiskDataService riskDataService;

    @Override
    public void execute(BpmMethodProcessDTO event) {
        Long processId = Long.valueOf(event.getFormId().getFormInstanceId());
        syncRiskData(processId);
    }

    /**
     * 同步流程明细数据到风险数据主表：风险编码存在则更新，不存在则新增
     */
    private void syncRiskData(Long processId) {
        log.info("处理风险数据流程完成回调, processId: {}", processId);
        YtsafetyRiskDataProcess process = riskDataProcessService.getById(processId);
        if (process == null) {
            log.warn("风险数据流程不存在, processId: {}", processId);
            return;
        }
        List<YtsafetyRiskDataDetailsVO> details = riskDataProcessDetailsService.queryList(processId);
        if (details == null || details.isEmpty()) {
            log.warn("风险数据流程明细为空, processId: {}", processId);
            return;
        }
        for (YtsafetyRiskDataDetailsVO detail : details) {
            String updateMsg = processDetail(process, detail);
            saveUpdateMsg(detail.getId(), updateMsg);
        }
    }

    /**
     * 处理单条明细，返回执行结果信息
     */
    private String processDetail(YtsafetyRiskDataProcess process, YtsafetyRiskDataDetailsVO detail) {
        if (StringUtils.isEmpty(detail.getRiskCode())) {
            log.warn("明细风险编码为空, 跳过, detailId: {}", detail.getId());
            return "风险编码为空，跳过同步";
        }
        YtsafetyRiskData existing = riskDataService.getOne(new LambdaQueryWrapper<YtsafetyRiskData>()
                .eq(YtsafetyRiskData::getRiskCode, detail.getRiskCode()));
        if (existing == null) {
            insertRiskData(process, detail);
            return "风险数据已同步新增";
        }
        updateRiskData(process, existing, detail);
        return "风险数据已同步更新";
    }

    /**
     * 保存执行结果到明细表 updateMsg 字段
     */
    private void saveUpdateMsg(Long detailId, String updateMsg) {
        if (detailId == null || updateMsg == null) {
            return;
        }
        riskDataProcessDetailsService.update(new LambdaUpdateWrapper<YtsafetyRiskDataProcessDetails>()
                .eq(YtsafetyRiskDataProcessDetails::getId, detailId)
                .set(YtsafetyRiskDataProcessDetails::getUpdateMsg, updateMsg));
    }

    /**
     * 更新已有风险数据
     */
    private void updateRiskData(YtsafetyRiskDataProcess process, YtsafetyRiskData existing, YtsafetyRiskDataDetailsVO detail) {
        existing.setProcessId(detail.getProcessId());
        fillCommonFields(existing, process, detail);
        riskDataService.updateById(existing);
        log.info("更新风险数据成功, riskCode: {}", detail.getRiskCode());
    }

    /**
     * 新增风险数据
     */
    private void insertRiskData(YtsafetyRiskDataProcess process, YtsafetyRiskDataDetailsVO detail) {
        YtsafetyRiskData entity = new YtsafetyRiskData();
        entity.setProcessId(process.getId());
        entity.setRiskCode(detail.getRiskCode());
        entity.setDelFlag(0);
        fillCommonFields(entity, process, detail);
        riskDataService.save(entity);
        log.info("新增风险数据成功, riskCode: {}", detail.getRiskCode());
    }

    /**
     * 填充风险数据公共字段（更新与新增共用）
     */
    private void fillCommonFields(YtsafetyRiskData entity, YtsafetyRiskDataProcess process, YtsafetyRiskDataDetailsVO detail) {
        entity.setRiskCategoryId(detail.getRiskCategoryId());
        entity.setRiskCategoryCode(detail.getRiskCategoryCode());
        entity.setRiskCategoryName(detail.getRiskCategoryName());
        entity.setFactoryId(detail.getFactoryId());
        entity.setFactoryName(detail.getFactoryName());
        entity.setAreaId(detail.getAreaId());
        entity.setAreaCode(detail.getAreaCode());
        entity.setAreaName(detail.getAreaName());
        entity.setOrgId(detail.getOrgId());
        entity.setOrgCode(detail.getOrgCode());
        entity.setOrgName(detail.getOrgName());
        entity.setLocationId(detail.getLocationId());
        entity.setLocationName(detail.getLocationName());
        entity.setRiskTypeId(detail.getRiskTypeId());
        entity.setRiskTypeName(detail.getRiskTypeName());
        entity.setJobStepId(detail.getJobStepId());
        entity.setJobStepCode(detail.getJobStepCode());
        entity.setJobStepName(detail.getJobStepName());
        entity.setSafetyRoleCode(detail.getSafetyRoleCode());
        entity.setManagerRoleCode(detail.getManagerRoleCode());
        entity.setCheckObject(detail.getCheckObject());
        entity.setRiskFactorId(detail.getRiskFactorId());
        entity.setRiskFactorName(detail.getRiskFactorName());
        entity.setRiskIdentifyId(detail.getRiskIdentifyId());
        entity.setRiskIdentifyName(detail.getRiskIdentifyName());
        entity.setExposeFreqId(detail.getExposeFreqId());
        entity.setExposeFreqName(detail.getExposeFreqName());
        entity.setPossibilityId(detail.getPossibilityId());
        entity.setPossibilityName(detail.getPossibilityName());
        entity.setSeverityId(detail.getSeverityId());
        entity.setSeverityName(detail.getSeverityName());
        entity.setRiskValue(detail.getRiskValue());
        entity.setRiskWarnColor(detail.getRiskWarnColor());
        entity.setRiskLevel(detail.getRiskLevel());
        entity.setRiskDescription(detail.getRiskDescription());
        entity.setPreventMeasure(detail.getPreventMeasure());
        entity.setSyncStatus(0);
        entity.setUpdateTime(new Date());
        entity.setUpdateName(process.getUpdateName());
        entity.setUpdateUser(process.getUpdateUser());
        entity.setCreateName(detail.getCreateName());
        entity.setCreateTime(detail.getCreateTime());
        entity.setCreateUser(detail.getCreateUser());
    }
}
