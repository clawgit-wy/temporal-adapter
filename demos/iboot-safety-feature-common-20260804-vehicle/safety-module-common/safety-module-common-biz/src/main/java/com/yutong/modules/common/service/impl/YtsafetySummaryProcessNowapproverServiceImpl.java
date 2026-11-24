package com.yutong.modules.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessNowapproverService;
import com.yutong.modules.common.entity.YtsafetySummaryProcessNowapprover;
import com.yutong.modules.common.entity.bpm.ProcessApprover;
import com.yutong.modules.common.mapper.YtsafetySummaryProcessNowapproverMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 下一节点审核人
 * @Author: jeecg-boot
 * @Date:   2026-06-29
 * @Version: V1.0
 */
@Service
public class YtsafetySummaryProcessNowapproverServiceImpl extends ServiceImpl<YtsafetySummaryProcessNowapproverMapper, YtsafetySummaryProcessNowapprover> implements IYtsafetySummaryProcessNowapproverService {

    @Autowired
    private YtsafetySummaryProcessNowapproverMapper summaryProcessNextapproverMapper;

    @Override
    public void saveNextApprover(Long id, List<ProcessApprover> approvers) {
        LambdaQueryWrapper<YtsafetySummaryProcessNowapprover> queryWrapper = new LambdaQueryWrapper<YtsafetySummaryProcessNowapprover>();
        queryWrapper.eq(YtsafetySummaryProcessNowapprover::getProcessId, id);
        summaryProcessNextapproverMapper.delete(queryWrapper);
        if (approvers == null || approvers.isEmpty()) return;
        List<String> empLogins = approvers.stream().map(ProcessApprover::getLoginName).collect(Collectors.toList());
        List<YtsafetySummaryProcessNowapprover> approver = summaryProcessNextapproverMapper.querynextApprover(empLogins, id);
        this.saveBatch(approver);
    }
}
