package com.yutong.safety.core.service.fireInspection;

import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionTaskInfoDO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionTaskInfoDOExample;
import com.yutong.safety.core.db.entity.fireInspection.excel.SafetyFireInspectionTaskInfoExcel;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskInfoQueryParams;
import com.yutong.safety.core.db.mapper.fireInspection.SafetyFireInspectionTaskInfoDOMapper;
import com.yutong.safety.core.dto.fireInspection.FireInspectionRectificationResultDto;
import com.yutong.safety.core.dto.fireInspection.FireInspectionTaskInfoCountDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SafetyFireInspectionTaskInfoService {
    @Resource
    private SafetyFireInspectionTaskInfoDOMapper safetyFireInspectionTaskInfoDOMapper;

    /**
     * 获取指定员工的任务量
     */
    public FireInspectionTaskInfoCountDto selectTaskCount(SafetyFireInspectionTaskInfoQueryParams param) {
        SafetyFireInspectionTaskInfoDOExample example = formatExample(param, Lists.newArrayList());
        return safetyFireInspectionTaskInfoDOMapper.selectCount(example);
    }

    public long selectCount(SafetyFireInspectionTaskInfoQueryParams param, List<String> empCodeList) {
        SafetyFireInspectionTaskInfoDOExample example = formatExample(param, empCodeList);
        return safetyFireInspectionTaskInfoDOMapper.countByExample(example);
    }

    public List<SafetyFireInspectionTaskInfoDO> selectList(SafetyFireInspectionTaskInfoQueryParams param, List<String> empCodeList, boolean pageCheck) {
        SafetyFireInspectionTaskInfoDOExample example = formatExample(param, empCodeList);
        example.setOrderByClause(" TASK_ISSUED_TIME DESC ");
        if (pageCheck) {
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
        return safetyFireInspectionTaskInfoDOMapper.selectByExample(example);
    }

    /**
     * 数据导出功能
     */
    public List<SafetyFireInspectionTaskInfoExcel> selectDownloadList(SafetyFireInspectionTaskInfoQueryParams param) {
        return safetyFireInspectionTaskInfoDOMapper.selectList(param);
    }

    /**
     * 批量新增
     * @param list List集合
     */
    public void batchSave(List<SafetyFireInspectionTaskInfoDO> list) {
        List<List<SafetyFireInspectionTaskInfoDO>> partition = ListUtils.partition(list, 200);
        partition.forEach(li -> safetyFireInspectionTaskInfoDOMapper.batchInsert(li));
    }

    /**
     * 数据修改-（变更任务状态，时间，整改结果，检查结果等信息）
     */
    public void update(SafetyFireInspectionTaskInfoDO record) {
        safetyFireInspectionTaskInfoDOMapper.updateByPrimaryKeySelective(record);
    }

    /**
     * 获取未整改的数据信息
     */
    public List<FireInspectionRectificationResultDto> selectListByRectificationResult(List<String> processIds) {
        return safetyFireInspectionTaskInfoDOMapper.selectListByRectificationResult(processIds);
    }

    /**
     * 批量修改整改结果
     */
    public void batchUpdate(List<SafetyFireInspectionTaskInfoDO> list) {
        safetyFireInspectionTaskInfoDOMapper.batchUpdate(list);
    }

    private SafetyFireInspectionTaskInfoDOExample formatExample(SafetyFireInspectionTaskInfoQueryParams param, List<String> empCodeList) {
        SafetyFireInspectionTaskInfoDOExample example = new SafetyFireInspectionTaskInfoDOExample();
        SafetyFireInspectionTaskInfoDOExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(param.getWorkStepName())) {
            criteria.andWorkStepNameLike("%" + param.getWorkStepName() + "%");
        }
        if (CollectionUtils.isNotEmpty(empCodeList)) {
            criteria.andWorkDeptCodeIn(empCodeList);
        }
        if (StringUtils.isNotBlank(param.getInspectionItem())) {
            criteria.andInspectionItemByIdIn(param.getInspectionItem());
        }
        if (StringUtils.isNotBlank(param.getRiskLevel())) {
            criteria.andRiskLevelEqualTo(param.getRiskLevel());
        }
        if (StringUtils.isNotBlank(param.getTaskStatus())) {
            criteria.andTaskStatusEqualTo(param.getTaskStatus());
        }
        if (StringUtils.isNotBlank(param.getEmpName())) {
            criteria.andEmpNoAndEmpNameEqualTo(param.getEmpName());
        }
        if (StringUtils.isNotBlank(param.getEmpNo())) {
            criteria.andEmpNoEqualTo(param.getEmpNo());
        }
        if (StringUtils.isNotBlank(param.getTaskCycleBeginDate())) {
            criteria.andTaskCycleBeginDateGreaterThanOrEqualTo(param.getTaskCycleBeginDate());
        }
        if (StringUtils.isNotBlank(param.getTaskCycleEndDate())) {
            criteria.andTaskCycleBeginDateLessThanOrEqualTo(param.getTaskCycleEndDate());
        }
        return example;
    }
}
