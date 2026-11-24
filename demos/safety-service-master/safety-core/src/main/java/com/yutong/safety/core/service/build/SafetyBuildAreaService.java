package com.yutong.safety.core.service.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildAreaDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildAreaDOExample;
import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceDO;
import com.yutong.safety.core.db.entity.build.param.BuildAreaListParam;
import com.yutong.safety.core.db.mapper.build.SafetyBuildAreaDOBizMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SafetyBuildAreaService {

    @Autowired
    private SafetyBuildAreaDOBizMapper safetyBuildAreaDOBizMapper;

    /**
     * 批量更新
     * @param buildAreaDOS 入参集合
     * @return 返回值
     */
    public int batchUpdate(List<SafetyBuildAreaDO> buildAreaDOS){
        if(CollectionUtils.isEmpty(buildAreaDOS)){
            return 0;
        }
        return safetyBuildAreaDOBizMapper.batchUpdate(buildAreaDOS);
    }

    public List<SafetyBuildAreaDO> selectFactoryList(){
        return safetyBuildAreaDOBizMapper.selectFactoryList();
    }

    public List<SafetyBuildAreaDO> selectList(BuildAreaListParam listParam){
        SafetyBuildAreaDOExample areaDOExample = new SafetyBuildAreaDOExample();
        SafetyBuildAreaDOExample.Criteria  criteria = areaDOExample.createCriteria();
        if(listParam.getAreaId() != null){
            criteria.andAreaIdEqualTo(listParam.getAreaId());
        }
        if(listParam.getPageFlag()){
            areaDOExample.setLimit(listParam.getLimit());
            areaDOExample.setOffset(listParam.getOffset());
        }
        return safetyBuildAreaDOBizMapper.selectByExample(areaDOExample);
    }
}
