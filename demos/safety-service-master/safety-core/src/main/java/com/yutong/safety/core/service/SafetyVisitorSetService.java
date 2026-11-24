package com.yutong.safety.core.service;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.web.VisitorSetAddParam;
import com.yutong.safety.common.param.web.VisitorSetDelParam;
import com.yutong.safety.common.param.web.VisitorSetListParam;
import com.yutong.safety.common.param.web.VisitorSetUpdateParam;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.core.db.entity.SafetyVisitorSetDO;
import com.yutong.safety.core.db.entity.SafetyVisitorSetDOExample;
import com.yutong.safety.core.db.mapper.SafetyVisitorSetDOMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-4-2 17:03
 */
@Service
public class SafetyVisitorSetService {

    @Autowired
    private SafetyVisitorSetDOMapper safetyVisitorSetDOMapper;

    /**
     * 分页数据查询
     * @param param
     * @return
     */
    public List<SafetyVisitorSetDO> pageList(VisitorSetListParam param) {
        SafetyVisitorSetDOExample example = new SafetyVisitorSetDOExample();
        SafetyVisitorSetDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if (StringUtils.isNotEmpty(param.getPositionType())) {
            criteria.andPositionTypeEqualTo(param.getPositionType());
        }
        if (StringUtils.isNotEmpty(param.getPositionName())) {
            criteria.andPositionNameEqualTo(param.getPositionName());
        }
        if (Objects.nonNull(param.getVisitorState())) {
            criteria.andVisitorStateEqualTo(param.getVisitorState());
        }
        if (StringUtils.isNotEmpty(param.getRemark())) {
            criteria.andRemarkLike("%"+param.getRemark()+"%");
        }
        example.setOrderByClause(" update_time desc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        return safetyVisitorSetDOMapper.selectByExample(example);
    }

    public long pageListCount(VisitorSetListParam param) {
        SafetyVisitorSetDOExample example = new SafetyVisitorSetDOExample();
        SafetyVisitorSetDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if (StringUtils.isNotEmpty(param.getPositionType())) {
            criteria.andPositionTypeEqualTo(param.getPositionType());
        }
        if (StringUtils.isNotEmpty(param.getPositionName())) {
            criteria.andPositionNameEqualTo(param.getPositionName());
        }
        if (Objects.nonNull(param.getVisitorState())) {
            criteria.andVisitorStateEqualTo(param.getVisitorState());
        }
        if (StringUtils.isNotEmpty(param.getRemark())) {
            criteria.andRemarkLike("%"+param.getRemark()+"%");
        }
        return safetyVisitorSetDOMapper.countByExample(example);
    }

    public void add(VisitorSetAddParam param) {
        SafetyVisitorSetDO safetyVisitorSetDO = new SafetyVisitorSetDO();
        safetyVisitorSetDO.setCreateTime(new Date());
        safetyVisitorSetDO.setUpdateTime(new Date());
        safetyVisitorSetDO.setDeleteFalg(false);
        BeanUtils.copyProperties(param, safetyVisitorSetDO);
        PreconditonsEx.checkArgument(safetyVisitorSetDOMapper.insert(safetyVisitorSetDO) == 1, ResponseEnum.FAIL);
    }

    public void update(VisitorSetUpdateParam param) {
        SafetyVisitorSetDO safetyVisitorSetDO = get(param.getId());
        safetyVisitorSetDO.setUpdateTime(new Date());
        BeanUtils.copyProperties(param, safetyVisitorSetDO);
        PreconditonsEx.checkArgument(safetyVisitorSetDOMapper.updateByPrimaryKey(safetyVisitorSetDO) == 1, ResponseEnum.FAIL);
    }

    public SafetyVisitorSetDO get(Integer id) {
        return safetyVisitorSetDOMapper.selectByPrimaryKey(id);
    }

    public void delete(VisitorSetDelParam param) {
        SafetyVisitorSetDO safetyVisitorSetDO = get(param.getId());
        safetyVisitorSetDO.setUpdateTime(new Date());
        safetyVisitorSetDO.setDeleteFalg(true);
        PreconditonsEx.checkArgument(safetyVisitorSetDOMapper.updateByPrimaryKey(safetyVisitorSetDO) == 1, ResponseEnum.FAIL);
    }

    /**
     * 根据类型来获取数据
     * @param positionType
     * @return
     */
    public List<SafetyVisitorSetDO> checkVisitorSet(String positionType) {
        SafetyVisitorSetDOExample example = new SafetyVisitorSetDOExample();
        if (StringUtils.isEmpty(positionType)) {
            example.createCriteria().andDeleteFalgEqualTo(false).andPositionTypeEqualTo("空");
        } else {
            example.createCriteria().andDeleteFalgEqualTo(false).andPositionTypeEqualTo(positionType);
        }
        example.setOrderByClause("priority ASC");
        List<SafetyVisitorSetDO> list = safetyVisitorSetDOMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? null : list;
    }


}
