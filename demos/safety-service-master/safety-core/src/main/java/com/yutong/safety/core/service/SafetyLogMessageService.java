package com.yutong.safety.core.service;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.param.web.LogMessageListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.common.utils.SensitiveUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.SafetyLogMessageDO;
import com.yutong.safety.core.db.entity.SafetyLogMessageDOExample;
import com.yutong.safety.core.db.mapper.SafetyLogMessageDOMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  create gwcuiyx 2023.07.13
 *  日志管理--service
 */
@Service
public class SafetyLogMessageService {

    @Autowired
    private SafetyLogMessageDOMapper safetyLogMessageDOMapper;
    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    /**
     * 数据列表查询
     * @param param
     * @return
     */
    public List<SafetyLogMessageDO> pageList(LogMessageListParam param) {
        SafetyLogMessageDOExample example = new SafetyLogMessageDOExample();
        SafetyLogMessageDOExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(param.getOperatePage())) {
            criteria.andOperatePageLike("%"+param.getOperatePage()+"%");
        }
        if(StringUtils.isNotEmpty(param.getOperator())) {
            criteria.andOperatorLike("%"+param.getOperator()+"%");
        }
        if(StringUtils.isNotEmpty(param.getStartTime())) {
            String start = param.getStartTime() + " 0:0:0";
            criteria.andCreateTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
        }
        if(StringUtils.isNotEmpty(param.getEndTime())) {
            String end = param.getEndTime() + " 23:59:59";
            criteria.andCreateTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }
        if (StringUtils.isNotEmpty(param.getMessage())) {
            criteria.andMessageLike("%" + param.getMessage() + "%");
        }
        example.setOrderByClause(" create_time desc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        List<SafetyLogMessageDO> list = safetyLogMessageDOMapper.selectByExample(example);
        list.forEach(item -> {
            if ("访客发卡".equals(item.getOperatePage())) {
                item.setMessage(SensitiveUtils.maskLogData(item.getMessage()));
            }
        });
        return list;
    }

    /**
     * 总数据量
     * @param param
     * @return
     */
    public long pageListCount(LogMessageListParam param) {
        SafetyLogMessageDOExample example = new SafetyLogMessageDOExample();
        SafetyLogMessageDOExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(param.getOperatePage())) {
            criteria.andOperatePageLike("%"+param.getOperatePage()+"%");
        }
        if(StringUtils.isNotEmpty(param.getOperator())) {
            criteria.andOperatorLike("%"+param.getOperator()+"%");
        }
        if(StringUtils.isNotEmpty(param.getStartTime())) {
            String start = param.getStartTime() + " 0:0:0";
            criteria.andCreateTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
        }
        if(StringUtils.isNotEmpty(param.getEndTime())) {
            String end = param.getEndTime() + " 23:59:59";
            criteria.andCreateTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }
        if (StringUtils.isNotEmpty(param.getMessage())) {
            criteria.andMessageLike("%" + param.getMessage() + "%");
        }
        return safetyLogMessageDOMapper.countByExample(example);
    }

    /**
     * 数据导出功能
     * @param param
     * @return
     */
    public List<SafetyLogMessageDO> export(LogMessageListParam param, String tocken) {
        SafetyLogMessageDOExample example = new SafetyLogMessageDOExample();
        SafetyLogMessageDOExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(param.getOperatePage())) {
            criteria.andOperatePageLike("%"+param.getOperatePage()+"%");
        }
        if(StringUtils.isNotEmpty(param.getOperator())) {
            criteria.andOperatorLike("%"+param.getOperator()+"%");
        }
        if(StringUtils.isNotEmpty(param.getStartTime())) {
            String start = param.getStartTime() + " 0:0:0";
            criteria.andCreateTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
        }
        if(StringUtils.isNotEmpty(param.getEndTime())) {
            String end = param.getEndTime() + " 23:59:59";
            criteria.andCreateTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }
        if (StringUtils.isNotEmpty(param.getMessage())) {
            criteria.andMessageLike("%" + param.getMessage() + "%");
        }
        example.setOrderByClause(" create_time desc ");
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("日志管理");
        log.setAbility("导出");
        log.setMessage("导出");
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        return safetyLogMessageDOMapper.selectByExample(example);
    }

    /**
     * 数据添加功能
     * @param param
     */
    public void addLogMessage(LogMessageAddParam param) {
        SafetyLogMessageDO safetyLogMessageDO = new SafetyLogMessageDO();
        BeanUtils.copyProperties(param, safetyLogMessageDO);
        safetyLogMessageDO.setCreateTime(new Date());
        PreconditonsEx.checkArgument(safetyLogMessageDOMapper.insert(safetyLogMessageDO) == 1, ResponseEnum.FAIL);
    }

}
