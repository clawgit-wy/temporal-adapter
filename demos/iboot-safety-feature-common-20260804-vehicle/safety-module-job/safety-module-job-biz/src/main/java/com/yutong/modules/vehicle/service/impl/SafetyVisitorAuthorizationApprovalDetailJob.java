package com.yutong.modules.vehicle.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yutong.modules.entity.SafetyVisitorAuthorizationApprovalDetail;
import com.yutong.modules.vehicle.mapper.SafetyVisitorAuthorizationApprovalDetailJobMapper;
import com.yutong.modules.vehicle.service.ISafetyVisitorAuthorizationApprovalDetailJob;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;
import java.util.List;

/**
 * @Description: 访客审批授权明细表
 * @Author: jeecg-boot
 * @Date:   2026-08-10
 * @Version: V1.0
 */
@Service
public class SafetyVisitorAuthorizationApprovalDetailJob extends ServiceImpl<SafetyVisitorAuthorizationApprovalDetailJobMapper, SafetyVisitorAuthorizationApprovalDetail> implements ISafetyVisitorAuthorizationApprovalDetailJob {

    /**
     * 注销授权信息：根据明细ID批量将数据状态修改为注销，并记录注销原因
     *
     * @param ids          明细表ID集合
     * @param cancelReason 注销原因
     */
    @Override
    public void cancelAuthorizationInfo(List<Integer> ids, String cancelReason) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        LambdaUpdateWrapper<SafetyVisitorAuthorizationApprovalDetail> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(SafetyVisitorAuthorizationApprovalDetail::getStatus, 2)
                .set(SafetyVisitorAuthorizationApprovalDetail::getCancelReason, cancelReason)
                .set(SafetyVisitorAuthorizationApprovalDetail::getUpdateTime, new Date())
                .eq(SafetyVisitorAuthorizationApprovalDetail::getDelFlag, 0)
                .in(SafetyVisitorAuthorizationApprovalDetail::getId, ids);
        this.update(updateWrapper);
    }
}
