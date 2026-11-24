package com.yutong.modules.common.mapper;

import java.util.List;

import com.yutong.modules.common.entity.YtsafetySummaryProcessNowapprover;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 下一节点审核人
 * @Author: jeecg-boot
 * @Date:   2026-06-29
 * @Version: V1.0
 */
public interface YtsafetySummaryProcessNowapproverMapper extends BaseMapper<YtsafetySummaryProcessNowapprover> {

    public List<YtsafetySummaryProcessNowapprover> querynextApprover(@Param("empLogins") List<String> empLogins, @Param("processId") Long processId);

}
