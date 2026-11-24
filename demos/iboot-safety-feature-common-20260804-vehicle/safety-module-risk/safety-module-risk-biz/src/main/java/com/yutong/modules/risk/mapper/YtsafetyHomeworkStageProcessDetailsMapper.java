package com.yutong.modules.risk.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.yutong.modules.risk.entity.YtsafetyHomeworkStageProcessDetails;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 作业环节变更流程明细表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
public interface YtsafetyHomeworkStageProcessDetailsMapper extends BaseMapper<YtsafetyHomeworkStageProcessDetails> {

    /**
     * 批量新增
     */
    int batchInsert(@Param("list") List<YtsafetyHomeworkStageProcessDetails> list);

    /**
     * 批量修改
     */
    int batchUpdate(@Param("list") List<YtsafetyHomeworkStageProcessDetails> list);

    /**
     * 根据 processId 删除明细
     */
    int deleteByProcessId(@Param("processId") Long processId);
}
