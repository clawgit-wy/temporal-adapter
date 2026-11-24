package com.yutong.safety.core.service.fireInspection;

import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionTaskSetDO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionTaskSetDOExample;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionWorkStepDO;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskSetAddParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskSetQueryParams;
import com.yutong.safety.core.db.mapper.fireInspection.SafetyFireInspectionTaskSetDOMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.yutong.safety.core.db.entity.fireInspection.common.FireInspectionConstants.TASK_OBJECT_TYPE_CUSTOMIZE;

@Service
public class SafetyFireInspectionTaskSetService {
    @Resource
    private SafetyFireInspectionTaskSetDOMapper safetyFireInspectionTaskSetDOMapper;

    public long selectCount(SafetyFireInspectionTaskSetQueryParams param, String workNo, List<String> empCodeList) {
        SafetyFireInspectionTaskSetDOExample example = formatExample(param, workNo,empCodeList);
        return safetyFireInspectionTaskSetDOMapper.countByExample(example);
    }

    public List<SafetyFireInspectionTaskSetDO> selectList(SafetyFireInspectionTaskSetQueryParams param, String workNo, List<String> empCodeList) {
        SafetyFireInspectionTaskSetDOExample example = formatExample(param, workNo, empCodeList);
        example.setOrderByClause(" CREATE_TIME DESC ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        return safetyFireInspectionTaskSetDOMapper.selectByExample(example);
    }

    /**
     * 根据作业环节编码删除(消防巡检-风险点台账-作业环节删除功能关联的删除)
     * @param workStepCodes 作业环节编码
     * @param token 用户信息
     * @return true 成功 否则失败
     */
    public boolean deleteByWorkStepCode(List<String> workStepCodes, String token) {
        SafetyFireInspectionTaskSetDO record = new SafetyFireInspectionTaskSetDO();
        record.setUpdateInfo(token);
        record.setDelFlag(SafetyFireInspectionTaskSetDO.DELETE);
        record.setRemark("作业环节删除关联删除对应的任务");
        SafetyFireInspectionTaskSetDOExample example = new SafetyFireInspectionTaskSetDOExample();
        SafetyFireInspectionTaskSetDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyFireInspectionTaskSetDO.NORMAL);
        criteria.andWorkStepCodeIn(workStepCodes);
        int i = safetyFireInspectionTaskSetDOMapper.updateByExampleSelective(record, example);
        return i > 0;
    }

