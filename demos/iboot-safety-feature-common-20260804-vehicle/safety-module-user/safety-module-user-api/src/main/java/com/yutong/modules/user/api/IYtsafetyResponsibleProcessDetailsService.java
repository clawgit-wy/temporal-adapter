package com.yutong.modules.user.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.user.dto.YtsafetyResponsibleProcessDetailsDTO;
import com.yutong.modules.user.entity.YtsafetyResponsibleProcessDetails;
import com.yutong.modules.user.vo.YtsafetyResponsibleProcessDetailsVO;

import java.util.List;

/**
 * @Description: 安全责任人变更流程明细表
 * @Author: lvss
 * @Date:   2026-06-22
 * @Version: V1.0
 */
public interface IYtsafetyResponsibleProcessDetailsService extends IService<YtsafetyResponsibleProcessDetails> {

    /**
     * 批量保存
     * @param dtos
     */
    public void save(Long processId,List<YtsafetyResponsibleProcessDetailsDTO> dtos);

    /**
     * 查询安全责任人详情
     * @param processId
     * @return
     */
    public List<YtsafetyResponsibleProcessDetailsVO> queryList(Long processId);
}
