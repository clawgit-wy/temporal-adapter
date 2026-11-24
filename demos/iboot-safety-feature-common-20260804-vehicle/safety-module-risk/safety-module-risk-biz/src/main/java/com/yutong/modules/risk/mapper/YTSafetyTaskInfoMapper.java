package com.yutong.modules.risk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.dto.YTSafetyTaskInfoDTO;
import com.yutong.modules.risk.dto.YtsafetyTaskRiskDataDTO;
import com.yutong.modules.risk.entity.YTSafetyHoliday;
import com.yutong.modules.risk.entity.YTSafetyTaskInfo;
import com.yutong.modules.risk.vo.*;
import com.yutong.modules.user.dto.TaskUserQueryDTO;
import com.yutong.modules.user.entity.SafetySysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: 任务台账表
 * @Author: jeecg-boot
 * @Date:   2026-06-25
 * @Version: V1.0
 */
@Mapper
public interface YTSafetyTaskInfoMapper extends BaseMapper<YTSafetyTaskInfo> {

    /**
     * 分页查询任务列表
     * @param page 分页对象
     * @param taskInfoDTO 查询条件
     * @return 分页结果
     */
    IPage<YTSafetyTaskInfoVO> queryPage(@Param("page") Page<YTSafetyTaskInfoVO> page,
                                        @Param("dto") YTSafetyTaskInfoDTO taskInfoDTO);

    List<String> getRiskGradingStandard(@Param("ids")String[] ids);

    public List<YtsafetyTaskUserVO> getTaskUser(@Param("dto") TaskUserQueryDTO taskUserQueryDTO);

    YTSafetyTaskInfoVO getTaskDetail(@Param("id")Long id);

    List<RiskLevelDictVO>getRiskLevelDict();

    IPage<YtsafetyTaskRiskDataVO> getTaskRiskData(@Param("page") Page<YtsafetyTaskRiskDataVO> page,
                                                  @Param("dto") YtsafetyTaskRiskDataDTO riskDataDTO);

    List<YtsafetyTaskRiskDataVO> queryTaskRiskDataList(@Param("dto") YtsafetyTaskRiskDataDTO riskDataDTO);

    List<String> queryTaskRiskOrgCodeList(@Param("dto") YtsafetyTaskRiskDataDTO riskDataDTO);

//
//    List<YtsafetyTaskUserVO> getSafetyOfficer(
//                                                  @Param("dto") YtsafetyTaskRiskDataDTO riskDataDTO
//                                              );

    void setTaskDormancy(@Param("ids")String[] ids);

    /**
     * 查询任务列表用于导出
     * @param taskInfoDTO 查询条件
     * @return 任务列表
     */
    List<YTSafetyTaskInfoExporVO> queryExportList(@Param("dto") YTSafetyTaskInfoDTO taskInfoDTO);

    void updateTaskStatusPening();

    void updateTaskStatusExpired();

    void updateUserTaskExecutionStatus();

    List<YTSafetyTaskInfoVO> taskAssignment(@Param("taskInfoId") Long taskInfoId);

    List<YTSafetyHoliday> queryHolidayList();

    IPage<YTSafetyMyTaskInfoVO> queryMyTaskList(
                                                @Param("page") Page<YTSafetyMyTaskInfoVO> page,
                                                @Param("userId") String userId,
                                               @Param("empNo") String empNo,
                                               @Param("execScope") String execScope,
                                                @Param("isCompleted") Integer isCompleted,
                                               @Param("startDate") String startDate,
                                               @Param("endDate") String endDate);

    Map getCompletedQuantity(@Param("userId") String userId,@Param("empNo") String empNo,
                                 @Param("execScope") String execScope,@Param("startDate") String startDate,
                             @Param("endDate") String endDate);

    List<String> approvefirefighters(@Param("userId") String userId,
                                     @Param("firefighter") String firefighter);

    List<Map>getDictItem();

    void updateTaskExecutionIsAbnormal(@Param("taskExecutionId") Long taskExecutionId);

    SafetySysRole getSafetySysRole(@Param("safetyOfficer") String safetyOfficer);
}
