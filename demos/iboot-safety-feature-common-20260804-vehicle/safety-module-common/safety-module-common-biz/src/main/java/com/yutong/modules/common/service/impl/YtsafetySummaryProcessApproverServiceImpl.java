package com.yutong.modules.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessApproverService;
import com.yutong.modules.common.entity.YtsafetySummaryProcessApprover;
import com.yutong.modules.common.entity.bpm.ProcessApprover;
import com.yutong.modules.common.mapper.YtsafetySummaryProcessApproverMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 流程审批人记录表
 * @Author: jeecg-boot
 * @Date:   2026-06-29
 * @Version: V1.0
 */
@Service
public class YtsafetySummaryProcessApproverServiceImpl extends ServiceImpl<YtsafetySummaryProcessApproverMapper, YtsafetySummaryProcessApprover> implements IYtsafetySummaryProcessApproverService {

    @Autowired
    private YtsafetySummaryProcessApproverMapper summaryProcessApproverMapper;

    @Override
    public void saveApproverRecord(Long id,List<ProcessApprover> approvers) {
        if (approvers == null || approvers.isEmpty()) return;
        List<String> empLogins = approvers.stream().map(ProcessApprover::getLoginName).collect(Collectors.toList());
        List<YtsafetySummaryProcessApprover> approver = summaryProcessApproverMapper.queryApproverRecord(empLogins,id);
        LambdaQueryWrapper<YtsafetySummaryProcessApprover> queryWrapper = new LambdaQueryWrapper<YtsafetySummaryProcessApprover>();
        queryWrapper.eq(YtsafetySummaryProcessApprover::getProcessId,id);
        queryWrapper.in(YtsafetySummaryProcessApprover::getNodeHandlerLogin,empLogins);
        summaryProcessApproverMapper.delete(queryWrapper);
        this.saveBatch(approver);
    }

    @Override
    public void savePermit(Long id, String permits) {
        if(StringUtils.isEmpty(permits)) return;
        List<String> empLogins = Arrays.stream(permits.split(",")).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        List<YtsafetySummaryProcessApprover> approver = summaryProcessApproverMapper.queryApproverRecord(empLogins,id);
        LambdaQueryWrapper<YtsafetySummaryProcessApprover> queryWrapper = new LambdaQueryWrapper<YtsafetySummaryProcessApprover>();
        queryWrapper.eq(YtsafetySummaryProcessApprover::getProcessId,id);
        queryWrapper.in(YtsafetySummaryProcessApprover::getNodeHandlerLogin,empLogins);
        summaryProcessApproverMapper.delete(queryWrapper);
        this.saveBatch(approver);
    }
}
