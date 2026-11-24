package com.yutong.safety.core.service.fireInspection;

import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionWorkStepDO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionWorkStepDOExample;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionWorkStepAddParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionWorkStepQueryParams;
import com.yutong.safety.core.db.mapper.fireInspection.SafetyFireInspectionWorkStepDOMapper;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SafetyFireInspectionWorkStepService {
    @Resource
    private SafetyFireInspectionWorkStepDOMapper safetyFireInspectionWorkStepDOMapper;


    public long selectCount(SafetyFireInspectionWorkStepQueryParams listParam, String workNo, List<String> codeList) {
        SafetyFireInspectionWorkStepDOExample example = formatExample(listParam, workNo, codeList);
        return safetyFireInspectionWorkStepDOMapper.countByExample(example);
    }

    /**
     * 判断指定条件的数据是否已存在
     * @param params 参数-目前只用到了作业环节编码
     * @return true 表示已存在
     */
    public boolean selectExit(SafetyFireInspectionWorkStepAddParams params) {
        SafetyFireInspectionWorkStepDOExample example = new SafetyFireInspectionWorkStepDOExample();
        SafetyFireInspectionWorkStepDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyFireInspectionWorkStepDO.NORMAL);
        criteria.andWorkStepCodeEqualTo(params.getWorkStepCode());
        long l = safetyFireInspectionWorkStepDOMapper.countByExample(example);
        return l > 0;
    }


    /**
     * 根据作业环节编码获取作业环节的数据信息
     * @param workStepCodes 作业环节编码
     */
    public List<SafetyFireInspectionWorkStepDO> selectList(List<String> workStepCodes) {
        SafetyFireInspectionWorkStepDOExample example = new SafetyFireInspectionWorkStepDOExample();
        SafetyFireInspectionWorkStepDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyFireInspectionWorkStepDO.NORMAL);
        criteria.andWorkStepCodeIn(workStepCodes);
        return safetyFireInspectionWorkStepDOMapper.selectByExample(example);
    }

    /**
     * 根据条件获取作业环节的数据
     * @param listParam 条件入参
     * @param workNo 员工工号
     * @param bool 是否分页 true分页， false 不分页
     * @return 返回集合
     */
    public List<SafetyFireInspectionWorkStepDO> selectList(SafetyFireInspectionWorkStepQueryParams listParam, String workNo, List<String> codeList, boolean bool) {
        SafetyFireInspectionWorkStepDOExample example = formatExample(listParam, workNo, codeList);
        example.setOrderByClause(" CREATE_TIME DESC ");
        if (bool) {
            example.setLimit(listParam.getLimit());
            example.setOffset(listParam.getOffset());
        }
        return safetyFireInspectionWorkStepDOMapper.selectByExample(example);
    }

    public List<SafetyFireInspectionWorkStepDO> selectByExample(List<Long> idList) {
        SafetyFireInspectionWorkStepDOExample example = new SafetyFireInspectionWorkStepDOExample();
        SafetyFireInspectionWorkStepDOExample.Criteria criteria = example.createCriteria();
        example.setOrderByClause(" CREATE_TIME DESC ");
        criteria.andDelFlagEqualTo(SafetyFireInspectionWorkStepDO.NORMAL);
        if (!CollectionUtils.isEmpty(idList)) {
            criteria.andIdIn(idList);
        }
        return safetyFireInspectionWorkStepDOMapper.selectByExample(example);
    }

    public SafetyFireInspectionWorkStepDO selectByPrimaryKey(Long id) {
        return safetyFireInspectionWorkStepDOMapper.selectByPrimaryKey(id);
    }

    public boolean updateByPrimaryKey(SafetyFireInspectionWorkStepDO data) {
        int i = safetyFireInspectionWorkStepDOMapper.updateByPrimaryKey(data);
        return i > 0;
    }

    public boolean deleteById(List<Long> idList, String token) {
        SafetyFireInspectionWorkStepDO record = new SafetyFireInspectionWorkStepDO();
        record.setUpdateInfo(token);
        record.setDelFlag(SafetyFireInspectionWorkStepDO.DELETE);
        SafetyFireInspectionWorkStepDOExample example = new SafetyFireInspectionWorkStepDOExample();
        SafetyFireInspectionWorkStepDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyFireInspectionWorkStepDO.NORMAL);
        criteria.andIdIn(idList);
        int i = safetyFireInspectionWorkStepDOMapper.updateByExampleSelective(record, example);
        return i > 0;
    }

    public boolean insert(SafetyFireInspectionWorkStepDO data) {
        int i = safetyFireInspectionWorkStepDOMapper.insert(data);
        return i > 0;
    }

    /**
     * 批量新增
     * @param list List集合
     */
    public void batchSave(List<SafetyFireInspectionWorkStepDO> list) {
        List<List<SafetyFireInspectionWorkStepDO>> partition = ListUtils.partition(list, 200);
        partition.forEach(li -> safetyFireInspectionWorkStepDOMapper.batchInsert(li));
    }

    /**
     * 批量修改
     * @param list List集合
     */
    public void batchUpdate(List<SafetyFireInspectionWorkStepDO> list) {
        List<List<SafetyFireInspectionWorkStepDO>> partition = ListUtils.partition(list, 200);
        partition.forEach(li -> safetyFireInspectionWorkStepDOMapper.batchUpdate(li));
    }

    private SafetyFireInspectionWorkStepDOExample formatExample(SafetyFireInspectionWorkStepQueryParams param, String workNo, List<String> codeList) {
        SafetyFireInspectionWorkStepDOExample example = new SafetyFireInspectionWorkStepDOExample();
        SafetyFireInspectionWorkStepDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyFireInspectionWorkStepDO.NORMAL);
        if (!CollectionUtils.isEmpty(codeList)) {
            criteria.andWorkDeptCodeIn(codeList);
        }
        if (StringUtils.isNotBlank(param.getWorkStepName())) {
            criteria.andWorkStepNameLike("%" + param.getWorkStepName() + "%");
        }
        if (StringUtils.isNotBlank(param.getWorkStepCode())) {
            criteria.andWorkStepCodeLike("%" + param.getWorkStepCode() + "%");
        }
        if (StringUtils.isNotBlank(param.getUpdateBy())) {
            criteria.andUpdateByAndUpdateByNoLike(param.getUpdateBy());
        }
        if (StringUtils.isNotBlank(workNo)) {
            criteria.andCreateByNoEqualTo(workNo);
        }
        return example;
    }
}
