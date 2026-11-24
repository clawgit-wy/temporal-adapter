package com.yutong.safety.core.service.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildConfigDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildConfigDOExample;
import com.yutong.safety.core.db.mapper.build.SafetyBuildConfigDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SafetyBuildConfigService {

    @Autowired
    private SafetyBuildConfigDOMapper safetyBuildConfigDOMapper;

    public List<SafetyBuildConfigDO> query() {
        SafetyBuildConfigDOExample example = new SafetyBuildConfigDOExample();
        SafetyBuildConfigDOExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeleteEqualTo("0");
        example.setOrderByClause(" id asc ");
        return safetyBuildConfigDOMapper.selectByExample(example);
    }

    public void saveOrUpdate(SafetyBuildConfigDO param) {
        Integer id = param.getId();
        if(null != id){
            safetyBuildConfigDOMapper.updateByPrimaryKeySelective(param);
        }else{
            safetyBuildConfigDOMapper.insertSelective(param);
        }
    }

    public SafetyBuildConfigDO queryForFirst() {
        SafetyBuildConfigDOExample example = new SafetyBuildConfigDOExample();
        SafetyBuildConfigDOExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeleteEqualTo("0");

        List<SafetyBuildConfigDO> list = safetyBuildConfigDOMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(list)){
            return list.get(0);
        }
        return null;
    }
}
