package com.yutong.safety.core.service.park;

import com.yutong.safety.core.db.entity.park.SafetyParkPowerRuleDO;
import com.yutong.safety.core.db.entity.park.param.ParkPowerRuleAddParam;
import com.yutong.safety.core.db.entity.park.param.ParkPowerRuleListParam;
import com.yutong.safety.core.db.entity.park.vo.ParkPowerRuleVO;
import com.yutong.safety.core.db.mapper.park.SafetyParkPowerRuleDOBizMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SafetyParkPowerRuleService {

    @Autowired
    private SafetyParkPowerRuleDOBizMapper safetyParkPowerRuleDOMapper;

    public int insert(ParkPowerRuleAddParam addParam){
        SafetyParkPowerRuleDO safetyParkPowerRuleDO = new SafetyParkPowerRuleDO();
        BeanUtils.copyProperties(addParam,safetyParkPowerRuleDO);
        safetyParkPowerRuleDO.setCreateTime(new Date());
        safetyParkPowerRuleDO.setUpdateTime(new Date());
        return safetyParkPowerRuleDOMapper.insert(safetyParkPowerRuleDO);
    }

    public int update(ParkPowerRuleAddParam addParam){
        SafetyParkPowerRuleDO safetyParkPowerRuleDO = new SafetyParkPowerRuleDO();
        BeanUtils.copyProperties(addParam,safetyParkPowerRuleDO);
        safetyParkPowerRuleDO.setUpdateTime(new Date());
        return safetyParkPowerRuleDOMapper.updateByPrimaryKeySelective(safetyParkPowerRuleDO);
    }


    public int updateBatchByIdList(ParkPowerRuleAddParam addParam,List<String> idList){
        SafetyParkPowerRuleDO safetyParkPowerRuleDO = new SafetyParkPowerRuleDO();
        BeanUtils.copyProperties(addParam,safetyParkPowerRuleDO);
        safetyParkPowerRuleDO.setUpdateTime(new Date());
        return safetyParkPowerRuleDOMapper.updateBatchByIdList(safetyParkPowerRuleDO,idList);
    }

    public List<ParkPowerRuleVO> selectList(ParkPowerRuleListParam param){
        param.setPageFlag(false);
        return safetyParkPowerRuleDOMapper.selectList(param);
    }

    public long selectCount(ParkPowerRuleListParam param){
        return safetyParkPowerRuleDOMapper.selectCount(param);
    }

    public List<ParkPowerRuleVO> selectPageList(ParkPowerRuleListParam param){
        param.setPageFlag(true);
        param.setOrderByClause(" sppr.priority asc,sppr.update_time desc");
        return safetyParkPowerRuleDOMapper.selectList(param);
    }

    /**
     * 返回优先级最高的一条，priority值越小优先级越高
     * @param empNo 员工号
     * @return 返回结果
     */
    public SafetyParkPowerRuleDO getFirstRuleByEmpNo(String empNo){
        if(StringUtils.isBlank(empNo)){
            return null;
        }
        List<SafetyParkPowerRuleDO> ruleList = safetyParkPowerRuleDOMapper.selectRuleByEmpNo(empNo);
        if(CollectionUtils.isEmpty(ruleList)){
           return null;
        }
        return ruleList.get(0);
    }

    /**
     * 获取指定员工匹配的规则-返回优先级最高的集合
     * @param empList
     * @return
     */
    public List<ParkPowerRuleVO> selectRulesForEmp(List<String> empList){
        if(CollectionUtils.isEmpty(empList)){
            return Collections.emptyList();
        }
        List<ParkPowerRuleVO> list = safetyParkPowerRuleDOMapper.selectRulesForEmp(empList);
        if(CollectionUtils.isEmpty(list)){
            return Collections.emptyList();
        }
        return list.stream()
                .collect(Collectors.groupingBy(
                        ParkPowerRuleVO::getEmpNo,
                        Collectors.minBy(Comparator.comparingInt(ParkPowerRuleVO::getPriority))
                ))
                .values()
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
