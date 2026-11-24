package com.yutong.modules.risk.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.dto.YtsafetyHomeworkStageProcessDTO;
import com.yutong.modules.risk.entity.YtsafetyHomeworkStageProcessDetails;
import com.yutong.modules.risk.vo.YtsafetyHomeworkStageDetailsExportVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 作业环节变更流程明细表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
public interface IYtsafetyHomeworkStageProcessDetailsService extends IService<YtsafetyHomeworkStageProcessDetails> {

    void add(YtsafetyHomeworkStageProcessDTO dto);

    void edit(YtsafetyHomeworkStageProcessDTO dto);

    IPage<YtsafetyHomeworkStageProcessDetails> queryPageList(Page<YtsafetyHomeworkStageProcessDetails> page, YtsafetyHomeworkStageProcessDTO query);

    /**
     * 查询流程详情导出列表
     * @param processId
     * @return
     */
    List<YtsafetyHomeworkStageDetailsExportVO> queryExportList(Long processId);


    /**
     * 根据 processId 删除明细
     */
    void deleteByProcessId(Long processId);

    /**
     * 根据 changeId 查询明细列表
     */
    List<YtsafetyHomeworkStageProcessDetails> queryListByChangeId(Long changeId);

    /**
     * 查询流程详情导出列表
     * @param processId
     * @return
     */
    List<YtsafetyHomeworkStageProcessDetails> queryDetailsList(Long processId);



}
