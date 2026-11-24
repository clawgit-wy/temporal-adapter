package com.yutong.modules.risk.service;

import com.yutong.modules.risk.dto.YtsafetyRiskDataDetailsDTO;
import com.yutong.modules.risk.entity.YtsafetyRiskDataProcessDetails;
import com.yutong.modules.risk.vo.YtsafetyRiskDataDetailsExportVO;
import com.yutong.modules.risk.vo.YtsafetyRiskDataDetailsVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.system.vo.LoginUser;

import java.util.List;

/**
 * @Description: 风险数据变更流程明细表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
public interface IYtsafetyRiskDataProcessDetailsService extends IService<YtsafetyRiskDataProcessDetails> {

    /**
     * 批量保存流程详情（先删后增）
     * @param processId
     * @param dtos
     */
    void save(Long processId, List<YtsafetyRiskDataDetailsDTO> dtos);

    /**
     * 批量保存流程详情（传入LoginUser，避免异步线程中ThreadLocal丢失）
     */
    void save(Long processId, List<YtsafetyRiskDataDetailsDTO> dtos, LoginUser loginUser);

    /**
     * 查询流程详情列表
     * @param processId
     * @return
     */
    List<YtsafetyRiskDataDetailsVO> queryList(Long processId);

    /**
     * 查询流程详情导出列表
     * @param processId
     * @return
     */
    List<YtsafetyRiskDataDetailsExportVO> queryExportList(Long processId);

}
