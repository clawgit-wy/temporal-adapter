package com.yutong.safety.core.service.build;

import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.core.db.entity.build.SafetyBuildInfoRecordDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildInfoRecordDOExample;
import com.yutong.safety.core.db.mapper.build.SafetyBuildInfoRecordDTOMapper;
import com.yutong.safety.core.dto.build.SafetyBuildInfoRecordDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SafetyBuildInfoRecordService {
    @Autowired
    SafetyBuildInfoRecordDTOMapper safetyBuildInfoRecordDTOMapper;

    private SafetyBuildInfoRecordDOExample getExample(SafetyBuildInfoRecordDTO param){
        SafetyBuildInfoRecordDOExample example = new SafetyBuildInfoRecordDOExample();
        SafetyBuildInfoRecordDOExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isBlank(param.getInfoType())){
            criteria.andInfoTypeEqualTo(param.getInfoType());
        }

        if(!StringUtils.isBlank(param.getBeginDate())){
            criteria.andInfoTimeGreaterThanOrEqualTo(param.getBeginDate());
        }

        if(!StringUtils.isBlank(param.getEndDate())){
            criteria.andInfoTimeLessThanOrEqualTo(param.getEndDate());
        }

        if(!StringUtils.isBlank(param.getInfoConfirm())){
            criteria.andInfoConfirmEqualTo(param.getInfoConfirm());
        }

        if(!StringUtils.isBlank(param.getInfoConfirm())){
            criteria.andInfoConfirmEqualTo(param.getInfoConfirm());
        }

        if(!StringUtils.isBlank(param.getNeedPage())){
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }

        return example;
    }

    public List<SafetyBuildInfoRecordDO> selectPageList(SafetyBuildInfoRecordDTO param) {
        SafetyBuildInfoRecordDOExample example = getExample(param);
        example.setOrderByClause(" info_time desc");
        return safetyBuildInfoRecordDTOMapper.selectByExample(example);
    }

    public long selectListCount(SafetyBuildInfoRecordDTO param) {
        SafetyBuildInfoRecordDOExample example = getExample(param);
        return safetyBuildInfoRecordDTOMapper.countByExample(example);
    }

    public void update(SafetyBuildInfoRecordDO param) {
        safetyBuildInfoRecordDTOMapper.updateByPrimaryKeySelective(param);
    }

    public void save(SafetyBuildInfoRecordDO param) {
        safetyBuildInfoRecordDTOMapper.insertSelective(param);
    }

    public void batchSave(List<SafetyBuildInfoRecordDO> list) {
        safetyBuildInfoRecordDTOMapper.batchSave(list);
    }
}
