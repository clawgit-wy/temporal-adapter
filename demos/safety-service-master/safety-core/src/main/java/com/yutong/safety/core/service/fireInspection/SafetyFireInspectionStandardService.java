package com.yutong.safety.core.service.fireInspection;

import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionStandardDO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionStandardDOExample;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionWorkStepDO;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionStandardAddParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionStandardQueryParams;
import com.yutong.safety.core.db.mapper.fireInspection.SafetyFireInspectionStandardDOMapper;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SafetyFireInspectionStandardService {

    @Resource
    private SafetyFireInspectionStandardDOMapper safetyFireInspectionStandardDOMapper;

    public long selectCount(SafetyFireInspectionStandardQueryParams param, String workNo) {
        SafetyFireInspectionStandardDOExample example = formatExample(param, workNo);
        return safetyFireInspectionStandardDOMapper.countByExample(example);
    }

    /**
     * 分页获取的数据
     * @param param 入参
     * @param workNo 工号
     */
    public List<SafetyFireInspectionStandardDO> selectList(SafetyFireInspectionStandardQueryParams param, String workNo, boolean pageCheck) {
        SafetyFireInspectionStandardDOExample example = formatExample(param, workNo);
        example.setOrderByClause(" CREATE_TIME DESC ");
        if (pageCheck) {
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
        return safetyFireInspectionStandardDOMapper.selectByExample(example);
    }

    /**
     * 根据作业环节编码获取所有的检查标准信息
     * @param workStepCodes 返回符合条件的数据
     */
    public List<SafetyFireInspectionStandardDO> selectList(List<String> workStepCodes) {
        SafetyFireInspectionStandardDOExample example = new SafetyFireInspectionStandardDOExample();
        SafetyFireInspectionStandardDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyFireInspectionStandardDO.NORMAL);
        criteria.andWorkStepCodeIn(workStepCodes);
        return safetyFireInspectionStandardDOMapper.selectByExample(example);
    }

    /**
     * 判断指定条件的数据是否已存在
     * @param params 参数-目前只用到了作业环节编码
     * @return true 表示已存在
     */
    public boolean selectExit(SafetyFireInspectionStandardAddParams params) {
        SafetyFireInspectionStandardDOExample example = new SafetyFireInspectionStandardDOExample();
        SafetyFireInspectionStandardDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyFireInspectionStandardDO.NORMAL);
        criteria.andWorkStepNameEqualTo(params.getWorkStepName());
        criteria.andWorkStepCodeEqualTo(params.getWorkStepCode());
        if (StringUtils.isNotBlank(params.getInspectionItem())) {
            criteria.andInspectionItemEqualTo(params.getInspectionItem());
        }
        if (StringUtils.isNotBlank(params.getInspectionObject())) {
            criteria.andInspectionObjectEqualTo(params.getInspectionObject());
        }
        criteria.andInspectionCategoryEqualTo(params.getInspectionCategory());
        criteria.andInspectionSubDimensionEqualTo(params.getInspectionSubDimension());
        criteria.andInspectionContentEqualTo(params.getInspectionContent());
        criteria.andRiskLevelEqualTo(params.getRiskLevel());
        criteria.andInspectionMethodEqualTo(params.getInspectionMethod());
        if (params.getId() != null) {
            criteria.andIdNotEqualTo(params.getId());
        }
        long l = safetyFireInspectionStandardDOMapper.countByExample(example);
        return l > 0;
    }

    public SafetyFireInspectionStandardDO selectByPrimaryKey(Long id) {
        return safetyFireInspectionStandardDOMapper.selectByPrimaryKey(id);
    }

    public boolean updateByPrimaryKey(SafetyFireInspectionStandardDO data) {
        int i = safetyFireInspectionStandardDOMapper.updateByPrimaryKey(data);
        return i > 0;
    }

    /**
     * 根据ID批量删除
     * @param idList ID集合
     * @param token 用户信息
     */
    public boolean deleteById(List<Long> idList, String token) {
        SafetyFireInspectionStandardDO record = new SafetyFireInspectionStandardDO();
        record.setUpdateInfo(token);
        record.setDelFlag(SafetyFireInspectionStandardDO.DELETE);
        SafetyFireInspectionStandardDOExample example = new SafetyFireInspectionStandardDOExample();
        SafetyFireInspectionStandardDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyFireInspectionStandardDO.NORMAL);
        criteria.andIdIn(idList);
        int i = safetyFireInspectionStandardDOMapper.updateByExampleSelective(record, example);
        return i > 0;
    }

    /**
     * 根据作业环节编码删除(消防巡检-风险点台账-作业环节删除功能关联的删除)
     * @param workStepCodes 作业环节编码
     * @param token 用户信息
     * @return true 成功 否则失败
     */
    public boolean deleteByWorkStepCode(List<String> workStepCodes, String token) {
        SafetyFireInspectionStandardDO record = new SafetyFireInspectionStandardDO();
        record.setUpdateInfo(token);
        record.setDelFlag(SafetyFireInspectionStandardDO.DELETE);
        record.setRemark("作业环节删除关联删除对应的检查标准");
        SafetyFireInspectionStandardDOExample example = new SafetyFireInspectionStandardDOExample();
        SafetyFireInspectionStandardDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyFireInspectionStandardDO.NORMAL);
        criteria.andWorkStepCodeIn(workStepCodes);
        int i = safetyFireInspectionStandardDOMapper.updateByExampleSelective(record, example);
        return i > 0;
    }

    /**
     * 同步修改检查标准的作业环节名称 此处不更新修改时间
     * @param workStepCode 作业环节编码
     */
    public boolean updateStepNameByStepCode(String workStepCode, String workStepName) {
        SafetyFireInspectionStandardDO record = new SafetyFireInspectionStandardDO();
        record.setWorkStepName(workStepName);
        SafetyFireInspectionStandardDOExample example = new SafetyFireInspectionStandardDOExample();
        SafetyFireInspectionStandardDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyFireInspectionStandardDO.NORMAL);
        criteria.andWorkStepCodeEqualTo(workStepCode);
        int i = safetyFireInspectionStandardDOMapper.updateByExampleSelective(record, example);
        return i > 0;
    }


    public boolean insert(SafetyFireInspectionStandardDO data) {
        int i = safetyFireInspectionStandardDOMapper.insert(data);
        return i > 0;
    }

    /**
     * 批量修改
     */
    public void batchSave(List<SafetyFireInspectionStandardDO> list) {
        List<List<SafetyFireInspectionStandardDO>> partition = ListUtils.partition(list, 200);
        partition.forEach(li -> safetyFireInspectionStandardDOMapper.batchInsert(li));
    }


    private SafetyFireInspectionStandardDOExample formatExample(SafetyFireInspectionStandardQueryParams param, String workNo) {
        SafetyFireInspectionStandardDOExample example = new SafetyFireInspectionStandardDOExample();
        SafetyFireInspectionStandardDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(0L);
        if (StringUtils.isNotBlank(param.getWorkStepName())) {
            criteria.andWorkStepNameLike("%" + param.getWorkStepName() + "%");
        }
        if (StringUtils.isNotBlank(param.getInspectionSubDimension())) {
            criteria.andInspectionSubDimensionLike("%" + param.getInspectionSubDimension() + "%");
        }
        if (StringUtils.isNotBlank(param.getRiskLevel())) {
            criteria.andRiskLevelEqualTo(param.getRiskLevel());
        }
        if (StringUtils.isNotBlank(param.getWorkStepCode())) {
            criteria.andWorkStepCodeLike("%" + param.getWorkStepCode() + "%");
        }
        if (!CollectionUtils.isEmpty(param.getRiskLevelList())) {
            criteria.andRiskLevelIn(param.getRiskLevelList());
        }
        if (StringUtils.isNotBlank(param.getUpdateBy())) {
            criteria.andUpdateByAndUpdateByNoLike(param.getUpdateBy());
        }
        if (StringUtils.isNotBlank(workNo)) {
            criteria.andCreateByNoEqualTo(workNo);
        }
        return example;
    }
    /**
     * 批量同步修改检查标准的作业环节名称 此处不更新修改时间
     * @param updateList 作业环节编码
     */
    public void batchUpdate(List<SafetyFireInspectionWorkStepDO> updateList) {
        List<List<SafetyFireInspectionWorkStepDO>> partition = ListUtils.partition(updateList, 200);
        partition.forEach(li -> safetyFireInspectionStandardDOMapper.batchUpdate(li));
    }
}