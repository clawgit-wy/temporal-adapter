package com.yutong.safety.core.service;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.web.CardRefundAddParam;
import com.yutong.safety.common.param.web.CardRefundListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.common.utils.SensitiveUtils;
import com.yutong.safety.core.db.entity.SafetyCardRefundDO;
import com.yutong.safety.core.db.entity.SafetyCardRefundDOExample;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.mapper.SafetyCardRefundDOBizMapper;
import com.yutong.safety.core.db.mapper.SafetyCardRefundDOMapper;
import com.yutong.safety.core.dto.SafetyVisitorHairpinDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author gwcuiyx
 * @date 2024-1-18 10:11
 */
@Service
public class SafetyCardRefundService {
    @Autowired
    private SafetyCardRefundDOMapper safetyCardRefundDOMapper;

    @Autowired
    private SafetyVisitorHairpinService safetyVisitorHairpinService;

    @Autowired
    private SafetySysUserService safetySysUserService;

    @Autowired
    private SafetyFieldManageService safetyFieldManageService;

    @Autowired
    private SafetyCardRefundDOBizMapper safetyCardRefundDOBizMapper;

    /**
     * 分页查询
     * @param param
     * @return
     */
    public List<SafetyCardRefundDO> pageList(CardRefundListParam param) {
        SafetyCardRefundDOExample example = new SafetyCardRefundDOExample();
        SafetyCardRefundDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if (StringUtils.isNotEmpty(param.getVisitedDept())) {
            criteria.andVisitedDeptLike("%" + param.getVisitedDept() + "%");
        }
        if (StringUtils.isNotEmpty(param.getHairpinFactory())) {
            criteria.andHairpinFactoryLike("%" + param.getHairpinFactory() + "%");
        }
        if (!ObjectUtils.isEmpty(param.getLoseStartDay())) {
            criteria.andLoseDayGreaterThanOrEqualTo(param.getLoseStartDay());
        }
        if (!ObjectUtils.isEmpty(param.getLoseEndDay())) {
            criteria.andLoseDayLessThanOrEqualTo(param.getLoseEndDay());
        }
        if (StringUtils.isNotEmpty(param.getVisitorName())) {
            criteria.andVisitorNameLike("%" + param.getVisitorName() + "%");
        }
        if (StringUtils.isNotEmpty(param.getVisitorPhone())) {
            criteria.andVisitorPhoneLike("%" + param.getVisitorPhone() + "%");
        }
        if (StringUtils.isNotEmpty(param.getVisitorIdCard())) {
            criteria.andVisitorIdCardLike("%" + param.getVisitorIdCard() + "%");
        }
        if(StringUtils.isNotEmpty(param.getHairpinStartTime())) {
            String start = param.getHairpinStartTime() + " 00:00:00";
            criteria.andHairpinTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
        }
        if(StringUtils.isNotEmpty(param.getHairpinEndTime())) {
            String end = param.getHairpinEndTime() + " 23:59:59";
            criteria.andHairpinTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }
        if (StringUtils.isNotEmpty(param.getUserName())) {
            criteria.andUserNameLike("%" + param.getUserName() + "%");
        }
        example.setOrderByClause(" create_time desc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        List<SafetyCardRefundDO> list = safetyCardRefundDOMapper.selectByExample(example);
        list.forEach(item -> {
            item.setVendorUnit(SensitiveUtils.maskName(item.getVendorUnit()));
            item.setVisitorName(SensitiveUtils.maskName(item.getVisitorName()));
            item.setVisitorPhone(SensitiveUtils.maskPhoneNumber(item.getVisitorPhone()));
            item.setVisitorIdCard(SensitiveUtils.maskCardId(item.getVisitorIdCard()));
        });
        return list;
    }

    /**
     * 分页总条数
     * @param param
     * @return
     */
    public long pageListCount(CardRefundListParam param) {
        SafetyCardRefundDOExample example = new SafetyCardRefundDOExample();
        SafetyCardRefundDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if (StringUtils.isNotEmpty(param.getVisitedDept())) {
            criteria.andVisitedDeptLike("%" + param.getVisitedDept() + "%");
        }
        if (StringUtils.isNotEmpty(param.getHairpinFactory())) {
            criteria.andHairpinFactoryLike("%" + param.getHairpinFactory() + "%");
        }
        if (!ObjectUtils.isEmpty(param.getLoseStartDay())) {
            criteria.andLoseDayGreaterThanOrEqualTo(param.getLoseStartDay());
        }
        if (!ObjectUtils.isEmpty(param.getLoseEndDay())) {
            criteria.andLoseDayLessThanOrEqualTo(param.getLoseEndDay());
        }
        if (StringUtils.isNotEmpty(param.getVisitorName())) {
            criteria.andVisitorNameLike("%" + param.getVisitorName() + "%");
        }
        if (StringUtils.isNotEmpty(param.getVisitorPhone())) {
            criteria.andVisitorPhoneLike("%" + param.getVisitorPhone() + "%");
        }
        if (StringUtils.isNotEmpty(param.getVisitorIdCard())) {
            criteria.andVisitorIdCardLike("%" + param.getVisitorIdCard() + "%");
        }
        if(StringUtils.isNotEmpty(param.getHairpinStartTime())) {
            String start = param.getHairpinStartTime() + " 00:00:00";
            criteria.andHairpinTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
        }
        if(StringUtils.isNotEmpty(param.getHairpinEndTime())) {
            String end = param.getHairpinEndTime() + " 23:59:59";
            criteria.andHairpinTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }
        if (StringUtils.isNotEmpty(param.getUserName())) {
            criteria.andUserNameLike("%" + param.getUserName() + "%");
        }
        return safetyCardRefundDOMapper.countByExample(example);
    }

    /**
     * 添加接口
     * @param visitorId
     */
    public void add(Integer visitorId) {
        //根据前端传值id获取访客数据
        SafetyVisitorHairpinDTO visitorHairpinDO = safetyVisitorHairpinService.view(visitorId);
        //先查下这个卡号是否已存在数据，如果存在，就不添加，直接返回
        if (StringUtils.isEmpty(visitorHairpinDO.getHairpinNo())) {
            return;
        }
        SafetyCardRefundDO safetyCardRefundDO = getCardRefundByNo(visitorHairpinDO.getHairpinNo());
        if (safetyCardRefundDO != null) {
            safetyCardRefundDO.setUpdateTime(new Date());
            safetyCardRefundDO.setLoseDay((int) DateUtils.loseDay(DateUtils.format(visitorHairpinDO.getHairpinTime(), 1), DateUtils.format(new Date(), 1)));
            return;
        }
        safetyCardRefundDO = new SafetyCardRefundDO();
        BeanUtils.copyProperties(visitorHairpinDO, safetyCardRefundDO);
        safetyCardRefundDO.setCreateTime(new Date());
        safetyCardRefundDO.setUpdateTime(new Date());
        safetyCardRefundDO.setDeleteFalg(false);
        //计算丢失天数
        safetyCardRefundDO.setLoseDay((int) DateUtils.loseDay(DateUtils.format(visitorHairpinDO.getHairpinTime(), 1), DateUtils.format(new Date(), 1)));
        //获取当初发卡的人员权限id
        Integer userId = visitorHairpinDO.getUserId();
        String authFactory = null;
        if (!ObjectUtils.isEmpty(userId)) {
            //获取人员
            SafetySysUserDO safetySysUserDO = safetySysUserService.safetySysUserDOById(userId);
            safetyCardRefundDO.setUserName(safetySysUserDO.getEmpName());
            authFactory = safetySysUserDO.getFactoryId();
        }
        if (com.yutong.gw.utils.StringUtils.isNotEmpty(visitorHairpinDO.getFactoryId()) && !ObjectUtils.isEmpty(userId)) {
            safetyCardRefundDO.setHairpinFactory(authFactoryNames(visitorHairpinDO.getFactoryId(), visitorHairpinDO.getVendorFactory(), authFactory));
        }
        PreconditonsEx.checkArgument(safetyCardRefundDOMapper.insert(safetyCardRefundDO) == 1, ResponseEnum.FAIL);
    }

    /**
     * 获取授权的厂区名称
     * @param factoryIds
     * @param userFactoryIds
     * @return
     */
    public String authFactoryNames(String factoryIds, String factoryNames, String userFactoryIds) {
        if (com.yutong.gw.utils.StringUtils.isEmpty(userFactoryIds)) {
            return factoryNames;
        }
        String[] visiteds = factoryIds.split(";");
        List<Integer> visitedList = Arrays.stream(visiteds).map(Integer::parseInt).collect(Collectors.toList());
        String[] users = userFactoryIds.split(",");
        List<Integer> userList = Arrays.stream(users).map(Integer::parseInt).collect(Collectors.toList());
        List<Integer> intersection = visitedList.stream().filter(userList::contains).collect(Collectors.toList());
        return safetyFieldManageService.getNamesByListIds(intersection);
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public List<SafetyCardRefundDO> export(CardRefundListParam param) {
        SafetyCardRefundDOExample example = new SafetyCardRefundDOExample();
        SafetyCardRefundDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if (StringUtils.isNotEmpty(param.getVisitedDept())) {
            criteria.andVisitedDeptLike("%" + param.getVisitedDept() + "%");
        }
        if (StringUtils.isNotEmpty(param.getHairpinFactory())) {
            criteria.andHairpinFactoryLike("%" + param.getHairpinFactory() + "%");
        }
        if (!ObjectUtils.isEmpty(param.getLoseStartDay())) {
            criteria.andLoseDayGreaterThanOrEqualTo(param.getLoseStartDay());
        }
        if (!ObjectUtils.isEmpty(param.getLoseEndDay())) {
            criteria.andLoseDayLessThanOrEqualTo(param.getLoseEndDay());
        }
        if (StringUtils.isNotEmpty(param.getVisitorName())) {
            criteria.andVisitorNameLike("%" + param.getVisitorName() + "%");
        }
        if (StringUtils.isNotEmpty(param.getVisitorPhone())) {
            criteria.andVisitorPhoneLike("%" + param.getVisitorPhone() + "%");
        }
        if (StringUtils.isNotEmpty(param.getVisitorIdCard())) {
            criteria.andVisitorIdCardLike("%" + param.getVisitorIdCard() + "%");
        }
        if(StringUtils.isNotEmpty(param.getHairpinStartTime())) {
            String start = param.getHairpinStartTime() + " 00:00:00";
            criteria.andHairpinTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
        }
        if(StringUtils.isNotEmpty(param.getHairpinEndTime())) {
            String end = param.getHairpinEndTime() + " 23:59:59";
            criteria.andHairpinTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }
        if (StringUtils.isNotEmpty(param.getUserName())) {
            criteria.andUserNameLike("%" + param.getUserName() + "%");
        }
        example.setOrderByClause(" create_time desc ");
        return safetyCardRefundDOMapper.selectByExample(example);
    }

    /**
     * 根据卡号，获取是否已经存在未退卡数据
     * @param hairpinNo
     * @return
     */
    public SafetyCardRefundDO getCardRefundByNo(String hairpinNo) {
        SafetyCardRefundDOExample example = new SafetyCardRefundDOExample();
        example.createCriteria().andDeleteFalgEqualTo(false).andHairpinNoEqualTo(hairpinNo);
        List<SafetyCardRefundDO> list = safetyCardRefundDOMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    /**
     * 发卡和退卡的时候做处理
     * @param hairpinNo
     */
    public void deleteByHairpinNo(String hairpinNo) {
        SafetyCardRefundDO safetyCardRefundDO = getCardRefundByNo(hairpinNo);
        if (safetyCardRefundDO == null) {
            return;
        }
        safetyCardRefundDO.setDeleteFalg(true);
        safetyCardRefundDO.setUpdateTime(new Date());
        PreconditonsEx.checkArgument(safetyCardRefundDOMapper.updateByPrimaryKey(safetyCardRefundDO) == 1, ResponseEnum.FAIL);
    }

    /**
     * 定时任务更新
     */
    public void taskCardRefundDays() {
        safetyCardRefundDOBizMapper.updateLoseDay();
    }
}
