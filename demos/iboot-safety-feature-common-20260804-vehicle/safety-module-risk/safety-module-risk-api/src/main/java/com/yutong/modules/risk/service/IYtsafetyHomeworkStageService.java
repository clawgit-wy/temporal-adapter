package com.yutong.modules.risk.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.dto.YtsafetyHomeworkStageDTO;
import com.yutong.modules.risk.dto.YtsafetyHomeworkStageProcessDTO;
import com.yutong.modules.risk.entity.YtsafetyHomeworkStage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.risk.query.YtsafetyHomeworkStageQuery;
import com.yutong.modules.risk.vo.YtsafetyHomeworkStageExportVO;

import javax.validation.Valid;

import java.util.List;

/**
 * @Description: 作业环节表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
public interface IYtsafetyHomeworkStageService extends IService<YtsafetyHomeworkStage> {

    IPage<YtsafetyHomeworkStage> queryPageList(Page<YtsafetyHomeworkStage> page, YtsafetyHomeworkStageQuery query);

    /**
     * 查询列表（用于导出）
     * @param query
     * @return
     */
    List<YtsafetyHomeworkStageExportVO> queryList(YtsafetyHomeworkStageQuery query);

    /**
     * 下拉列表查询（依据名称模糊查询）
     * @param name 作业环节名称
     * @return
     */
    List<YtsafetyHomeworkStage> queryDropdownList(String name);

    /**
     * 将审批通过的流程明细复制到正式作业环节表
     * @param processId 流程主键
     */
    void copyProcessDetailsToStage(Long processId);

    /**
     * 根据linkCode查询作业环节
     * @param linkCode 链接编码
     * @return 作业环节对象
     */
    YtsafetyHomeworkStage getByLinkCode(String linkCode);





}
