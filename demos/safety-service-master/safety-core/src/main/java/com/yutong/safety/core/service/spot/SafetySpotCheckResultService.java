package com.yutong.safety.core.service.spot;

import com.yutong.safety.common.param.web.spot.SpotCheckResultParam;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckResultDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckResultDOExample;
import com.yutong.safety.core.db.mapper.spot.SafetySpotCheckResultDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SafetySpotCheckResultService {
    @Autowired
    private SafetySpotCheckResultDOMapper safetySpotCheckResultDOMapper;

    /**
     * 分页查询
     *
     * @param param 查询入参
     * @return 返回
     */
    public List<SafetySpotCheckResultDO> pageList(SpotCheckResultParam param) {
        SafetySpotCheckResultDOExample example = new SafetySpotCheckResultDOExample();
        SafetySpotCheckResultDOExample.Criteria criteria = example.createCriteria();
        if(param.getRecordId() != null){
            criteria.andRecordIdEqualTo(param.getRecordId());
        }
        example.setOrderByClause("  reform_time desc");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        return safetySpotCheckResultDOMapper.selectByExample(example);
    }

    /**
     * 分页总条数
     *
     * @param param 查询入参
     * @return 返回
     */
    public long pageListCount(SpotCheckResultParam param) {
        SafetySpotCheckResultDOExample example = new SafetySpotCheckResultDOExample();
        SafetySpotCheckResultDOExample.Criteria criteria = example.createCriteria();
        if(param.getRecordId() != null){
            criteria.andRecordIdEqualTo(param.getRecordId());
        }
        return safetySpotCheckResultDOMapper.countByExample(example);
    }

    /**
     * 查询-不分页
     *
     * @param param 查询入参
     * @return 返回
     */
    public List<SafetySpotCheckResultDO> list(SpotCheckResultParam param) {
        SafetySpotCheckResultDOExample example = new SafetySpotCheckResultDOExample();
        SafetySpotCheckResultDOExample.Criteria criteria = example.createCriteria();
        if(param.getRecordId() != null){
            criteria.andRecordIdEqualTo(param.getRecordId());
        }
        example.setOrderByClause("  reform_time desc");
        return safetySpotCheckResultDOMapper.selectByExample(example);
    }

}
