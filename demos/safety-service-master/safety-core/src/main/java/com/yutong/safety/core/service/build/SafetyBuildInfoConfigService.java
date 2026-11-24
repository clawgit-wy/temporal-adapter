package com.yutong.safety.core.service.build;

import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.core.db.entity.build.SafetyBuildInfoConfigDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildInfoConfigDOExample;
import com.yutong.safety.core.db.mapper.build.SafetyBuildInfoConfigDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
public class SafetyBuildInfoConfigService {
    @Autowired
    SafetyBuildInfoConfigDOMapper safetyBuildInfoConfigDOMapper;

    public List<SafetyBuildInfoConfigDO> query(SafetyBuildInfoConfigDO param) {
        SafetyBuildInfoConfigDOExample e = new SafetyBuildInfoConfigDOExample();
        SafetyBuildInfoConfigDOExample.Criteria criteria = e.createCriteria();

        if(!StringUtils.isBlank(param.getInfoName())){
            criteria.andInfoNameLike(param.getInfoName());
        }

        if(!StringUtils.isBlank(param.getShowFlag())){
            criteria.andShowFlagEqualTo(param.getShowFlag());
        }

        if(param.getId() != null){
            criteria.andIdEqualTo(param.getId());
        }

        e.setOrderByClause(" create_time desc");
        return safetyBuildInfoConfigDOMapper.selectByExample(e);
    }

    /**
     * 根据ID获取消息主体信息
     * @param id ID
     * @return 消息内容
     */
    public String queryById(Integer id) {
        SafetyBuildInfoConfigDO en = new SafetyBuildInfoConfigDO();
        en.setShowFlag("1");
        en.setId(id);
        List<SafetyBuildInfoConfigDO> query = this.query(en);
        if (CollectionUtils.isEmpty(query)) {
            return "";
        } else{
            return query.get(0).getInfoContent();
        }
    }

    public List<SafetyBuildInfoConfigDO> queryList(SafetyBuildInfoConfigDO param) {
        SafetyBuildInfoConfigDOExample e = new SafetyBuildInfoConfigDOExample();
        SafetyBuildInfoConfigDOExample.Criteria criteria = e.createCriteria();

        if(!StringUtils.isBlank(param.getShowFlag())){
            criteria.andShowFlagEqualTo(param.getShowFlag());
        }

        if(param.getId() != null){
            criteria.andIdEqualTo(param.getId());
        }

        e.setOrderByClause(" create_time desc");
        return safetyBuildInfoConfigDOMapper.selectByExample(e);
    }

    public void update(SafetyBuildInfoConfigDO param) {
        param.setCreateTime(DateUtils.format(new Date(),2));
        safetyBuildInfoConfigDOMapper.updateByPrimaryKeySelective(param);
    }
}
