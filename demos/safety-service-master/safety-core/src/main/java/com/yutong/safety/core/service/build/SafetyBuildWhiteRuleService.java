package com.yutong.safety.core.service.build;

import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.util.StringUtils;
import com.google.common.collect.Sets;
import com.yutong.safety.core.db.entity.build.SafetyBuildWhiteRuleDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildWhiteRuleDOExample;
import com.yutong.safety.core.db.entity.build.param.BuildWhiteRuleListParam;
import com.yutong.safety.core.db.mapper.build.SafetyBuildWhiteRuleDOMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SafetyBuildWhiteRuleService {

    @Resource
    private SafetyBuildWhiteRuleDOMapper safetyBuildWhiteRuleDOMapper;

    public int save(SafetyBuildWhiteRuleDO buildWhiteRuleDO){
        String curTime = DateUtils.format(new Date());
        buildWhiteRuleDO.setUpdateTime(curTime);
        buildWhiteRuleDO.setUpdateUser(buildWhiteRuleDO.getCreateUser());
        buildWhiteRuleDO.setCreateTime(curTime);
        buildWhiteRuleDO.setDeleteFlag("0");
        return safetyBuildWhiteRuleDOMapper.insert(buildWhiteRuleDO);
    }

    /**
     * 批量删除
     * @param ids ID拼接串
     * @return
     */
    public int deleteById(String ids,String updateUser){
        if(StringUtils.isBlank(ids)){
            return 0;
        }
        Set<String> idsSet = Sets.newHashSet(ids.split(","));
        List<Integer> idList = idsSet.stream().map(Integer::parseInt).collect(Collectors.toList());
        SafetyBuildWhiteRuleDOExample example = new SafetyBuildWhiteRuleDOExample();
        example.createCriteria().andDeleteFlagEqualTo("0").andIdIn(idList);
        SafetyBuildWhiteRuleDO ruleDO = new SafetyBuildWhiteRuleDO();
        ruleDO.setDeleteFlag("1"); //标记删除
        ruleDO.setUpdateUser(updateUser);
        ruleDO.setUpdateTime(DateUtils.format(new Date()));
        return safetyBuildWhiteRuleDOMapper.updateByExampleSelective(ruleDO,example);
    }

    public List<SafetyBuildWhiteRuleDO> selectList(BuildWhiteRuleListParam listParam){
        SafetyBuildWhiteRuleDOExample example = formatExample(listParam);
        return safetyBuildWhiteRuleDOMapper.selectByExample(example);
    }

    public Long selectCount(BuildWhiteRuleListParam listParam) {
        listParam.setPageFlag(false);
        SafetyBuildWhiteRuleDOExample example = formatExample(listParam);
        return safetyBuildWhiteRuleDOMapper.countByExample(example);
    }

    private SafetyBuildWhiteRuleDOExample formatExample(BuildWhiteRuleListParam listParam){
        SafetyBuildWhiteRuleDOExample example = new SafetyBuildWhiteRuleDOExample();
        SafetyBuildWhiteRuleDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFlagEqualTo("0");
        if(StringUtils.isNotBlank(listParam.getIdStr())){
            Set<String> idsSet = Sets.newHashSet(listParam.getIdStr().split(","));
            List<Integer> idList = idsSet.stream().map(Integer::parseInt).collect(Collectors.toList());
            criteria.andIdIn(idList);
        }
        if(StringUtils.isNotBlank(listParam.getSearchName())){
            example.setDiyQuery(" (post_name like '%"+listParam.getSearchName()+"%' or org_name_path like '%"+listParam.getSearchName()+"%') ");
        }
        if(StringUtils.isBlank(listParam.getOrderByClause())){
            example.setOrderByClause(" create_time  desc ");
        }
        if(listParam.getPageFlag() != null && listParam.getPageFlag()){
            example.setLimit(listParam.getLimit());
            example.setOffset(listParam.getOffset());
        }
        return example;
    }

    public SafetyBuildWhiteRuleDO getById(Integer id){
        if(id == null){
            return null;
        }
        return safetyBuildWhiteRuleDOMapper.selectByPrimaryKey(id);
    }
}
