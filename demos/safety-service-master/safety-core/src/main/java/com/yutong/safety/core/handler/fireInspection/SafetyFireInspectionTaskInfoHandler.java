package com.yutong.safety.core.handler.fireInspection;

import com.yutong.safety.common.constants.FireInspectionEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.YxtRebotUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyGroupInfoDO;
import com.yutong.safety.core.db.entity.dict.SafetySystemParamDO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionStandardDO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionTaskInfoDO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionTaskInfoDetailDO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionTaskSetDO;
import com.yutong.safety.core.db.entity.fireInspection.excel.SafetyFireInspectionTaskInfoExcel;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskInfoQueryParams;
import com.yutong.safety.core.dto.fireInspection.FireInspectionEmpInfoDto;
import com.yutong.safety.core.dto.fireInspection.FireInspectionTaskInfoCountDto;
import com.yutong.safety.core.dto.fireInspection.FireInspectionTaskInfoDto;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.SafetyGroupService;
import com.yutong.safety.core.service.dict.SafetySystemParamService;
import com.yutong.safety.core.service.fireInspection.SafetyFireInspectionStandardService;
import com.yutong.safety.core.service.fireInspection.SafetyFireInspectionTaskInfoDetailService;
import com.yutong.safety.core.service.fireInspection.SafetyFireInspectionTaskInfoService;
import com.yutong.safety.core.service.fireInspection.SafetyFireInspectionTaskSetService;
import com.yutong.safety.core.service.spot.SafetySpotCheckTaskService;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yutong.safety.common.constants.Constants.FILE_SIZE;
import static com.yutong.safety.core.db.entity.fireInspection.common.FireInspectionConstants.*;

@Component
public class SafetyFireInspectionTaskInfoHandler {

    @Value("${iam.retention.robotId}")
    private String incomintRebotId;

    @Value("${eip.approveFlow}")
    private String approveFlowUrl;

    @Resource
    private SafetyGroupService safetyGroupService;
    @Resource
    private SafetySpotCheckTaskService safetySpotCheckTaskService;
    @Resource
    private YxtRebotUtils yxtRebotUtils;
    @Resource
    private SafetyFireInspectionTaskInfoService safetyFireInspectionTaskInfoService;
    @Resource
    private SafetyFireInspectionTaskInfoDetailService safetyFireInspectionTaskInfoDetailService;
    @Resource
    private SafetyFireInspectionTaskSetService safetyFireInspectionTaskSetService;
    @Resource
    private SafetyFireInspectionStandardService safetyFireInspectionStandardService;
    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Resource
    private SafetySystemParamService safetySystemParamService;

    /**
     * 分页数据获取
     *
     * @param param 入参
     * @return 返回符合条件的数据
     */
    public PageVO<SafetyFireInspectionTaskInfoDO> selectPageList(SafetyFireInspectionTaskInfoQueryParams param) {
        // 当前部门的所有子部门
        List<String> empCodeList = safetyGroupService.getEmpCodeList(param.getWorkDeptCode());
        // 获取数据
        long total = safetyFireInspectionTaskInfoService.selectCount(param, empCodeList);
        List<SafetyFireInspectionTaskInfoDO> list = safetyFireInspectionTaskInfoService.selectList(param, empCodeList, true);
        if (!CollectionUtils.isEmpty(list)) {
            List<String> deptCode = list.stream().map(SafetyFireInspectionTaskInfoDO::getWorkDeptCode).collect(Collectors.toList());
            List<SafetyGroupInfoDO> listByCode = safetyGroupService.getListByCode(deptCode);
            Map<String, SafetyGroupInfoDO> deptMap = listByCode.stream().collect(Collectors.toMap(SafetyGroupInfoDO::getCode, t -> t));
            list.forEach(li -> {
                SafetyGroupInfoDO infoDO = deptMap.get(li.getWorkDeptCode());
                if (infoDO != null) {
                    li.setOrgNamePath(infoDO.getOrgNamePath());
                }
                FireInspectionEnum fireInspectionEnum = FireInspectionEnum.getFireInspectionEnumByCode(li.getRiskLevel());
                li.setRiskLevel(fireInspectionEnum.getRiskLevelDetail());
                li.setInspectionCycle(fireInspectionEnum.getInspectionCycle());
                li.setInspectionLevel(fireInspectionEnum.getInspectionLevel());
            });
        }
        return new PageVO<>(total, list);
    }


