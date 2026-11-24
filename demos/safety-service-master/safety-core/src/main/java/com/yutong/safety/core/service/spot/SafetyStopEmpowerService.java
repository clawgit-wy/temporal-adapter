package com.yutong.safety.core.service.spot;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.param.web.StopEmpowerListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.SensitiveUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.SafetyStopEmpowerDO;
import com.yutong.safety.core.db.entity.SafetyStopEmpowerDOExample;
import com.yutong.safety.core.db.mapper.SafetyStopEmpowerDOMapper;
import com.yutong.safety.core.service.SafetyLogMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *  create gwcuiyx 2023.07.18
 *  停车场授权记录--service
 */
@Service
public class SafetyStopEmpowerService {
    @Autowired
    private SafetyStopEmpowerDOMapper safetyStopEmpowerDOMapper;
    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    /**
     * 数据分页列表查询
     * @param param
     * @return
     */
    public List<SafetyStopEmpowerDO> pageList(StopEmpowerListParam param) {
        SafetyStopEmpowerDOExample example = new SafetyStopEmpowerDOExample();
        SafetyStopEmpowerDOExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(param.getVisitorName())) {
            criteria.andVisitorNameLike("%"+param.getVisitorName()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVisitorPhone())) {
            criteria.andVisitorPhoneLike("%"+param.getVisitorPhone()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVisitorIdCard())) {
            criteria.andVisitorIdCardLike("%"+param.getVisitorIdCard()+"%");
        }
        if(StringUtils.isNotEmpty(param.getEmpowerNo())) {
            criteria.andEmpowerNoLike("%"+param.getEmpowerNo()+"%");
        }
        if(StringUtils.isNotEmpty(param.getEmpowerFactory())) {
            criteria.andEmpowerFactoryLike("%"+param.getEmpowerFactory()+"%");
        }
        if(StringUtils.isNotEmpty(param.getCardNo())) {
            criteria.andCardNoLike("%"+param.getCardNo()+"%");
        }
        if(StringUtils.isNotEmpty(param.getCheckStartTime())) {
            String start = param.getCheckStartTime() + " 0:0:0";
            criteria.andRegisterTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
        }
        if(StringUtils.isNotEmpty(param.getCheckEndTime())) {
            String end = param.getCheckEndTime() + " 23:59:59";
            criteria.andRegisterTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }
        if(StringUtils.isNotEmpty(param.getPowerSartTime())) {
            String start = param.getPowerSartTime() + " 0:0:0";
            criteria.andStartTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
        }
        if(StringUtils.isNotEmpty(param.getPowerEndTime())) {
            String end = param.getPowerEndTime() + " 23:59:59";
            criteria.andEndTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }
        example.setOrderByClause(" register_time desc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        List<SafetyStopEmpowerDO> list = safetyStopEmpowerDOMapper.selectByExample(example);
        list.forEach(item -> {
            item.setVisitorIdCard(SensitiveUtils.maskCardId(item.getVisitorIdCard()));
            item.setVisitorName(SensitiveUtils.maskName(item.getVisitorName()));
            item.setVisitorPhone(SensitiveUtils.maskPhoneNumber(item.getVisitorPhone()));
        });
        return list;
    }

    /**
     * 总数据量
     * @param param
     * @return
     */
    public long pageListCount(StopEmpowerListParam param) {
        SafetyStopEmpowerDOExample example = new SafetyStopEmpowerDOExample();
        SafetyStopEmpowerDOExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(param.getVisitorName())) {
            criteria.andVisitorNameLike("%"+param.getVisitorName()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVisitorPhone())) {
            criteria.andVisitorPhoneLike("%"+param.getVisitorPhone()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVisitorIdCard())) {
            criteria.andVisitorIdCardLike("%"+param.getVisitorIdCard()+"%");
        }
        if(StringUtils.isNotEmpty(param.getEmpowerNo())) {
            criteria.andEmpowerNoLike("%"+param.getEmpowerNo()+"%");
        }
        if(StringUtils.isNotEmpty(param.getEmpowerFactory())) {
            criteria.andEmpowerFactoryLike("%"+param.getEmpowerFactory()+"%");
        }
        if(StringUtils.isNotEmpty(param.getCardNo())) {
            criteria.andCardNoLike("%"+param.getCardNo()+"%");
        }
        if(StringUtils.isNotEmpty(param.getCheckStartTime())) {
            String start = param.getCheckStartTime() + " 0:0:0";
            criteria.andRegisterTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
        }
        if(StringUtils.isNotEmpty(param.getCheckEndTime())) {
            String end = param.getCheckEndTime() + " 23:59:59";
            criteria.andRegisterTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }
        if(StringUtils.isNotEmpty(param.getPowerSartTime())) {
            String start = param.getPowerSartTime() + " 0:0:0";
            criteria.andStartTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
        }
        if(StringUtils.isNotEmpty(param.getPowerEndTime())) {
            String end = param.getPowerEndTime() + " 23:59:59";
            criteria.andEndTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }
        return safetyStopEmpowerDOMapper.countByExample(example);
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public List<SafetyStopEmpowerDO> export(StopEmpowerListParam param, String tocken) {
        SafetyStopEmpowerDOExample example = new SafetyStopEmpowerDOExample();
        SafetyStopEmpowerDOExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(param.getVisitorName())) {
            criteria.andVisitorNameLike("%"+param.getVisitorName()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVisitorPhone())) {
            criteria.andVisitorPhoneLike("%"+param.getVisitorPhone()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVisitorIdCard())) {
            criteria.andVisitorIdCardLike("%"+param.getVisitorIdCard()+"%");
        }
        if(StringUtils.isNotEmpty(param.getEmpowerNo())) {
            criteria.andEmpowerNoLike("%"+param.getEmpowerNo()+"%");
        }
        if(StringUtils.isNotEmpty(param.getEmpowerFactory())) {
            criteria.andEmpowerFactoryLike("%"+param.getEmpowerFactory()+"%");
        }
        if(StringUtils.isNotEmpty(param.getCardNo())) {
            criteria.andCardNoLike("%"+param.getCardNo()+"%");
        }
        if(StringUtils.isNotEmpty(param.getCheckStartTime())) {
            String start = param.getCheckStartTime() + " 0:0:0";
            criteria.andRegisterTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
        }
        if(StringUtils.isNotEmpty(param.getCheckEndTime())) {
            String end = param.getCheckEndTime() + " 23:59:59";
            criteria.andRegisterTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }
        if(StringUtils.isNotEmpty(param.getPowerSartTime())) {
            String start = param.getPowerSartTime() + " 0:0:0";
            criteria.andStartTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
        }
        if(StringUtils.isNotEmpty(param.getPowerEndTime())) {
            String end = param.getPowerEndTime() + " 23:59:59";
            criteria.andEndTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }

        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("停车场授权记录");
        log.setAbility("导出");
        log.setMessage("导出");
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        return safetyStopEmpowerDOMapper.selectByExample(example);
    }

    /**
     * 数据添加功能
     * @param safetyStopEmpowerDO
     */
    public void add(SafetyStopEmpowerDO safetyStopEmpowerDO) {
        safetyStopEmpowerDOMapper.insert(safetyStopEmpowerDO);
    }
}
