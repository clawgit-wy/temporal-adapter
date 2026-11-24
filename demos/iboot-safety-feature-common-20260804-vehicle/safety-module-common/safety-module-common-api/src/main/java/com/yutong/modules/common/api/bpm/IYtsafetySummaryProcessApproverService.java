package com.yutong.modules.common.api.bpm;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.common.entity.YtsafetySummaryProcessApprover;
import com.yutong.modules.common.entity.bpm.ProcessApprover;

import java.util.List;

/**
 * @Description: 流程审批人记录表
 * @Author: jeecg-boot
 * @Date:   2026-06-29
 * @Version: V1.0
 */
public interface IYtsafetySummaryProcessApproverService extends IService<YtsafetySummaryProcessApprover> {

    /**
     * 记录审批人
     * @param id
     */
    public void saveApproverRecord(Long id, List<ProcessApprover> approvers);

    /**
     * 保存特权人
     * @param permits
     */
    public void savePermit(Long id, String permits);
}
