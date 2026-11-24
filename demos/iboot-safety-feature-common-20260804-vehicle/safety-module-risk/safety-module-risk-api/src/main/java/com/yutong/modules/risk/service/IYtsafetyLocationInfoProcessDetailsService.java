package com.yutong.modules.risk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.dto.YtsafetyLocationInfoProcessDetailsDTO;
import com.yutong.modules.risk.entity.YtsafetyLocationInfoProcess;
import com.yutong.modules.risk.entity.YtsafetyLocationInfoProcessDetails;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoDetailsExportVO;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoExportVO;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoProcessDetailsVO;
import org.jeecg.common.system.vo.LoginUser;

import java.util.List;

/**
 * @Description: 位置信息变更流程明细表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
public interface IYtsafetyLocationInfoProcessDetailsService extends IService<YtsafetyLocationInfoProcessDetails> {

    /**
     * 保存流程详情数据
     * @param processId
     * @param dtos
     */
    void save(Long processId, List<YtsafetyLocationInfoProcessDetailsDTO> dtos);

    /**
     * 保存流程详情数据（传入LoginUser，避免异步线程中ThreadLocal丢失）
     */
    void save(Long processId, List<YtsafetyLocationInfoProcessDetailsDTO> dtos, LoginUser loginUser);

    /**
     * 根据流程主键查询详情列表
     * @param processId
     * @return
     */
    List<YtsafetyLocationInfoProcessDetailsVO> queryList(Long processId);

    /**
     * 查询流程详情导出列表
     * @param processId
     * @return
     */
    List<YtsafetyLocationInfoDetailsExportVO> queryExportList(Long processId);

    /**
     * 与已有数据比对，标记变更字段
     * @param processId 流程主键
     * @param newData 导入的新数据
     */
    void compareWithExisting(Long processId, List<YtsafetyLocationInfoProcessDetailsVO> newData);

}
