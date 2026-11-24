package com.yutong.safety.core.service;

import com.yutong.safety.common.param.web.LaneFactoryListParam;
import com.yutong.safety.core.db.entity.SafetyLaneFactoryDOExample;
import com.yutong.safety.core.db.entity.SafetyLaneFactoryDOKey;
import com.yutong.safety.core.db.mapper.SafetyLaneFactoryDOMapper;
import com.yutong.safety.core.db.mapper.SafetyRetentionBizMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gwxiexj
 * @since 2024-04-12 15:11
 */
@Service
public class SafetyLaneFactoryService {

    @Autowired
    private SafetyLaneFactoryDOMapper safetyLaneFactoryDOMapper;

    @Autowired
    private SafetyRetentionBizMapper safetyRetentionBizMapper;

    /**
     * 查询列表-分页
     * @param param 查询-入参
     * @return 返回结果集
     */
    public List<SafetyLaneFactoryDOKey> pageList(LaneFactoryListParam param) throws Exception{
        SafetyLaneFactoryDOExample example = new SafetyLaneFactoryDOExample();
        SafetyLaneFactoryDOExample.Criteria criteria = example.createCriteria();

        if(StringUtils.isNotEmpty(param.getFactoryName())){
            criteria.andFactoryNameEqualTo(param.getFactoryName());
        }
        if(StringUtils.isNotEmpty(param.getLaneName())){
            criteria.andLaneNameLike(param.getLaneName());
        }

        example.setOrderByClause(" factory_name desc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        return safetyLaneFactoryDOMapper.selectByExample(example);
    }

    /**
     * 分页总条数
     *
     * @param param 查询入参
     * @return 返回
     */
    public long pageListCount(LaneFactoryListParam param) {
        SafetyLaneFactoryDOExample example = new SafetyLaneFactoryDOExample();
        SafetyLaneFactoryDOExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(param.getFactoryName())){
            criteria.andFactoryNameEqualTo(param.getFactoryName());
        }
        if(StringUtils.isNotEmpty(param.getLaneName())){
            criteria.andLaneNameLike(param.getLaneName());
        }
        return safetyLaneFactoryDOMapper.countByExample(example);
    }


    /**
     * 查询列表
     * @param param 查询-入参
     * @return 返回结果集
     */
    public List<SafetyLaneFactoryDOKey> getList(LaneFactoryListParam param) throws Exception{
        SafetyLaneFactoryDOExample example = new SafetyLaneFactoryDOExample();
        SafetyLaneFactoryDOExample.Criteria criteria = example.createCriteria();

        if(StringUtils.isNotEmpty(param.getFactoryName())){
            criteria.andFactoryNameEqualTo(param.getFactoryName());
        }
        if(StringUtils.isNotEmpty(param.getLaneName())){
            criteria.andLaneNameLike(param.getLaneName());
        }

        example.setOrderByClause(" factory_name desc ");
        return safetyLaneFactoryDOMapper.selectByExample(example);
    }

    /**
     * 获取厂区里列表
     * @param param
     * @return
     * @throws Exception
     */
    public List<String> getFactoryList(LaneFactoryListParam param) throws Exception{
        SafetyLaneFactoryDOExample example = new SafetyLaneFactoryDOExample();
        SafetyLaneFactoryDOExample.Criteria criteria = example.createCriteria();

        if(StringUtils.isNotEmpty(param.getFactoryName())){
            criteria.andFactoryNameEqualTo(param.getFactoryName());
        }
        if(StringUtils.isNotEmpty(param.getLaneName())){
            criteria.andLaneNameLike(param.getLaneName());
        }
        return safetyRetentionBizMapper.selectFactoryList(example);
    }
}
