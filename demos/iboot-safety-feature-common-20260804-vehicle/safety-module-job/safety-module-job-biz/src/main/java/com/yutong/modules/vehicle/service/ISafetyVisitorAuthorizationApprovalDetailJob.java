package com.yutong.modules.vehicle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.entity.SafetyVisitorAuthorizationApprovalDetail;

import java.util.List;

/**
 * @Description: 访客审批授权明细表
 * @Author: jeecg-boot
 * @Date:   2026-08-10
 * @Version: V1.0
 */
public interface ISafetyVisitorAuthorizationApprovalDetailJob extends IService<SafetyVisitorAuthorizationApprovalDetail> {

    /**
     * 注销授权信息：根据明细ID批量将数据状态修改为注销
     *
     * @param ids          明细表ID集合
     * @param cancelReason 注销原因
     */
    void cancelAuthorizationInfo(List<Integer> ids, String cancelReason);
}
