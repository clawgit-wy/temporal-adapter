package com.yutong.modules.risk.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.risk.dto.YTSafetyTaskExecutionDTO;
import com.yutong.modules.risk.entity.YTSafetyTaskExecution;
import com.yutong.modules.risk.vo.PatrolTaskGroupVO;
import com.yutong.modules.risk.vo.YTSafetyTaskExecutionVO;
import com.yutong.modules.risk.vo.YTSafetyTaskExecutionXCRWExportVO;
import com.yutong.modules.risk.vo.YTSafetyTaskExecutionZXRWExportVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public interface YTSafetyTaskExecutionService extends IService<YTSafetyTaskExecution>{
    
    /**
     * 分页查询任务执行列表
     * @param page 分页对象
     * @param taskExecutionDTO 查询条件
     * @return 分页结果
     */
    IPage<YTSafetyTaskExecutionVO> queryPage(Page<YTSafetyTaskExecutionVO> page, YTSafetyTaskExecutionDTO taskExecutionDTO);

    List<YTSafetyTaskExecutionXCRWExportVO> exportPatrolMissionQueryPage(HttpServletRequest request, YTSafetyTaskExecutionDTO taskExecutionDTO);


    List<YTSafetyTaskExecutionZXRWExportVO> exportSpecialTaskQueryPage(HttpServletRequest request, YTSafetyTaskExecutionDTO taskExecutionDTO);

    List<YTSafetyTaskExecutionVO> getMyTaskList(String userId);

    /**
     * 查询用户待办任务分组统计（按 taskType + postType 分组）
     * @param execUserId 执行用户ID
     * @return 分组统计列表
     */
    List<PatrolTaskGroupVO> selectPatrolToDo(Long execUserId);

}