    /**
     * 手机端接口
     */
    public PageVO<FireInspectionTaskInfoDto> getList(SafetyFireInspectionTaskInfoQueryParams param) {
        String empNo = param.getEmpNo();
        if (StringUtils.isBlank(empNo)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "巡检人信息为空");
        }
        if(empNo.startsWith("01")){
            String newPatrolEmpNo = safetySpotCheckTaskService.employeeConvertWorkNo(empNo);
            param.setEmpNo(newPatrolEmpNo);
        }
        if (param.getTaskStatusNum() == null || param.getTaskStatusNum() == 0) {
            param.setTaskStatus(FIRE_TASK_INCOMPLETE);
        } else {
            param.setTaskStatus(FIRE_TASK_COMPLETED);
        }
        if (StringUtils.isBlank(param.getTaskCycleBeginDate())) {
            // 获取本周的周一 和 周日
            LocalDate today = LocalDate.now();
            LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            param.setTaskCycleBeginDate(monday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            param.setTaskCycleEndDate(sunday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        List<FireInspectionTaskInfoDto> list = new ArrayList<>();
        // 获取任务信息
        long total = safetyFireInspectionTaskInfoService.selectCount(param, Lists.newArrayList());
        List<SafetyFireInspectionTaskInfoDO> taskInfoDOS = safetyFireInspectionTaskInfoService.selectList(param, Lists.newArrayList(), false);
        if (!CollectionUtils.isEmpty(taskInfoDOS)) {
            // 获取没个任务信息的任务详情数据量
            List<Long> idList = taskInfoDOS.stream().map(SafetyFireInspectionTaskInfoDO::getId).collect(Collectors.toList());
            List<FireInspectionTaskInfoCountDto> countDtos = safetyFireInspectionTaskInfoDetailService.selectCount(idList);
            Map<Long, FireInspectionTaskInfoCountDto> dtaMap = countDtos.stream().collect(Collectors.toMap(FireInspectionTaskInfoCountDto::getInspectionTaskId, t -> t));
            List<String> deptCode = taskInfoDOS.stream().map(SafetyFireInspectionTaskInfoDO::getWorkDeptCode).collect(Collectors.toList());
            List<SafetyGroupInfoDO> listByCode = safetyGroupService.getListByCode(deptCode);
            Map<String, SafetyGroupInfoDO> deptMap = listByCode.stream().collect(Collectors.toMap(SafetyGroupInfoDO::getCode, t -> t));
            // 赋值
            taskInfoDOS.forEach(li -> {
                FireInspectionEnum fireInspectionEnum = FireInspectionEnum.getFireInspectionEnumByCode(li.getRiskLevel());
                li.setRiskLevel(fireInspectionEnum.getRiskLevelDetail());
                li.setInspectionCycle(fireInspectionEnum.getInspectionCycle());
                li.setInspectionLevel(fireInspectionEnum.getInspectionLevel());
                Long id = li.getId();
                SafetyGroupInfoDO infoDO = deptMap.get(li.getWorkDeptCode());
                if (infoDO != null) {
                    li.setOrgNamePath(infoDO.getOrgNamePath());
                }
                FireInspectionTaskInfoCountDto dto = dtaMap.get(id);
                FireInspectionTaskInfoDto taskInfoDO = new FireInspectionTaskInfoDto();
                BeanUtils.copyProperties(li, taskInfoDO);
                taskInfoDO.initCount(dto);
                taskInfoDO.setTaskColor(fireInspectionEnum.getColor());
                list.add(taskInfoDO);

            });
        }
        return new PageVO<>(total, list);
    }


    /**
     * 数据统计
     *
     * @param param 入参
     * @return 返回完成的数量和未完成的数量
     */
    public FireInspectionTaskInfoCountDto getTaskNum(SafetyFireInspectionTaskInfoQueryParams param) {
        String empNo = param.getEmpNo();
        if (StringUtils.isBlank(empNo)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "巡检人信息为空");
        }
        if(empNo.startsWith("01")){
            String newPatrolEmpNo = safetySpotCheckTaskService.employeeConvertWorkNo(empNo);
            param.setEmpNo(newPatrolEmpNo);
        }
        if (StringUtils.isBlank(param.getTaskCycleBeginDate())) {
            // 获取本周的周一 和 周日
            LocalDate today = LocalDate.now();
            LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            param.setTaskCycleBeginDate(monday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            param.setTaskCycleEndDate(sunday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        return safetyFireInspectionTaskInfoService.selectTaskCount(param);
    }

    /**
     * 数据导出
     *
     * @param param 参数 （获取数据的条件）
     */
    public void export(HttpServletResponse response, SafetyFireInspectionTaskInfoQueryParams param) throws IOException {
        String fileName = new String(("任务详情-" + DateUtils.format(new Date(), 5) + ".xlsx").getBytes(), "ISO8859-1");
        // 当前部门的所有子部门
        List<String> empCodeList = safetyGroupService.getEmpCodeList(param.getWorkDeptCode());
        if (!CollectionUtils.isEmpty(empCodeList)) {
            param.setWorkDeptCodes(empCodeList);
        }
        List<SafetyFireInspectionTaskInfoExcel> list = safetyFireInspectionTaskInfoService.selectDownloadList(param);
        if (list.size() > FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), String.format("导出数据不能超过 %s 条！", FILE_SIZE));
        }
        Map<String, SafetyGroupInfoDO> deptMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(list)) {
            List<String> deptCode = list.stream().map(SafetyFireInspectionTaskInfoExcel::getWorkDeptCode).collect(Collectors.toList());
            List<SafetyGroupInfoDO> listByCode = safetyGroupService.getListByCode(deptCode);
            deptMap = listByCode.stream().collect(Collectors.toMap(SafetyGroupInfoDO::getCode, t -> t));
        }
        String[] title = {"任务名称", "作业环节名称", "作业部门", "风险等级", "检查级别", "检查周期", "任务周期", "任务下发时间",
                "巡检人", "所属部门", "任务状态", "任务完成时间", "检查结果", "整改结果", "检查编码", "检查项目", "检查类别", "检查细分维度",
                "检查内容", "检查方法", "检查结果", "检查时间", "备注"};

        String[][] values = new String[list.size()][title.length];
        for (int i = 0; i < list.size(); i++) {
            SafetyFireInspectionTaskInfoExcel info = list.get(i);
            FireInspectionEnum fireInspectionEnum = FireInspectionEnum.getFireInspectionEnumByCode(info.getRiskLevel());
            int j = 0;
            values[i][j++] = info.getTaskName();
            values[i][j++] = info.getWorkStepName();
            SafetyGroupInfoDO infoDO = deptMap.get(info.getWorkDeptCode());
            if (infoDO != null) {
                values[i][j++] = getOrgNamePath(infoDO.getOrgNamePath());
            } else {
                values[i][j++] = info.getWorkDeptName();
            }
            values[i][j++] = fireInspectionEnum.getRiskLevelDetail();
            values[i][j++] = fireInspectionEnum.getInspectionLevel();
            values[i][j++] = fireInspectionEnum.getInspectionCycle();
            values[i][j++] = info.getTaskCycleBeginDate() + "~" + info.getTaskCycleEndDate();
            values[i][j++] = DateUtils.format(info.getTaskIssuedTime(), 2);
            values[i][j++] = info.getEmpName() + "（" + info.getEmpNo() + "）";
            values[i][j++] = getOrgNamePath(info.getEmpOrgNamePath());
            values[i][j++] = info.getTaskStatus();
            values[i][j++] = info.getTaskCompleteTime() == null ? "" : DateUtils.format(info.getTaskCompleteTime(), 2);
            values[i][j++] = info.getInspectionResult();
            values[i][j++] = info.getRectificationResult();
            values[i][j++] = info.getInspectionCode();
            values[i][j++] = info.getInspectionItem();
            values[i][j++] = info.getInspectionCategory();
            values[i][j++] = info.getInspectionSubDimension();
            values[i][j++] = info.getInspectionContent();
            values[i][j++] = info.getInspectionMethod();
            values[i][j++] = info.getInspectionResultDetail();
            values[i][j++] = info.getInspectionTime() == null ? "" : DateUtils.format(info.getInspectionTime(), 2);
            values[i][j++] = info.getProcessId();
        }

        String sheetName = "任务详情";
        HSSFWorkbook hssfWorkbook = ExcelUtils.getHSSFWorkbook(sheetName, title, values);
        response.setContentType("application/octet-stream;charset=ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        OutputStream os = response.getOutputStream();
        hssfWorkbook.write(os);
        os.flush();
        os.close();
    }

    private String getOrgNamePath(String oldOrgNamePath) {
        if (StringUtils.isBlank(oldOrgNamePath)) {
            return "";
        }
        List<String> orgNamePaths = Arrays.asList(oldOrgNamePath.split(","));
        Collections.reverse(orgNamePaths);
        return String.join("<", orgNamePaths);
    }

    /**
     * 定时下发巡检任务
     */
    public void autoInspectionTask() {
        // 1.获取当前自然周，判断今天是否需要下发巡检任务，巡检任务的下发周期为自然周的周一和周四
        LocalDate today = LocalDate.now();
        int value = today.getDayOfWeek().getValue();
        if (value != 1 && value != 4) {
            return;
        }

        // 2.获取需要下发巡检任务的数据 任务周期包含今天的，并且状态是正常的，数据未删除的数据
        // 判断检查周期 周四时只获取检查周期为每周两次的，即风险等级为蓝的 周一获取所有的
        String riskLevelInfo = (value == 4) ? FireInspectionEnum.BLUE.getCode() : "";
        List<SafetyFireInspectionTaskSetDO> dataList = safetyFireInspectionTaskSetService.selectList(riskLevelInfo);
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        List<String> workStepCodes = dataList.stream().map(SafetyFireInspectionTaskSetDO::getWorkStepCode).distinct().collect(Collectors.toList());

        // 4.获取检查标准信息
        List<SafetyFireInspectionStandardDO> standardDOS = safetyFireInspectionStandardService.selectList(workStepCodes);
        if (CollectionUtils.isEmpty(standardDOS)) {
            return;
        }
        Map<String, List<SafetyFireInspectionStandardDO>> stringListMap = standardDOS.stream().collect(Collectors.groupingBy(SafetyFireInspectionStandardDO::getWorkStepCode));
        // 4.1 过滤掉没有检查标准的数据
        Set<String> workStepList = standardDOS.stream().map(SafetyFireInspectionStandardDO::getWorkStepCode).collect(Collectors.toSet());
        dataList = dataList.stream().filter(li -> workStepList.contains(li.getWorkStepCode())).collect(Collectors.toList());

        // 5.获取默认任务对象信息， 根据任务对象类型字段获取默认对象数据的作业部门
        List<String> workDeptCodes = dataList.stream().filter(li -> TASK_OBJECT_TYPE_DEFAULT.equals(li.getTaskObjectType())).map(SafetyFireInspectionTaskSetDO::getWorkDeptCode).distinct().collect(Collectors.toList());
        Map<String, List<SafetyEmployeeMessageDO>> orgCodeMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(workDeptCodes)) {
            List<SafetyEmployeeMessageDO> empInfoList = safetyEmployeeMessageService.selectInfoByDeptPosition(workDeptCodes);
            orgCodeMap = empInfoList.stream().collect(Collectors.groupingBy(SafetyEmployeeMessageDO::getOrgCode));
        }

        // 任务创建
        createAndSaveTasks(value, dataList, orgCodeMap, stringListMap);
    }

    /**
     * 任务创建
     * @param value 周期
     * @param dataList 任务信息
     * @param orgCodeMap 组织信息
     * @param standardMap 检查标准
     */
    private void createAndSaveTasks(int value,
                                    List<SafetyFireInspectionTaskSetDO> dataList,
                                    Map<String, List<SafetyEmployeeMessageDO>> orgCodeMap,
                                    Map<String, List<SafetyFireInspectionStandardDO>> standardMap) {
        // 自定义对象
        Map<String, FireInspectionEmpInfoDto> taskEmpMap = getTaskEmpInfo(dataList);

        List<SafetyFireInspectionTaskInfoDO> taskInfoDOS = new ArrayList<>();
        List<SafetyFireInspectionTaskInfoDetailDO> taskInfoDetailDOS = new ArrayList<>();
        for (SafetyFireInspectionTaskSetDO taskSetDO : dataList) {
            // 任务对象  任务对象分为默认和自定义；默认直接取，自定义需要获取
            List<FireInspectionEmpInfoDto> empNoList = new ArrayList<>();
            Long taskObjectType = taskSetDO.getTaskObjectType();
            // 过滤掉不符合条件的数据
            if (shouldSkipTask(taskSetDO, orgCodeMap, taskEmpMap)) {
                continue;
            }
            // 自定义对象
            if (taskObjectType.equals(TASK_OBJECT_TYPE_CUSTOMIZE)) {
                List<String> list = Arrays.asList(taskSetDO.getEmpNo().split(","));
                empNoList = list.stream().map(taskEmpMap::get).filter(Objects::nonNull).collect(Collectors.toList());
            }
            // 默认对象
            if (taskObjectType.equals(TASK_OBJECT_TYPE_DEFAULT)) {
                empNoList = getDefaultEmpList(taskSetDO, orgCodeMap);
            }
            // 任务对象不为空，才继续进行
            if (CollectionUtils.isEmpty(empNoList)) {
                continue;
            }

            empNoList.forEach(emp -> {
                // 主数据
                SafetyFireInspectionTaskInfoDO taskInfo = getTaskInfo(taskSetDO, emp, value);
                // 明细数据
                String riskLevel = taskSetDO.getRiskLevel();
                List<SafetyFireInspectionStandardDO> list = standardMap.get(taskSetDO.getWorkStepCode());
                List<SafetyFireInspectionTaskInfoDetailDO> detailDOList = list.stream().filter(ll -> checkRiskLevel(ll.getRiskLevel(), riskLevel)).map(t -> getTaskInfoDetail(t, taskInfo.getTemporaryId())).collect(Collectors.toList());
                // 存在明细数据，才创建具体的任务
                if (!CollectionUtils.isEmpty(detailDOList)) {
                    taskInfoDOS.add(taskInfo);
                    taskInfoDetailDOS.addAll(detailDOList);
                }
            });
        }
        if (!CollectionUtils.isEmpty(taskInfoDOS)) {
            safetyFireInspectionTaskInfoService.batchSave(taskInfoDOS);
            Map<String, Long> dataMap = taskInfoDOS.stream().collect(Collectors.toMap(SafetyFireInspectionTaskInfoDO::getTemporaryId, SafetyFireInspectionTaskInfoDO::getId));
            taskInfoDetailDOS.forEach(li -> li.setInspectionTaskId(dataMap.get(li.getTemporaryId())));
            safetyFireInspectionTaskInfoDetailService.batchSave(taskInfoDetailDOS);

            // 发送消息
            SafetySystemParamDO systemParamDO = safetySystemParamService.getByParamCode(SEND_MESSAGE_OFF);
            if (systemParamDO != null && "1".equals(systemParamDO.getParamValue())) {
                String msg = "【消防巡检】您有一条新消防巡检任务-%s，请于%s~%s完成";
                taskInfoDOS.forEach(li -> yxtRebotUtils.postForEntity(null, incomintRebotId, li.getEmpNo(), String.format(msg, li.getTaskName(), DateUtils.format(DateUtils.parse(li.getTaskCycleBeginDate(), 1), 9), DateUtils.format(DateUtils.parse(li.getTaskCycleEndDate(), 1), 9)), new RestTemplate()));
            }
        }
    }

    private boolean shouldSkipTask(SafetyFireInspectionTaskSetDO taskSetDO,
                                   Map<String, List<SafetyEmployeeMessageDO>> orgCodeMap,
                                   Map<String, FireInspectionEmpInfoDto> taskEmpMap) {
        Long taskObjectType = taskSetDO.getTaskObjectType();
        if (taskObjectType.equals(TASK_OBJECT_TYPE_CUSTOMIZE)) {
            return StringUtils.isBlank(taskSetDO.getEmpNo()) || taskEmpMap.isEmpty();
        } else if (TASK_OBJECT_TYPE_DEFAULT.equals(taskObjectType)) {
            return orgCodeMap.isEmpty();
        }
        return false;
    }

    /**
     * 默认对象
     */
    private List<FireInspectionEmpInfoDto> getDefaultEmpList(SafetyFireInspectionTaskSetDO taskSetDO,
                                                             Map<String, List<SafetyEmployeeMessageDO>> orgCodeMap) {
        // 获取风险等级对应的岗位信息
        String inspectionPosition = FireInspectionEnum.getFireInspectionEnumByCode(taskSetDO.getRiskLevel()).getInspectionPosition();
        List<String> position = Arrays.asList(inspectionPosition.split("/"));
        // 获取当前部门的所有人员
        List<SafetyEmployeeMessageDO> departmentEmployees = orgCodeMap.get(taskSetDO.getWorkDeptCode());
        if (CollectionUtils.isEmpty(departmentEmployees)) {
            return Collections.emptyList();
        }

        // 过滤掉不属于当前风险等级的人员
        List<SafetyEmployeeMessageDO> filteredEmployees = departmentEmployees.stream()
                .filter(emp -> position.stream().anyMatch(emp.getPositionName()::contains))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filteredEmployees)) {
            return Collections.emptyList();
        }

