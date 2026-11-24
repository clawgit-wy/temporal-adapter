package com.yutong.safety.core.service;

import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.param.web.RetentionNoticeListParam;
import com.yutong.safety.common.param.web.RetentionNoticeSaveParam;
import com.yutong.safety.common.vo.web.RetentionNoticeVO;
import com.yutong.safety.core.db.entity.SafetyRetentionNoticeDO;
import com.yutong.safety.core.db.entity.SafetyRetentionNoticeDOExample;
import com.yutong.safety.core.db.mapper.SafetyRetentionBizMapper;
import com.yutong.safety.core.db.mapper.SafetyRetentionNoticeDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author gwxiexj
 * @since 2024-04-10 13:27
 */
@Service
public class SafetyRetentionNoticeService {

    @Autowired
    private SafetyRetentionNoticeDOMapper safetyRetentionNoticeDOMapper;

    @Autowired
    private SafetyRetentionBizMapper safetyRetentionBizMapper;

    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    /**
     * 更新滞留通知
     * @param param 入参
     * @return 变更条数
     */
    public int saveOrUpdate(RetentionNoticeSaveParam param){
        int rowNum;
        SafetyRetentionNoticeDO noticeDO = new SafetyRetentionNoticeDO();
        BeanUtils.copyProperties(param,noticeDO);

        if(param.getId() != null){
            noticeDO.setId(param.getId());
            noticeDO.setUpdateBy(param.getUpdateBy());
        }else{
            noticeDO.setCreateBy(param.getUpdateBy());
            noticeDO.setCreateTime(new Date());
        }
        noticeDO.setUpdateTime(new Date());
        if(param.getId() == null){
            rowNum = safetyRetentionNoticeDOMapper.insert(noticeDO);
        }else{
            rowNum = safetyRetentionNoticeDOMapper.updateByPrimaryKey(noticeDO);
        }
        return rowNum;
    }

    /**
     * 查询列表-分页
     * @param param 查询-入参
     * @return 返回结果集
     */
    public List<RetentionNoticeVO> pageList(RetentionNoticeListParam param){
        SafetyRetentionNoticeDOExample example = new SafetyRetentionNoticeDOExample();
        SafetyRetentionNoticeDOExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(param.getCarType())){
            criteria.andCarTypeEqualTo(param.getCarType());
        }
        if(StringUtils.isNotEmpty(param.getRemindType())){
            criteria.andRemindTypeLike(param.getRemindType());
        }
        if(param.getIsUsed() != null){
            criteria.andIsUsedEqualTo(param.getIsUsed());
        }
        example.setOrderByClause(" update_time desc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        return safetyRetentionBizMapper.selectByExample(example);
    }

    /**
     * 分页总条数
     *
     * @param param 查询入参
     * @return 返回
     */
    public long pageListCount(RetentionNoticeListParam param) {
        SafetyRetentionNoticeDOExample example = new SafetyRetentionNoticeDOExample();
        SafetyRetentionNoticeDOExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(param.getCarType())){
            criteria.andCarTypeEqualTo(param.getCarType());
        }
        if(StringUtils.isNotEmpty(param.getRemindType())){
            criteria.andRemindTypeLike(param.getRemindType());
        }
        if(param.getIsUsed() != null){
            criteria.andIsUsedEqualTo(param.getIsUsed());
        }
        return safetyRetentionNoticeDOMapper.countByExample(example);
    }

    /**
     * 获取最新滞留通知规则列表
     * @return 返回结果集
     */
    public List<RetentionNoticeVO> getNoticeRuleList(RetentionNoticeListParam param){
        return safetyRetentionBizMapper.getNoticeRuleList(param);
    }

    /**
     *
     * @param noticeId 消息ID
     * @return 变更条数
     */
    public int deleteById(Integer noticeId){
        int rowNum = 0;
        if(noticeId != null){
            rowNum = safetyRetentionNoticeDOMapper.deleteByPrimaryKey(noticeId);
        }
        return rowNum;
    }
}
