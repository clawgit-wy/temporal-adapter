package com.yutong.modules.risk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yutong.modules.common.entity.YTSafetyFileInfo;
import com.yutong.modules.risk.api.YTSafetyTaskExecutionService;
import com.yutong.modules.risk.api.YTSafetyTaskService;
import com.yutong.modules.risk.dto.YTSafetyTaskExecutionDTO;
import com.yutong.modules.risk.entity.YTSafetyTaskExecution;
import com.yutong.modules.risk.enums.CustomConstant;
import com.yutong.modules.risk.mapper.YTSafetyTaskExecutionMapper;
import com.yutong.modules.risk.mapper.YTSafetyTaskInfoMapper;
import com.yutong.modules.risk.vo.PatrolTaskGroupVO;
import com.yutong.modules.risk.vo.YTSafetyTaskExecutionVO;
import com.yutong.modules.risk.vo.YTSafetyTaskExecutionXCRWExportVO;
import com.yutong.modules.risk.vo.YTSafetyTaskExecutionZXRWExportVO;
import com.yutong.modules.risk.vo.YTSafetyTaskInfoVO;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class YTSafetyTaskExecutionServiceImpl extends ServiceImpl<YTSafetyTaskExecutionMapper, YTSafetyTaskExecution> implements YTSafetyTaskExecutionService {
    
    @Resource
    private YTSafetyTaskExecutionMapper taskExecutionMapper;

    @Resource
    private YTSafetyTaskService taskService;

    @Resource
    private YTSafetyTaskInfoMapper taskInfoMapper;

    @Value("${ytsafety.excel.exportUrl}")
    private String exportUrl;
    
    @Override
    public IPage<YTSafetyTaskExecutionVO> queryPage(Page<YTSafetyTaskExecutionVO> page, YTSafetyTaskExecutionDTO taskExecutionDTO) {
        IPage<YTSafetyTaskExecutionVO> ytSafetyTaskExecutionVOIPage = taskExecutionMapper.queryPage(page, taskExecutionDTO);
        List<Map> safetyDictItems = taskInfoMapper.getDictItem();

        for (YTSafetyTaskExecutionVO vo : ytSafetyTaskExecutionVOIPage.getRecords()){
            vo.setCompleteInterval(vo.getCompleteInterval() == null ? "-" : vo.getCompleteInterval() + "分钟");
            if (vo.getTaskStatus().equals(CustomConstant.TaskExecutionStatus.COMPLETED)){
                vo.setTaskStatus("已完成");
            }else if (vo.getTaskStatus().equals(CustomConstant.TaskExecutionStatus.RUNNING)){
                vo.setTaskStatus("执行中");
            }else if(vo.getTaskStatus().equals(CustomConstant.TaskExecutionStatus.OVERDUE_COMPLETED)){
                vo.setTaskStatus("超期完成");
            }else if(vo.getTaskStatus().equals(CustomConstant.TaskExecutionStatus.OVERDUE_UNCOMPLETED)){
                vo.setTaskStatus("超期未完成");
            }else if(vo.getTaskStatus().equals(CustomConstant.TaskStatus.APPROVAL)){
                vo.setTaskStatus("审批中");
            }else if(vo.getTaskStatus().equals(CustomConstant.TaskStatus.REJECT)){
                vo.setTaskStatus("已驳回");
            }else if(vo.getTaskStatus().equals(CustomConstant.TaskStatus.DISCARDED)){
                vo.setTaskStatus("已废弃");
            }

            if(StringUtils.isNotBlank(vo.getRiskCategoryId())){
                if(vo.getRiskCategoryId().contains(",")){
                    vo.setRiskCategoryId("治安,消防");
                }else{
                    safetyDictItems.stream().filter(item -> String.valueOf(item.get("id")).equals(vo.getRiskCategoryId()))
                            .findFirst().ifPresent(item -> vo.setRiskCategoryId((String) item.get("itemName")));
                }
            }else{
                vo.setRiskCategoryId("-");
            }
        }
        return ytSafetyTaskExecutionVOIPage;
    }

    @Override
    public List<YTSafetyTaskExecutionXCRWExportVO> exportPatrolMissionQueryPage(HttpServletRequest request, YTSafetyTaskExecutionDTO taskExecutionDTO) {
        List<YTSafetyTaskExecutionXCRWExportVO> ytSafetyTaskExecutionVOS = taskExecutionMapper.exportPatrolMissionQueryPage(taskExecutionDTO);
        List<Map> safetyDictItems = taskInfoMapper.getDictItem();
        for (YTSafetyTaskExecutionXCRWExportVO vo : ytSafetyTaskExecutionVOS){
            List<YTSafetyFileInfo> imgFiles = vo.getAttachmentImgList();
            if (imgFiles != null && !imgFiles.isEmpty()) {
                String attachmentImgIds = imgFiles.stream()
                        .map(YTSafetyFileInfo::getFilePath)
                        .filter(filePath -> filePath != null && !filePath.isEmpty())
                        .collect(Collectors.joining(","));
                vo.setAttachmentImgId(attachmentImgIds);
            }
            vo.setCompleteInterval(vo.getCompleteInterval() == null ? "-" : vo.getCompleteInterval() + "分钟");
            if(1 == vo.getIsAbnormal()){
                vo.setIsAbnormalStr("是");
            }else if(0 == vo.getIsAbnormal()){
                vo.setIsAbnormalStr("否");
            }
            if (vo.getTaskStatus().equals(CustomConstant.TaskExecutionStatus.COMPLETED)){
                vo.setTaskStatus("已完成");
            }else if (vo.getTaskStatus().equals(CustomConstant.TaskExecutionStatus.RUNNING)){
                vo.setTaskStatus("执行中");
            }else if(vo.getTaskStatus().equals(CustomConstant.TaskExecutionStatus.OVERDUE_COMPLETED)){
                vo.setTaskStatus("超期完成");
            }else if(vo.getTaskStatus().equals(CustomConstant.TaskExecutionStatus.OVERDUE_UNCOMPLETED)){
                vo.setTaskStatus("超期未完成");
            }

            if(StringUtils.isNotBlank(vo.getRiskCategoryId())){
                if(vo.getRiskCategoryId().contains(",")){
                    vo.setRiskCategoryId("治安,消防");
                }else{
                    safetyDictItems.stream().filter(item -> String.valueOf(item.get("id")).equals(vo.getRiskCategoryId()))
                            .findFirst().ifPresent(item -> vo.setRiskCategoryId((String) item.get("itemName")));
                }
            }else{
                vo.setRiskCategoryId("-");
            }
            vo.setDetailUrl(exportUrl +"patroltaskdetail?id="+vo.getId());
        }

        return ytSafetyTaskExecutionVOS;
    }

    @Override
    public List<YTSafetyTaskExecutionZXRWExportVO> exportSpecialTaskQueryPage(HttpServletRequest request, YTSafetyTaskExecutionDTO taskExecutionDTO) {
        List<YTSafetyTaskExecutionZXRWExportVO> ytSafetyTaskExecutionVOS = taskExecutionMapper.exportSpecialTaskQueryPage(taskExecutionDTO);
        for (YTSafetyTaskExecutionZXRWExportVO vo : ytSafetyTaskExecutionVOS){
            List<YTSafetyFileInfo> imgFiles = vo.getAttachmentImgList();
            if (imgFiles != null && !imgFiles.isEmpty()) {
                String attachmentImgIds = imgFiles.stream()
                        .map(YTSafetyFileInfo::getFilePath)
                        .filter(filePath -> filePath != null && !filePath.isEmpty())
                        .collect(Collectors.joining(","));
                vo.setAttachmentImgId(attachmentImgIds);
            }
            List<YTSafetyFileInfo> files = vo.getAttachmentList();
            if (files != null && !files.isEmpty()) {
                String attachmentIds = files.stream()
                        .map(YTSafetyFileInfo::getFilePath)
                        .filter(filePath -> filePath != null && !filePath.isEmpty())
                        .collect(Collectors.joining(","));
                vo.setAttachmentId(attachmentIds);
            }
            if (vo.getTaskStatus().equals(CustomConstant.TaskExecutionStatus.COMPLETED)){
                vo.setTaskStatus("已完成");
            }else if (vo.getTaskStatus().equals(CustomConstant.TaskExecutionStatus.RUNNING)){
                vo.setTaskStatus("执行中");
            }else if(vo.getTaskStatus().equals(CustomConstant.TaskExecutionStatus.OVERDUE_COMPLETED)){
                vo.setTaskStatus("超期完成");
            }else if(vo.getTaskStatus().equals(CustomConstant.TaskExecutionStatus.OVERDUE_UNCOMPLETED)){
                vo.setTaskStatus("超期未完成");
            }else if(vo.getTaskStatus().equals(CustomConstant.TaskStatus.APPROVAL)){
                vo.setTaskStatus("审批中");
            }else if(vo.getTaskStatus().equals(CustomConstant.TaskStatus.REJECT)){
                vo.setTaskStatus("已驳回");
            }else if(vo.getTaskStatus().equals(CustomConstant.TaskStatus.DISCARDED)){
                vo.setTaskStatus("已废弃");
            }

            vo.setDetailUrl(exportUrl +"specialtaskdetail?id="+vo.getId());

        }

        return ytSafetyTaskExecutionVOS;
    }

    @Override
    public List<YTSafetyTaskExecutionVO> getMyTaskList(String userId) {

        List<YTSafetyTaskExecution> taskExecutions = taskExecutionMapper.selectList(
                new LambdaQueryWrapper<YTSafetyTaskExecution>().eq(YTSafetyTaskExecution::getExecUserId, userId));

        if (taskExecutions != null && !taskExecutions.isEmpty()) {

        }
        return Collections.emptyList();
    }

    @Override
    public List<PatrolTaskGroupVO> selectPatrolToDo(Long execUserId) {
        List<PatrolTaskGroupVO> list = taskExecutionMapper.selectPatrolToDo(execUserId);
        // 计算完成率
        for (PatrolTaskGroupVO vo : list) {
            if (vo.getTotalCount() != null && vo.getTotalCount() > 0) {
                int rate = vo.getCompletedCount() * 100 / vo.getTotalCount();
                vo.setRate(rate + "%");
            } else {
                vo.setRate("0%");
            }
        }
        return list;
    }
}
