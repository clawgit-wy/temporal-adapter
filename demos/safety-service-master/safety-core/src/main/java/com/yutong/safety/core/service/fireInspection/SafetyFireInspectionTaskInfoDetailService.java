package com.yutong.safety.core.service.fireInspection;

import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionTaskInfoDetailDO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionTaskInfoDetailDOExample;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskInfoDetailQueryParams;
import com.yutong.safety.core.db.mapper.fireInspection.SafetyFireInspectionTaskInfoDetailDOMapper;
import com.yutong.safety.core.dto.fireInspection.FireInspectionTaskInfoCountDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SafetyFireInspectionTaskInfoDetailService {

    @Resource
    private SafetyFireInspectionTaskInfoDetailDOMapper safetyFireInspectionTaskInfoDetailDOMapper;

    public long selectCount(SafetyFireInspectionTaskInfoDetailQueryParams params) {
        SafetyFireInspectionTaskInfoDetailDOExample example = new SafetyFireInspectionTaskInfoDetailDOExample();
        SafetyFireInspectionTaskInfoDetailDOExample.Criteria criteria = example.createCriteria();
        criteria.andInspectionTaskIdEqualTo(params.getInspectionTaskId());
        return safetyFireInspectionTaskInfoDetailDOMapper.countByExample(example);
    }

    /**
     * 获取任务量详情
     */
    public List<FireInspectionTaskInfoCountDto> selectCount(List<Long> idList) {
        return safetyFireInspectionTaskInfoDetailDOMapper.selectCount(idList);
    }

    public List<SafetyFireInspectionTaskInfoDetailDO> selectList(SafetyFireInspectionTaskInfoDetailQueryParams params, boolean pageCheck) {
        SafetyFireInspectionTaskInfoDetailDOExample example = new SafetyFireInspectionTaskInfoDetailDOExample();
        SafetyFireInspectionTaskInfoDetailDOExample.Criteria criteria = example.createCriteria();
        example.setOrderByClause(" INSPECTION_RESULT ASC, INSPECTION_TIME DESC ");
        criteria.andInspectionTaskIdEqualTo(params.getInspectionTaskId());
        if (pageCheck) {
            example.setLimit(params.getLimit());
            example.setOffset(params.getOffset());
        }
        return safetyFireInspectionTaskInfoDetailDOMapper.selectByExample(example);
    }

    /**
     * 根据ID获取明细数据，只获取没有检查结果的数据
     * @param ids
     * @return
     */
    public List<SafetyFireInspectionTaskInfoDetailDO> selectList(List<Long> ids) {
        SafetyFireInspectionTaskInfoDetailDOExample example = new SafetyFireInspectionTaskInfoDetailDOExample();
        SafetyFireInspectionTaskInfoDetailDOExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        criteria.andInspectionResultIsNull();
        return safetyFireInspectionTaskInfoDetailDOMapper.selectByExample(example);
    }

    /**
     * 批量新增
     * @param list List集合
     */
    public void batchSave(List<SafetyFireInspectionTaskInfoDetailDO> list) {
        List<List<SafetyFireInspectionTaskInfoDetailDO>> partition = ListUtils.partition(list, 200);
        partition.forEach(li -> safetyFireInspectionTaskInfoDetailDOMapper.batchInsert(li));
    }

    public SafetyFireInspectionTaskInfoDetailDO selectByPrimaryKey(Long id) {
        return safetyFireInspectionTaskInfoDetailDOMapper.selectByPrimaryKey(id);
    }

    public boolean updateByPrimaryKeySelective(SafetyFireInspectionTaskInfoDetailDO detailDO) {
        int i = safetyFireInspectionTaskInfoDetailDOMapper.updateByPrimaryKeySelective(detailDO);
        return i > 0;
    }

    /**
     * 批量变更任务详情明细
     *
     * @param ids id列表
     */
    public void updateProcessIdResult(List<Long> ids, int status) {
        if (!CollectionUtils.isEmpty(ids)) {
            safetyFireInspectionTaskInfoDetailDOMapper.updateProcessIdResult(ids, status);
        }
    }

    /**
     * 批量修改， 目前只批量修改时间、检查结果、异常ID、等信息
     * @param updateDto List<修改的数据对象>
     */
    public void batchUpdate(List<SafetyFireInspectionTaskInfoDetailDO> updateDto) {
        safetyFireInspectionTaskInfoDetailDOMapper.batchUpdate(updateDto);
    }
}
