package com.yutong.modules.risk.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.yutong.modules.risk.entity.YtsafetyRiskDataProcessDetails;
import com.yutong.modules.risk.vo.YtsafetyRiskDataDetailsVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 风险数据变更流程明细表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
public interface YtsafetyRiskDataProcessDetailsMapper extends BaseMapper<YtsafetyRiskDataProcessDetails> {

    /**
     * 根据流程主键删除详情数据
     * @param processId
     */
    void deleteByProcessId(@Param("processId") Long processId);

    /**
     * 根据流程主键查询详情列表
     * @param processId
     * @return
     */
    List<YtsafetyRiskDataDetailsVO> queryList(@Param("processId") Long processId);

    /**
     * 批量插入明细数据（单条SQL多VALUES）
     */
    int batchInsert(@Param("list") List<YtsafetyRiskDataProcessDetails> list);

}
