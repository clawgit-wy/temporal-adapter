package com.yutong.safety.core.service.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildAutoDeptDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import com.yutong.safety.core.db.entity.build.param.BuildAutoDeptListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildAutoDeptVO;
import com.yutong.safety.core.db.mapper.build.SafetyBuildAutoDeptDOMapper;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SafetyBuildAutoDeptService {

    @Autowired
    private SafetyBuildAutoDeptDOMapper safetyBuildAutoDeptDOMapper;

    public int batchInsert(List<SafetyBuildAutoDeptDO> list){
        List<List<SafetyBuildAutoDeptDO>> partition = ListUtils.partition(list, 200);
        partition.forEach(li -> safetyBuildAutoDeptDOMapper.batchInsert(li));
        return list.size();
    }

    public long selectCount(BuildAutoDeptListParam listParam){
        return safetyBuildAutoDeptDOMapper.countByExample(listParam);
    }

    public List<BuildAutoDeptVO> selectList(BuildAutoDeptListParam listParam){
        return safetyBuildAutoDeptDOMapper.selectByExample(listParam);
    }

    public int updateByPrimaryKeySelective(SafetyBuildAutoDeptDO deptDO){
        return safetyBuildAutoDeptDOMapper.updateByPrimaryKeySelective(deptDO);
    }

}
