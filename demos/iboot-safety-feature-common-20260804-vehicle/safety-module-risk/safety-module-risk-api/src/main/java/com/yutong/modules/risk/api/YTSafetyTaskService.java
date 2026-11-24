package com.yutong.modules.risk.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.risk.dto.YTSafetyTaskInfoDTO;
import com.yutong.modules.risk.dto.YtsafetyTaskRiskDataDTO;
import com.yutong.modules.risk.entity.YTSafetyTaskInfo;
import com.yutong.modules.risk.vo.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public interface YTSafetyTaskService extends IService<YTSafetyTaskInfo>{
    
    /**
     * 添加任务（包含扩展信息）
     * @param taskInfoDTO 任务信息DTO
     */
    void addTask(YTSafetyTaskInfoDTO taskInfoDTO);
    
    /**
     * 编辑任务（包含扩展信息）
     * @param taskInfoDTO 任务信息DTO
     */
    void editTask(YTSafetyTaskInfoDTO taskInfoDTO);
    
    /**
     * 删除任务（逻辑删除）
     * @param id 任务ID
     */
    void deleteTask(Long id);
    
    /**
     * 根据ID查询任务详情
     * @param id 任务ID
     * @return 任务详情VO
     */
    YTSafetyTaskInfoVO queryById(Long id);
    
    /**
     * 分页查询任务列表
     * @param page 分页对象
     * @param taskInfoDTO 查询条件
     * @return 分页结果
     */
    IPage<YTSafetyTaskInfoVO> queryPage(Page<YTSafetyTaskInfoVO> page, YTSafetyTaskInfoDTO taskInfoDTO);

    /**
     * 导出任务列表
     * @param request HTTP请求
     * @param taskInfoDTO 查询条件
     * @return ModelAndView
     */
    List<YTSafetyTaskInfoExporVO> exportTaskList(HttpServletRequest request, YTSafetyTaskInfoDTO taskInfoDTO);


    void updateTaskStatusPening();

    void updateUserTaskExecutionStatus();

    void updateTaskStatusExpired();

    void issueTasks(Long taskInfoId);

    YTSafetyXcrwTaskExecutionDetailVO queryTaskExecutionDetailApp(String id);

    YTSafetyXcrwTaskExecutionDetailVO queryTaskExecutionDetailPc(String id);

    YTSafetyZxrwTaskExecutionDetailVO queryTaskZxrwExecutionDetailPc(String id);

    YTSafetyMyTaskVO queryMyTaskList(String execScope,String startDate,String endDate,Integer isCompleted,Integer pageNo,Integer pageSize);

    void zxrwEndTask(String id,String fileIds,String photoFileds,String exports);

    /**
     * 测试发送岗位验证通知
     */
    void testSendGwzyNotify(String emp);

    void updateTaskExecutionIsAbnormal(Long taskExecutionId);

    List<YtsafetyTaskUserVO> getTaskRiskDataSafetyOfficer(YtsafetyTaskRiskDataDTO riskDataDTO);
}
