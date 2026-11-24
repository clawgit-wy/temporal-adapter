package com.yutong.safety.core.handler.spot;

import com.yutong.safety.common.param.web.spot.PatrolRecordListParam;
import com.yutong.safety.common.param.web.spot.PatrolTaskListParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.web.spot.PatrolRecordVO;
import com.yutong.safety.common.vo.web.spot.PatrolTaskReportDTO;
import com.yutong.safety.common.vo.web.spot.PatrolTaskReportVO;
import com.yutong.safety.core.db.entity.spot.SafetyPatrolPlanDO;
import com.yutong.safety.core.service.spot.SafetyPatrolPlanService;
import com.yutong.safety.core.service.spot.SafetyPatrolRecordService;
import com.yutong.safety.core.service.spot.SafetyPatrolTaskService;
import com.yutong.safety.core.service.spot.SafetySpotCheckTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 治安巡查-任务设置
 * @author gwxiexj
 * @since 2024-05-27 13:36
 */
@Component
public class SafetyPatrolTaskHandler {

    @Autowired
    private SafetyPatrolTaskService safetyPatrolTaskService;

    @Autowired
    private SafetyPatrolRecordService safetyPatrolRecordService;

    @Autowired
    private SafetyPatrolPlanService safetyPatrolPlanService;

    @Autowired
    private SafetySpotCheckTaskService safetySpotCheckTaskService;

    /**
     * 查询统计治安巡查任务
     * @param param 入参
     * @return 返回值
     */
    public List<PatrolTaskReportDTO> selectPatrolTODO(PatrolTaskListParam param) {
        List<PatrolTaskReportDTO> reportDTOS = new ArrayList<>();
        String patrolEmpNo = param.getPatrolEmpNo();
        if(StringUtils.isEmpty(patrolEmpNo)){
            LogUtils.warn("当前登录人员工号为空");
            return reportDTOS;
        }
        //01开头的员工号转为60开头的新员工号
        if(patrolEmpNo.startsWith("01")){
            //patrolEmpNo = safetySpotCheckTaskService.employeeConvertWorkNo(patrolEmpNo);
            //param.setPatrolEmpNo(patrolEmpNo);
        }
        //增加planId过滤条件
        List<SafetyPatrolPlanDO> patrolPlanDOS = safetyPatrolPlanService.selectPlanListByEmpNo(patrolEmpNo);
        List<Integer> planIds = new ArrayList<>();
        if(CollectionUtils.isEmpty(patrolPlanDOS)){
            planIds.add(0);
        }else{
            planIds = patrolPlanDOS.stream().map(SafetyPatrolPlanDO::getId).collect(Collectors.toList());
        }
        param.setPlanIdList(planIds);

        List<PatrolTaskReportVO> reportVOS = safetyPatrolTaskService.selectPatrolTaskReport(param);
        if(CollectionUtils.isEmpty(reportVOS)){
            return reportDTOS;
        }
        //获取各厂区完成情况，在进行汇总
        List<PatrolTaskReportVO> detailVOS = safetyPatrolTaskService.selectDetailSummary(param);

        Map<String, Integer> detailCheckMap = new HashMap<>();
        Map<String, Integer> detailPlanCheckMap = reportVOS.stream().collect(Collectors.groupingBy(PatrolTaskReportVO::getTypeName,Collectors.summingInt(PatrolTaskReportVO::getFrequency)));

        Map<Integer, Integer> planCheckMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(detailVOS)){
            detailCheckMap = detailVOS.stream().filter(vo -> !StringUtils.isEmpty(vo.getTypeName())).collect(Collectors.groupingBy(PatrolTaskReportVO::getTypeName,Collectors.summingInt(PatrolTaskReportVO::getCheckNum)));
            planCheckMap = detailVOS.stream().filter(vo -> vo.getPlanId() != null).collect(Collectors.groupingBy(PatrolTaskReportVO::getPlanId,Collectors.summingInt(PatrolTaskReportVO::getCheckNum)));
        }
        //根据治安巡查的类型分组
        Map<String,List<PatrolTaskReportVO>> voMap = reportVOS.stream().collect(Collectors.groupingBy(PatrolTaskReportVO::getTypeName));

