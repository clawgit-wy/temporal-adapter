package com.yutong.safety.core.service.spot;

import com.google.common.collect.Sets;
import com.yutong.safety.core.db.entity.spot.SafetySpotRulePermDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotRulePermDOExample;
import com.yutong.safety.core.db.mapper.spot.SafetySpotRulePermDOBizMapper;
import com.yutong.safety.core.db.mapper.spot.SafetySpotRulePermDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gwcuiyx
 * @date 2024-4-30 13:36
 */
@Service
public class SafetySpotRulePermService {
    @Autowired
    private SafetySpotRulePermDOBizMapper safetySpotRulePermDOBizMapper;

    @Autowired
    private SafetySpotRulePermDOMapper safetySpotRulePermDOMapper;

    /**
     * 添加设备信息管理与设备巡检标准配置关系表内容
     * @param spotId
     * @param ruleIds
     */
    public void insert(Integer spotId, String ruleIds) {
        //添加前应先删除原有数据
        deleteBySpotId(spotId);
        Set<String> ids = Sets.newHashSet(ruleIds.split(","));
        List<Integer> idList = ids.stream().map(Integer::parseInt).collect(Collectors.toList());
        safetySpotRulePermDOBizMapper.batchInsert(spotId, idList);
    }

    /**
     * 根据设备信息表id删除关系表数据
     * @param spotId
     */
    public void deleteBySpotId(Integer spotId) {
        SafetySpotRulePermDOExample example = new SafetySpotRulePermDOExample();
        example.createCriteria().andSpotIdEqualTo(spotId);
        safetySpotRulePermDOBizMapper.deleteByExample(example);
    }

    /**
     * 根据list的id删除中间表数据
     * @param spotList
     */
    public void deleteBySpotList(List<Integer> spotList) {
        SafetySpotRulePermDOExample example = new SafetySpotRulePermDOExample();
        example.createCriteria().andSpotIdIn(spotList);
        safetySpotRulePermDOBizMapper.deleteByExample(example);
    }

    /**
     * 查找指定标准是否被应用
     * @param ruleList
     * @return
     */
    public List<SafetySpotRulePermDO> selectByRule(List<Integer> ruleList){
        SafetySpotRulePermDOExample example = new SafetySpotRulePermDOExample();
        example.createCriteria().andRuleIdIn(ruleList);
        return safetySpotRulePermDOMapper.selectByExample(example);
    }
}
