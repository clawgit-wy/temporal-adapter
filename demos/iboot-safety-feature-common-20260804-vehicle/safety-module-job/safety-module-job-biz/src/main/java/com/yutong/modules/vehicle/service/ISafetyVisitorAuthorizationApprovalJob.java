package com.yutong.modules.vehicle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.entity.SafetyVisitorAuthorizationApproval;

import java.util.Set;

/**
 * @Description: 访客审批授权表
 * @Author: jeecg-boot
 * @Date:   2026-08-10
 * @Version: V1.0
 */
public interface ISafetyVisitorAuthorizationApprovalJob extends IService<SafetyVisitorAuthorizationApproval> {

    /**
     * 组织变更处理：注销组织变更人员相关的访客授权，并发送宇信通提醒消息
     *
     * @param empNos 组织变更/离职的人员员工号集合
     */
    void positionChange(Set<String> empNos);
}
