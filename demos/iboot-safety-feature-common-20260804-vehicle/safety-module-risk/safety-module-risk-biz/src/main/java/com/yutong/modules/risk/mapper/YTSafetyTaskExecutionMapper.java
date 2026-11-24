package com.yutong.modules.risk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.dto.YTSafetyTaskExecutionDTO;
import com.yutong.modules.risk.entity.YTSafetyTaskExecution;
import com.yutong.modules.risk.vo.PatrolTaskGroupVO;
import com.yutong.modules.risk.vo.YTSafetyTaskExecutionVO;
import com.yutong.modules.risk.vo.YTSafetyTaskExecutionXCRWExportVO;
import com.yutong.modules.risk.vo.YTSafetyTaskExecutionZXRWExportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 用户任务执行表
 * @Author: jeecg-boot
 * @Date:   2026-06-25
 * @Version: V1.0
 */
@Mapper
public interface YTSafetyTaskExecutionMapper extends BaseMapper<YTSafetyTaskExecution> {
    
    /**
     * 分页查询任务执行列表
     * @param page 分页对象
     * @param dto 查询条件
     * @return 分页结果
     */
    IPage<YTSafetyTaskExecutionVO> queryPage(@Param("page") Page<YTSafetyTaskExecutionVO> page,
                                             @Param("dto") YTSafetyTaskExecutionDTO dto);

    List<YTSafetyTaskExecutionXCRWExportVO> exportPatrolMissionQueryPage(@Param("dto") YTSafetyTaskExecutionDTO dto);

    List<YTSafetyTaskExecutionZXRWExportVO> exportSpecialTaskQueryPage(@Param("dto") YTSafetyTaskExecutionDTO dto);

    /**
     * 查询用户待办任务分组统计（按 taskType + postType 分组）
     * @param execUserId 执行用户ID
     * @return 分组统计列表
     */
    List<PatrolTaskGroupVO> selectPatrolToDo(@Param("execUserId") Long execUserId);

}
