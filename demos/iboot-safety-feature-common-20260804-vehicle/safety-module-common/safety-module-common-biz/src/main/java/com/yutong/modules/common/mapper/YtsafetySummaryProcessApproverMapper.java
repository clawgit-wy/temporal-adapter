package com.yutong.modules.common.mapper;

import java.util.List;

import com.yutong.modules.common.entity.YtsafetySummaryProcessApprover;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 流程审批人记录表
 * @Author: jeecg-boot
 * @Date:   2026-06-29
 * @Version: V1.0
 */
public interface YtsafetySummaryProcessApproverMapper extends BaseMapper<YtsafetySummaryProcessApprover> {

    public List<YtsafetySummaryProcessApprover> queryApproverRecord(@Param("empLogins") List<String> empLogins, @Param("processId") Long processId);

}
