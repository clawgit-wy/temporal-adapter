package com.yutong.modules.common.api.bpm;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.common.entity.YtsafetySummaryProcessNowapprover;
import com.yutong.modules.common.entity.bpm.ProcessApprover;

import java.util.List;

/**
 * @Description: 下一节点审核人
 * @Author: jeecg-boot
 * @Date:   2026-06-29
 * @Version: V1.0
 */
public interface IYtsafetySummaryProcessNowapproverService extends IService<YtsafetySummaryProcessNowapprover> {

    /**
     * 设置节点审批人
     * @param id
     * @param approvers
     */
    public void saveNextApprover(Long id,List<ProcessApprover> approvers);
}