        for(Map.Entry<String, List<PatrolTaskReportVO>> entry:voMap.entrySet()){
            String typeName = entry.getKey();
            Integer checkNum = detailCheckMap.get(typeName);
            Integer planNum = detailPlanCheckMap.get(typeName);
            if(checkNum == null){
                checkNum = 0;
            }
            if(planNum != null){
                //计算完成率
                String rate = checkNum*100/planNum + "%";

                PatrolTaskReportDTO curDto = new PatrolTaskReportDTO();
                curDto.setTeamName(typeName);
                List<PatrolTaskReportVO> reportVOList = entry.getValue();
                Map<Integer, Integer> finalPlanCheckMap = planCheckMap;
                reportVOList.forEach(element->{
                    Integer planId = element.getPlanId();
                    Integer curCheck = finalPlanCheckMap.get(planId);
                    Integer curPlan = element.getFrequency();
                    element.setCheckNum(curCheck==null?0:curCheck);
                    element.setUnCheckNum(curCheck==null?curPlan:curPlan-curCheck);
                });
                //根据神域数量倒叙排序
                if(!CollectionUtils.isEmpty(reportVOList)){
                    reportVOList.stream().sorted(Comparator.comparing(PatrolTaskReportVO::getUnCheckNum).reversed());
                }
                curDto.setSubList(reportVOList);
                curDto.setRate(rate);

                reportDTOS.add(curDto);
            }else{
                LogUtils.error("异常typeName="+typeName+"计划数量="+planNum);
            }
        }
        return reportDTOS;
    }

    /**
     * 查询统计治安巡查任务
     * @param param 入参
     * @return 返回值
     */
    public List<PatrolTaskReportDTO> selectPatrolTODODetail(PatrolTaskListParam param) {
        List<PatrolTaskReportDTO> reportDTOS = new ArrayList<>();
        String patrolEmpNo = param.getPatrolEmpNo();
        if(StringUtils.isEmpty(patrolEmpNo)){
            LogUtils.warn("当前登录人员工号为空");
            return reportDTOS;
        }
        //01开头的员工号转为60开头的新员工号
        if(patrolEmpNo.startsWith("01")){
            String newPatrolEmpNo = safetySpotCheckTaskService.employeeConvertWorkNo(patrolEmpNo);
            param.setPatrolEmpNo(newPatrolEmpNo);
        }
        //查询各任务的统计情况
        List<PatrolTaskReportVO> reportVOS = safetyPatrolTaskService.selectPatrolTODODetail(param);
        if(CollectionUtils.isEmpty(reportVOS)){
            return reportDTOS;
        }
        String taskIdStr = reportVOS.stream().map(PatrolTaskReportVO::getTaskId).map(Objects::toString).collect(Collectors.joining(","));
        //获取相同检索条件下的已验证的信息
        PatrolRecordListParam recordListParam = new PatrolRecordListParam();
        recordListParam.setEventSource(1); //只查询周期类
        recordListParam.setTaskIdStr(taskIdStr);
        recordListParam.setFactoryIdStr(param.getFactoryStr());
        recordListParam.setFactoryId(StringUtils.isEmpty(param.getFactoryId())?null:Integer.valueOf(param.getFactoryId()));
        List<PatrolRecordVO> recordList = safetyPatrolRecordService.getList(recordListParam);
        Map<Integer,List<PatrolRecordVO>> recordMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(recordList)){
            recordMap = recordList.stream().collect(Collectors.groupingBy(PatrolRecordVO::getTaskId));
        }
        //根据teamName进行分组
        Map<String,List<PatrolTaskReportVO>> voMap = reportVOS.stream().collect(Collectors.groupingBy(PatrolTaskReportVO::getTeamName));
        for(Map.Entry<String, List<PatrolTaskReportVO>> entry:voMap.entrySet()){
            PatrolTaskReportDTO curDto = new PatrolTaskReportDTO();
            curDto.setTeamName(entry.getKey());
            List<PatrolTaskReportVO> curReportDTOList = entry.getValue();
            //处理taskID的已巡检项
            for(PatrolTaskReportVO reportVO:curReportDTOList){
                List<PatrolRecordVO> rlist = recordMap.get(reportVO.getTaskId());
                reportVO.setRecordList(CollectionUtils.isEmpty(rlist)?new ArrayList<>():rlist);
            }
            //根据神域数量倒叙排序
            if(!CollectionUtils.isEmpty(curReportDTOList)){
                curReportDTOList.stream().sorted(Comparator.comparing(PatrolTaskReportVO::getUnCheckNum).reversed());
            }
            curDto.setSubList(curReportDTOList);
            reportDTOS.add(curDto);
        }
        return reportDTOS;
    }

}