    /**
     * 同步修改人物设置的作业环节名称 此处不更新修改时间
     * @param workStepCode 作业环节编码
     * @param workStepName 作业环节名称
     * @param workDeptCode 作业部门编码
     * @param workDeptName 作业部门名称
     */
    public void updateInfoByStepCode(String workStepCode, String workStepName, String workDeptCode, String workDeptName) {
        SafetyFireInspectionTaskSetDO record = new SafetyFireInspectionTaskSetDO();
        record.setWorkDeptName(workDeptName);
        record.setWorkDeptCode(workDeptCode);
        record.setWorkStepName(workStepName);
        SafetyFireInspectionTaskSetDOExample example = new SafetyFireInspectionTaskSetDOExample();
        SafetyFireInspectionTaskSetDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyFireInspectionTaskSetDO.NORMAL);
        criteria.andWorkStepCodeEqualTo(workStepCode);
        safetyFireInspectionTaskSetDOMapper.updateByExampleSelective(record, example);
    }

    /**
     * 判断指定条件的数据是否已存在
     * @param params 参数-目前只用到了作业环节编码
     * @return true 表示已存在
     */
    public boolean selectExit(SafetyFireInspectionTaskSetAddParams params) {
        SafetyFireInspectionTaskSetDOExample example = new SafetyFireInspectionTaskSetDOExample();
        SafetyFireInspectionTaskSetDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyFireInspectionTaskSetDO.NORMAL);
        criteria.andTaskNameEqualTo(params.getTaskName());
        criteria.andWorkStepCodeEqualTo(params.getWorkStepCode());
        criteria.andRiskLevelEqualTo(params.getRiskLevel());
        criteria.andTaskObjectTypeEqualTo(params.getTaskObjectType());
        if (params.getTaskObjectType().equals(TASK_OBJECT_TYPE_CUSTOMIZE)) {
            criteria.andEmpNoEqualTo(params.getEmpNo());
        }
        criteria.andTaskBeginDateEqualTo(params.getTaskBeginDate());
        criteria.andTaskEndDateEqualTo(params.getTaskEndDate());
        if (params.getId() != null) {
            criteria.andIdNotEqualTo(params.getId());
        }
        long l = safetyFireInspectionTaskSetDOMapper.countByExample(example);
        return l > 0;
    }

    public SafetyFireInspectionTaskSetDO selectByPrimaryKey(Long id) {
        return safetyFireInspectionTaskSetDOMapper.selectByPrimaryKey(id);
    }

    public boolean updateByPrimaryKey(SafetyFireInspectionTaskSetDO data) {
        int i = safetyFireInspectionTaskSetDOMapper.updateByPrimaryKey(data);
        return i > 0;
    }

    /**
     * 根据ID批量删除
     * @param idList ID集合
     * @param token 用户信息
     */
    public boolean deleteById(List<Long> idList, String token) {
        SafetyFireInspectionTaskSetDO record = new SafetyFireInspectionTaskSetDO();
        record.setUpdateInfo(token);
        record.setDelFlag(SafetyFireInspectionTaskSetDO.DELETE);
        SafetyFireInspectionTaskSetDOExample example = new SafetyFireInspectionTaskSetDOExample();
        SafetyFireInspectionTaskSetDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyFireInspectionTaskSetDO.NORMAL);
        criteria.andIdIn(idList);
        int i = safetyFireInspectionTaskSetDOMapper.updateByExampleSelective(record, example);
        return i > 0;
    }



    public boolean insert(SafetyFireInspectionTaskSetDO data) {
        int i = safetyFireInspectionTaskSetDOMapper.insert(data);
        return i > 0;
    }

    /**
     * 批量插入
     */
    public void batchSave(List<SafetyFireInspectionTaskSetDO> list) {
        List<List<SafetyFireInspectionTaskSetDO>> partition = ListUtils.partition(list, 200);
        partition.forEach(li -> safetyFireInspectionTaskSetDOMapper.batchInsert(li));
    }

    /**
     * 获取全量数据 条件：任务周期包含今天的、并且状态是正常的，数据未删除的数据
     * 特殊条件：周四时检查周期为每周2次
     * @return 返回符合条件的数据
     * @param riskLevel 风险等级
     */
    public List<SafetyFireInspectionTaskSetDO> selectList(String riskLevel) {
        String date = DateUtils.format(new Date(), 1);
        SafetyFireInspectionTaskSetDOExample example = new SafetyFireInspectionTaskSetDOExample();
        SafetyFireInspectionTaskSetDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyFireInspectionTaskSetDO.NORMAL);
        criteria.andStatusEqualTo(0L);
        criteria.andTaskBeginDateLessThanOrEqualTo(date);
        criteria.andTaskEndDateGreaterThanOrEqualTo(date);
        if (StringUtils.isNotBlank(riskLevel)) {
            criteria.andRiskLevelEqualTo(riskLevel);
        }
        return safetyFireInspectionTaskSetDOMapper.selectByExample(example);
    }

    /**
     * 根据作业环节编码获取数据
     *
     * @param workStepCodes 作业环节编码
     * @return 返回符合条件的数据
     */
    public List<SafetyFireInspectionTaskSetDO> selectList(List<String> workStepCodes) {
        SafetyFireInspectionTaskSetDOExample example = new SafetyFireInspectionTaskSetDOExample();
        SafetyFireInspectionTaskSetDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyFireInspectionTaskSetDO.NORMAL);
        criteria.andWorkStepCodeIn(workStepCodes);
        return safetyFireInspectionTaskSetDOMapper.selectByExample(example);
    }

    private SafetyFireInspectionTaskSetDOExample formatExample(SafetyFireInspectionTaskSetQueryParams param, String workNo, List<String> empCodeList) {
        SafetyFireInspectionTaskSetDOExample example = new SafetyFireInspectionTaskSetDOExample();
        SafetyFireInspectionTaskSetDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(0L);
        if (StringUtils.isNotBlank(param.getWorkStepName())) {
            criteria.andWorkStepNameLike("%" + param.getWorkStepName() + "%");
        }
        if (StringUtils.isNotBlank(param.getTaskName())) {
            criteria.andTaskNameLike("%" + param.getTaskName() + "%");
        }
        if (StringUtils.isNotBlank(param.getRiskLevel())) {
            criteria.andRiskLevelEqualTo(param.getRiskLevel());
        }
        if (StringUtils.isNotBlank(param.getUpdateBy())) {
            criteria.andUpdateByAndUpdateByNoLike(param.getUpdateBy());
        }
        if (CollectionUtils.isNotEmpty(empCodeList)) {
            criteria.andWorkDeptCodeIn(empCodeList);
        }
        if (StringUtils.isNotBlank(workNo)) {
            criteria.andCreateByNoEqualTo(workNo);
        }
        return example;
    }

    /**
     * 批量同步修改任务设置的作业环节名称 此处不更新修改时间
     * @param updateList 作业环节编码
     */
    public void batchUpdate(List<SafetyFireInspectionWorkStepDO> updateList) {
        List<List<SafetyFireInspectionWorkStepDO>> partition = ListUtils.partition(updateList, 200);
        partition.forEach(li -> safetyFireInspectionTaskSetDOMapper.batchUpdate(li));
    }
}
