package com.yutong.safety.core.service.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildInOutHistoryDO;
import com.yutong.safety.core.db.mapper.build.SafetyBuildInOutHistoryDOBizMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SafetyBuildInOutHistoryService {

    @Autowired
    private SafetyBuildInOutHistoryDOBizMapper safetyBuildInOutHistoryDOMapper;

    public int batchUpdate(List<SafetyBuildInOutHistoryDO> inOutHistoryDOList){
        if(CollectionUtils.isEmpty(inOutHistoryDOList)){
            return 0;
        }
        return safetyBuildInOutHistoryDOMapper.batchUpdate(inOutHistoryDOList);
    }

    public Long getMaxHistoryId(){
        return safetyBuildInOutHistoryDOMapper.getMaxHistoryId();
    }
}