        return filteredEmployees.stream()
                .map(source -> {
                    FireInspectionEmpInfoDto dto = new FireInspectionEmpInfoDto();
                    BeanUtils.copyProperties(source, dto);
                    return dto;
                }).collect(Collectors.toList());
    }

    /**
     * 自定义值获取员工信息
     * @param dataList
     */
    private Map<String, FireInspectionEmpInfoDto> getTaskEmpInfo(List<SafetyFireInspectionTaskSetDO> dataList) {

        Set<String> list = new HashSet<>();
        for (SafetyFireInspectionTaskSetDO li : dataList) {
            // 默认值
            if (TASK_OBJECT_TYPE_DEFAULT.equals(li.getTaskObjectType())) {
                continue;
            }

            // 员工号
            String empNo = li.getEmpNo();
            if (StringUtils.isBlank(empNo)) {
                continue;
            }

            // 分割员工号并过滤掉空白项
            String[] empNos = empNo.split(",");
            for (String no : empNos) {
                if (StringUtils.isNotBlank(no)) {
                    list.add(no.trim());
                }
            }
        }

        if (CollectionUtils.isEmpty(list)) {
            return new HashMap<>();
        }
        // 查询员工信息
        List<SafetyEmployeeMessageDO> listByStaffNo = safetyEmployeeMessageService.getListByStaffNo(new ArrayList<>(list));
        if (CollectionUtils.isEmpty(listByStaffNo)) {
            return new HashMap<>();
        }

        return listByStaffNo.stream()
                .map(source -> {
                    FireInspectionEmpInfoDto dto = new FireInspectionEmpInfoDto();
                    BeanUtils.copyProperties(source, dto);
                    return dto;
                })
                .collect(Collectors.toMap(
                        FireInspectionEmpInfoDto::getEmpNo,
                        Function.identity(),
                        (oldValue, newValue) -> oldValue
                ));
    }

    /**
     * 任务详情对象
     *
     * @param taskSetDO  任务设置对象
     * @param empInfoDto 员工信息
     * @param value      周几， 周一的话周期结束时间加二天到周三， 周四的话加三天到周周日
     * @return 返回任务详情对象
     */
    private SafetyFireInspectionTaskInfoDO getTaskInfo(SafetyFireInspectionTaskSetDO taskSetDO, FireInspectionEmpInfoDto empInfoDto, int value) {
        SafetyFireInspectionTaskInfoDO taskInfoDO = new SafetyFireInspectionTaskInfoDO();
        BeanUtils.copyProperties(taskSetDO, taskInfoDO);
        taskInfoDO.setEmpNo(empInfoDto.getEmpNo());
        taskInfoDO.setEmpName(empInfoDto.getEmpName());
        taskInfoDO.setEmpOrgCodePath(empInfoDto.getOrgCodePath());
        taskInfoDO.setEmpOrgNamePath(empInfoDto.getOrgNamePath());
        taskInfoDO.setTaskCycleBeginDate(DateUtils.format(new Date(), 1));
        if (FireInspectionEnum.BLUE.getCode().equals(taskInfoDO.getRiskLevel())) {
            taskInfoDO.setTaskCycleEndDate(DateUtils.format(DateUtils.addDays(new Date(), value == 4 ? 3 : 2), 1));
        } else {
            taskInfoDO.setTaskCycleEndDate(DateUtils.format(DateUtils.addDays(new Date(), 6), 1));
        }
        taskInfoDO.setTaskIssuedTime(new Date());
        taskInfoDO.setTaskStatus(FIRE_TASK_INCOMPLETE);
        taskInfoDO.setId(null);
        taskInfoDO.setTemporaryId(String.format("%s-%s-%s-%s", taskInfoDO.getWorkDeptCode(), taskInfoDO.getEmpNo(), System.currentTimeMillis(), UUID.randomUUID().toString())); // 临时唯一值
        return taskInfoDO;
    }

    /**
     * 任务详情明细对象
     *
     * @param standardDO 检查标准
     * @return 返回对象信息
     */
    private SafetyFireInspectionTaskInfoDetailDO getTaskInfoDetail(SafetyFireInspectionStandardDO standardDO, String temporaryId) {
        SafetyFireInspectionTaskInfoDetailDO detailDO = new SafetyFireInspectionTaskInfoDetailDO();
        BeanUtils.copyProperties(standardDO, detailDO);
        detailDO.setInspectionCode(String.valueOf(standardDO.getId()));
        detailDO.setTemporaryId(temporaryId);
        return detailDO;
    }

    /**
     * 判断当前的风险等级，是否在要指定的风险等级里面
     *
     * @param riskLevelCheck 当前的风险等级
     * @param riskLevelMust  指定的风险等级
     * @return true 表示在  false表示不在
     */
    private boolean checkRiskLevel(String riskLevelCheck, String riskLevelMust) {
        // 目前风险等级从小到大排序，所以可以直接比较
        int riskCodeCheck = Integer.parseInt(riskLevelCheck);
        int riskCodeMust = Integer.parseInt(riskLevelMust);
        return riskCodeMust >= riskCodeCheck;
    }
}
