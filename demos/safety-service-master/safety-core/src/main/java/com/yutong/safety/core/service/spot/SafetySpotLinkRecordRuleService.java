package com.yutong.safety.core.service.spot;

import com.yutong.safety.core.db.entity.spot.SafetySpotLinkRecordRuleDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotLinkRecordRuleDOExample;
import com.yutong.safety.core.db.mapper.spot.SafetySpotLinkRecordRuleDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gwxiexj
 * @since 2024-05-09 16:16
 */
@Service
public class SafetySpotLinkRecordRuleService {

    @Autowired
    private SafetySpotLinkRecordRuleDOMapper safetySpotLinkRecordRuleDOMapper;

    public int save(SafetySpotLinkRecordRuleDO recordRuleDO){
        return safetySpotLinkRecordRuleDOMapper.insert(recordRuleDO);
    }

    public int update(SafetySpotLinkRecordRuleDO recordRuleDO){
        return safetySpotLinkRecordRuleDOMapper.updateByPrimaryKeySelective(recordRuleDO);
    }

    public int updateByExample(SafetySpotLinkRecordRuleDO recordRuleDO, SafetySpotLinkRecordRuleDOExample example){
        return safetySpotLinkRecordRuleDOMapper.updateByExampleSelective(recordRuleDO,example);
    }

    public List<SafetySpotLinkRecordRuleDO> selectList(SafetySpotLinkRecordRuleDOExample example){
        return safetySpotLinkRecordRuleDOMapper.selectByExample(example);
    }

    public Long countByExample(SafetySpotLinkRecordRuleDOExample example){
        return safetySpotLinkRecordRuleDOMapper.countByExample(example);
    }

    public List<SafetySpotLinkRecordRuleDO> getDataByList(List<Integer> lastList){
        return safetySpotLinkRecordRuleDOMapper.getDataByList(lastList);
    }

    public void batchUpdateByList(List<SafetySpotLinkRecordRuleDO> list){
        safetySpotLinkRecordRuleDOMapper.batchUpdateByList(list);
    }


}
