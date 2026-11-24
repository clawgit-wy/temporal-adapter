package com.yutong.safety.core.service.park;

import com.yutong.safety.core.db.entity.park.SafetyParkPowerProcessDO;
import com.yutong.safety.core.db.entity.park.SafetyParkPowerProcessDetailDO;
import com.yutong.safety.core.db.entity.park.SafetyParkPowerProcessDetailDOExample;
import com.yutong.safety.core.db.mapper.park.SafetyParkPowerProcessDOBizMapper;
import com.yutong.safety.core.db.mapper.park.SafetyParkPowerProcessDetailDOBizMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SafetyParkPowerProcessDetailService {
    @Autowired
    SafetyParkPowerProcessDetailDOBizMapper safetyParkPowerProcessDetailDOBizMapper;

    public Integer save(SafetyParkPowerProcessDetailDO process) {
        return safetyParkPowerProcessDetailDOBizMapper.insert(process);
    }

    public void update(SafetyParkPowerProcessDetailDO process) {
        safetyParkPowerProcessDetailDOBizMapper.updateByPrimaryKeySelective(process);
    }

    public void batchSave(List<SafetyParkPowerProcessDetailDO> list) {
        safetyParkPowerProcessDetailDOBizMapper.batchSave(list);
    }

    public void updateByProId(SafetyParkPowerProcessDO en) {
        safetyParkPowerProcessDetailDOBizMapper.updateByProId(en.getId());
    }

    public List<SafetyParkPowerProcessDetailDO> select(String processId) {
        SafetyParkPowerProcessDetailDOExample example = new SafetyParkPowerProcessDetailDOExample();
        example.createCriteria().andProcessIdEqualTo(processId);

        return safetyParkPowerProcessDetailDOBizMapper.selectByExample(example);
    }

}
